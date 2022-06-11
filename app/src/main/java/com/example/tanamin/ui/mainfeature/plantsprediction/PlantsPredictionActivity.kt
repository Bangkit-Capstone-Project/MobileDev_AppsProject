package com.example.tanamin.ui.mainfeature.plantsprediction

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.R
import com.example.tanamin.databinding.ActivityPlantsPredictionBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.data.History
import com.example.tanamin.nonui.response.ClassificationsResponse
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.response.ResultPlant
import com.example.tanamin.nonui.response.UploadFileResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.mainfeature.camerautil.reduceFileImage
import com.example.tanamin.ui.mainfeature.camerautil.rotateBitmap
import com.example.tanamin.ui.mainfeature.camerautil.uriToFile
import com.example.tanamin.ui.mainfeature.plantsprediction.result.PlantsPredictionDetailResultActivity
import com.example.tanamin.ui.mainfeature.riceplant.RicePlantActivity
import com.example.tanamin.ui.welcomingpage.WelcomingPageActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import com.example.tanamin.nonui.data.Classification as Classification1

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PlantsPredictionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlantsPredictionBinding
    private lateinit var viewModel: PlantsPredictionActivityViewModel
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
        binding = ActivityPlantsPredictionBinding.inflate(layoutInflater)
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
        binding.btnHelp.setOnClickListener { help() }
        binding.btnBack.setOnClickListener { onBackPressed() }
    }
    private fun help(){
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(R.layout.item_help_vegetable,
            findViewById<LinearLayout>(R.id.bottomSheet)
        )
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

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
            val myFile = uriToFile(selectedImg, this@PlantsPredictionActivity)
            mFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    //THIS FUNCTION IS TO SEND IMAGE TO THE SERVER AND RETURN THE IMAGE LINK FROM THE SERVER
    private fun uploadImage(){
        if(mFile != null){
            val file = reduceFileImage(mFile as File)
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "data",
                file.name,
                requestImageFile
            )
            showLoading(true)
            val service = ApiConfig.getApiService().uploadPhoto(imageMultipart)

            service.enqueue(object : Callback<UploadFileResponse>{
                override fun onResponse(
                    call: Call<UploadFileResponse>,
                    response: Response<UploadFileResponse>
                ) {
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if(responseBody != null){
                            plantsPrediction(responseBody.data.pictureUrl)
                        }
                    }else{
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


    //THIS FUNCTION IS TO SEND THE LINK PLUS THE ENDPOINT TO THE SERVER TO GET THE PREDICTION
    private fun plantsPrediction(theUrl: String){
        val endpoint = "5666821356906348544"
        val userToken = "Bearer $token"
        val service = ApiConfig.getApiService().getVegetableClassification(userToken, theUrl, endpoint)

        service.enqueue(object : Callback<ClassificationsResponse>{
            override fun onResponse(
                call: Call<ClassificationsResponse>,
                response: Response<ClassificationsResponse>
            ) {
                logd(response.body()?.data.toString())
                val responseBody = response.body()
                if (responseBody != null) {
                    prepareToSendResutl(responseBody)
                }else{
                    showFailed()
                    logd("Respones Message ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ClassificationsResponse>, t: Throwable) {
                logd("Checking Failed")
                showFailed()
            }
        })
    }

    //TO GET THE TOKEN
    private fun setupModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[PlantsPredictionActivityViewModel::class.java]

        viewModel.getToken().observe(this) { userToken ->
            token = userToken
        }

        viewModel.getRefreshToken().observe(this){ userRefreshToken ->
            refreshToken = userRefreshToken
        }
    }

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
                Log.d(this@PlantsPredictionActivity.toString(), "${t.message}")
            }

        })
    }

    private fun prepareToSendResutl(Classification: ClassificationsResponse){

        val resultData = Classification1(
            "${Classification.data.result.createdAt}",
            "${Classification.data.result.vegetableName}",
            "${Classification.data.result.imageUrl}",
            "${Classification.data.result.accuracy}",
            "${Classification.data.result.description}",
        )
        val intentPlantsPredictionDetailResultActivity = Intent(this@PlantsPredictionActivity, PlantsPredictionDetailResultActivity::class.java)

        intentPlantsPredictionDetailResultActivity.putExtra(
            PlantsPredictionDetailResultActivity.EXTRA_RESULT,
            resultData
        )
        startActivity(intentPlantsPredictionDetailResultActivity)
    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@PlantsPredictionActivity.toString(), "$msg")
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
            startActivity(Intent(this, PlantsPredictionActivity::class.java))
            finish()
        }
    }
}