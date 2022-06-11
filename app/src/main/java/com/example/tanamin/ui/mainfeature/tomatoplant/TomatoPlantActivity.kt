 package com.example.tanamin.ui.mainfeature.tomatoplant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityPlantsPredictionBinding
import com.example.tanamin.databinding.ActivityTomatoPlantBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.data.Classification
import com.example.tanamin.nonui.response.ClassificationsResponse
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.response.TomatoDiseaseResponse
import com.example.tanamin.nonui.response.UploadFileResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.mainfeature.camerautil.reduceFileImage
import com.example.tanamin.ui.mainfeature.camerautil.rotateBitmap
import com.example.tanamin.ui.mainfeature.camerautil.uriToFile
import com.example.tanamin.ui.mainfeature.plantsprediction.CameraPlantsPredictionActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivity
import com.example.tanamin.ui.mainfeature.plantsprediction.PlantsPredictionActivityViewModel
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class TomatoPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTomatoPlantBinding
    private lateinit var viewModel: TomatoPlantActivityViewModel
    private var mFile: File? = null
    private lateinit var token: String
    private lateinit var refreshToken: String

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTomatoPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //CHECKING CAMERA PERMISSION
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupModel()

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }


        //HANDLING BACKBUTTON
        supportActionBar?.hide()
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnHelp.setOnClickListener {
            help()
        }
    }
    private fun help(){
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.item_help_bottomsheet,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        bottomSheetView.findViewById<View>(R.id.btn_close).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }

    //HANDLING ONBACKPRESSED FOR THE BACKBUTTON


    private fun startCameraX() {
        val intent = Intent(this, CameraPlantsPredictionActivity::class.java)
        launcherIntentCameraX.launch(intent)
        refreshTokenin()
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            mFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(mFile?.path),
                isBackCamera
            )
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
        refreshTokenin()
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@TomatoPlantActivity)
            mFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    //TO GET THE TOKEN
    private fun setupModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[TomatoPlantActivityViewModel::class.java]

        viewModel.getToken().observe(this) { userToken ->
            token = userToken
        }

        viewModel.getRefreshToken().observe(this){ userRefreshToken ->
            refreshToken = userRefreshToken
        }
    }

    private fun uploadImage(){
        logd(mFile.toString())
        if(mFile != null) {
            val file = reduceFileImage(mFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "data",
                file.name,
                requestImageFile
            )

            val service = ApiConfig.getApiService().uploadPhoto(imageMultipart)

            service.enqueue(object : Callback<UploadFileResponse> {
                override fun onResponse(
                    call: Call<UploadFileResponse>,
                    response: Response<UploadFileResponse>
                ) {
                    showLoading(true)
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if(responseBody != null){
                            logd("THEBIGINNING " + responseBody.toString())
                            logd("Another thebiginning " + responseBody.data.toString())
                            tomatoDisease(responseBody.data.pictureUrl)
                        }
                    }else{
                        logd("ngeselin ${response.toString()}")
                        val responseBody = response.body()
                        if(responseBody != null){
                            logd("ngeselin lah ini ${responseBody.status}")
                        }
                    }
                }
                override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                    logd("Retrofit Failed")
                }
            })
        } else {
            logd("Input Image first")
        }
    }

    //THIS FUNCTION IS TO SEND THE LINK PLUS THE ENDPOINT TO THE SERVER WITH ITS TOKEN TO GET THE PREDICTION
    private fun tomatoDisease(theUrl: String){
        val endpoint = "9197643464764817408"
        val userToken = "Bearer $token"

        logd("UserToken: $userToken")
        logd("imageURL: $theUrl")
        logd("endpoint: $endpoint")
        val service = ApiConfig.getApiService().getTomatoDisease(userToken, theUrl, endpoint)
        service.enqueue(object : Callback<TomatoDiseaseResponse>{
            override fun onResponse(
                call: Call<TomatoDiseaseResponse>,
                response: Response<TomatoDiseaseResponse>
            ) {
                logd(response.body()?.data.toString())
                val responseBody = response.body()
                if (responseBody != null) {
                    logd("PREDICTION CHECKER: ${responseBody.data}")
                    prepareToSendResult(responseBody)
                }else{
                    showFailed()
                    logd("Respones Message ${response.message()}")
                }
            }
            override fun onFailure(call: Call<TomatoDiseaseResponse>, t: Throwable) {
                logd("Checking Failed")
                showFailed()
            }
        })
    }

    //TO REFRESH THE TOKEN
    private fun refreshTokenin(){
        val service = ApiConfig.getApiService().getRefreshedToken(refreshToken)
        service.enqueue(object: Callback<RefreshTokenResponse>{
            override fun onResponse(
                call: Call<RefreshTokenResponse>,
                response: Response<RefreshTokenResponse>
            ) {
                val responseBody = response.body()
                if(response.isSuccessful){
                    viewModel.saveToken(responseBody?.data!!.accessToken)
                }else{
                    logd("data yang di ambil itu ${responseBody?.message}")
                }
            }

            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                Log.d(this@TomatoPlantActivity.toString(), "${t.message}")
            }

        })
    }

    private fun prepareToSendResult(PlantsDiseases: TomatoDiseaseResponse){
        val resultData = com.example.tanamin.nonui.data.PlantsDiseases(
            "${PlantsDiseases.data.result.createdAt}",
            "${PlantsDiseases.data.result.diseasesName}",
            "${PlantsDiseases.data.result.historyId}",
            "${PlantsDiseases.data.result.imageUrl}",
            PlantsDiseases.data.result.accuracy,
            "${PlantsDiseases.data.result.description}",
            "${PlantsDiseases.data.result.plantName}"
        )
        logd("This is the result of classification $resultData")
        val intentTomatoPlantDetailResultActivity = Intent(this@TomatoPlantActivity, TomatoPlantDetailResultActivity::class.java)
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_RESULT, resultData)
        startActivity(intentTomatoPlantDetailResultActivity)
    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@TomatoPlantActivity.toString(), "$msg")
    }
    private fun showLoading(b: Boolean){
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.item_bottomsheet_upload,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        bottomSheetView.findViewById<View>(R.id.btn_close).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()

    }
    private fun showFailed(){

        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.item_upload_failed,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
        bottomSheetView.findViewById<View>(R.id.btn_tryagain).setOnClickListener {
            startActivity(Intent(this, TomatoPlantActivity::class.java))
        }
    }

}