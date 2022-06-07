package com.example.tanamin.ui.mainfeature.plantsprediction

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.tanamin.databinding.ActivityPlantsPredictionBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.ClassificationsResponse
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.response.UploadFileResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.mainfeature.camerautil.reduceFileImage
import com.example.tanamin.ui.mainfeature.camerautil.rotateBitmap
import com.example.tanamin.ui.mainfeature.camerautil.uriToFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import java.io.File
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Url

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

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }


        //HANDLING BACKBUTTON
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
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

    //HANDLING BACKPRESSEN ON THE BACKBUTTON
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
            val service = ApiConfig.getApiService().uploadPhoto(imageMultipart)

            service.enqueue(object : Callback<UploadFileResponse>{
                override fun onResponse(
                    call: Call<UploadFileResponse>,
                    response: Response<UploadFileResponse>
                ) {
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if(responseBody != null){
                            plantsPrediction(responseBody.data.toString())
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

        //GETTING JUST THE LINK BY PARSING
        val beforeParsedUrl: String = theUrl
        val firstArray: List<String> = beforeParsedUrl.split("=")
        val beforeSecondParsing: String = firstArray[1]

        //this one is to erase the ')'
        val secondArray: List<String> = beforeSecondParsing.split(")")
        val url = secondArray[0]

        logd("UserToken di sending data: $userToken")

        val service = ApiConfig.getApiService().getVegetableClassification(userToken, url, endpoint)
        service.enqueue(object : Callback<ClassificationsResponse>{
            override fun onResponse(
                call: Call<ClassificationsResponse>,
                response: Response<ClassificationsResponse>
            ) {
                logd(response.body()?.data.toString())
                val responseBody = response.body()
                if (responseBody != null) {
                    logd("PREDICTION CHECKER: ${responseBody.data}")
                }else{
                    logd("Respones Message ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ClassificationsResponse>, t: Throwable) {
                logd("Checking Failed")
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
            logd("Token sebelum diubah di fungsi setup model $token")
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
                    logd("Token sesudah diubah $token")
                    logd("Ini token yang di get dari viewmodel ${token}")
                    Log.d(this@PlantsPredictionActivity.toString(), "onResponse: ${responseBody?.data!!.accessToken}")
                }else{
                    logd("data yang di ambil itu ${responseBody?.message}")
                }
            }

            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                Log.d(this@PlantsPredictionActivity.toString(), "${t.message}")
            }

        })
    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@PlantsPredictionActivity.toString(), "$msg")
    }
}