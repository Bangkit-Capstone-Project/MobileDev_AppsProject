package com.example.tanamin.ui.mainfeature.riceplant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
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
import com.example.tanamin.databinding.ActivityRicePlantBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.response.RiceDiseaseResponse
import com.example.tanamin.nonui.response.TomatoDiseaseResponse
import com.example.tanamin.nonui.response.UploadFileResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.mainfeature.camerautil.reduceFileImage
import com.example.tanamin.ui.mainfeature.camerautil.rotateBitmap
import com.example.tanamin.ui.mainfeature.camerautil.uriToFile
import com.example.tanamin.ui.mainfeature.riceplant.result.RicePlantDetailResultActivity
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

class RicePlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRicePlantBinding
    private lateinit var viewModel: RicePlantActivityViewModel
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
        binding = ActivityRicePlantBinding.inflate(layoutInflater)
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
        supportActionBar?.hide()

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }
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

    private fun startCameraX() {
        val intent = Intent(this, CameraRiceActivity::class.java)
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
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
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
            val myFile = uriToFile(selectedImg, this@RicePlantActivity)
            mFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    //THIS FUNCTION IS TO SEND IMAGE TO THE SERVER AND RETURN THE IMAGE LINK FROM THE SERVER
    private fun uploadImage(){
        logd(mFile.toString())
        if(mFile != null){
            val file = reduceFileImage(mFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "data",
                file.name,
                requestImageFile
            )
            val service = ApiConfig.getApiService().uploadPhoto(imageMultipart)
            showLoading(true)

            service.enqueue(object : Callback<UploadFileResponse> {
                override fun onResponse(
                    call: Call<UploadFileResponse>,
                    response: Response<UploadFileResponse>
                ) {

                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if(responseBody != null){
                            riceDiseasePrediction(responseBody.data.pictureUrl)
                        }
                    }else{
                        val responseBody = response.body()
                        if(responseBody != null){
                            logd("bismillah lah ini ${responseBody.status}")
                        }
                    }
                }
                override fun onFailure(call: Call<UploadFileResponse>, t: Throwable) {
                    logd("Retrofit Failed")
                    showLoading(false)
                }
            })
        } else {
            logd("Input Image first")
        }
    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@RicePlantActivity.toString(), "$msg")
    }

    //THIS FUNCTION IS TO SEND THE LINK PLUS THE ENDPOINT TO THE SERVER TO GET THE PREDICTION
    private fun riceDiseasePrediction(theUrl: String){
        val endpoint = "2528938316535955456"
        val userToken = "Bearer $token"
        val service = ApiConfig.getApiService().getRiceDisease(userToken, theUrl, endpoint)

        service.enqueue(object : Callback<RiceDiseaseResponse>{
            override fun onResponse(
                call: Call<RiceDiseaseResponse>,
                response: Response<RiceDiseaseResponse>
            ) {
                logd(response.body()?.data.toString())
                val responseBody = response.body()
                if (responseBody != null) {
                    prepareToSendResult(responseBody)
                }else{
                    showFailed()
                    logd("Respones Message ${response.message()}")
                }
            }
            override fun onFailure(call: Call<RiceDiseaseResponse>, t: Throwable) {
                logd("Checking Failed")
                showFailed()
            }
        })
    }

    //TO GET THE TOKEN AND THE REFRESH TOKEN KEY
    private fun setupModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[RicePlantActivityViewModel::class.java]

        viewModel.getToken().observe(this) { userToken ->
            token = userToken
        }

        viewModel.getRefreshToken().observe(this){ userRefreshToken ->
            refreshToken = userRefreshToken
        }
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
                Log.d(this@RicePlantActivity.toString(), "${t.message}")
            }
        })
    }

    private fun prepareToSendResult(PlantsDiseases: RiceDiseaseResponse){
        val resultData = com.example.tanamin.nonui.data.PlantsDiseases(
            "${PlantsDiseases.data.result.createdAt}",
            "${PlantsDiseases.data.result.diseasesName}",
            "${PlantsDiseases.data.result.historyId}",
            "${PlantsDiseases.data.result.imageUrl}",
            PlantsDiseases.data.result.accuracy,
            "${PlantsDiseases.data.result.description}",
            "${PlantsDiseases.data.result.plantName}"
        )
        val intentRicePlantDetailResultActivity = Intent(this@RicePlantActivity, RicePlantDetailResultActivity::class.java)
        intentRicePlantDetailResultActivity.putExtra(RicePlantDetailResultActivity.EXTRA_RESULT, resultData)
        startActivity(intentRicePlantDetailResultActivity)
    }

    private fun showLoading(b: Boolean){
        val bottomSheetDialog = BottomSheetDialog(this@RicePlantActivity, R.style.BottomSheetDialogTheme)
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
            startActivity(Intent(this, RicePlantActivity::class.java))
        }
    }
}