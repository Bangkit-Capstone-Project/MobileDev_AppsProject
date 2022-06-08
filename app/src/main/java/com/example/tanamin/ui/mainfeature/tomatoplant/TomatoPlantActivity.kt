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
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity
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

        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.uploadButton.setOnClickListener { uploadImage() }


        //HANDLING BACKBUTTON
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    //HANDLING ONBACKPRESSED FOR THE BACKBUTTON
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
                    if(response.isSuccessful){
                        val responseBody = response.body()
                        if(responseBody != null){
                            logd("THEBIGINNING " + responseBody.toString())
                            logd("Another thebiginning " + responseBody.data.toString())
                            tomatoDisease(responseBody.data.toString())
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

        //GETTING JUST THE LINK BY PARSING
        val beforeParsedUrl: String = theUrl
        val firstArray: List<String> = beforeParsedUrl.split("=")
        val beforeSecondParsing: String = firstArray[1]

        //this one is to erase the ')'
        val secondArray: List<String> = beforeSecondParsing.split(")")
        val url = secondArray[0]

        logd("UserToken: $userToken")
        logd("imageURL: $url")
        logd("endpoint: $endpoint")
        val service = ApiConfig.getApiService().getTomatoDisease(userToken, url, endpoint)
        service.enqueue(object : Callback<TomatoDiseaseResponse>{
            override fun onResponse(
                call: Call<TomatoDiseaseResponse>,
                response: Response<TomatoDiseaseResponse>
            ) {
                logd(response.body()?.data.toString())
                val responseBody = response.body()
                if (responseBody != null) {
                    logd("PREDICTION CHECKER: ${responseBody.data}")
                    prepareToSendData(responseBody.data.toString())

                }else{
                    logd("Respones Message ${response.message()}")
                }
            }
            override fun onFailure(call: Call<TomatoDiseaseResponse>, t: Throwable) {
                logd("Checking Failed")
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

    //UNTUK MEMPERSIAPKAN DATA YANG DAPAT INTENT KAN KE DISPLAY ACTIVITY DAN NGESEND DATANYA :)
    private fun prepareToSendData(theData: String){
        /*
        Karena ada 7 data yang mau di send (createdAt, diseasesName, historyId, imageUrl, accuracy, description, plantName)
        kita buat parsing untuk setiap tujug tujuhnya. Kemudian, karena setiap datanya itu memiliki kesamaan
        dalam struktur, kita parsing dari belakang :)
         */
        val intentTomatoPlantDetailResultActivity = Intent(this@TomatoPlantActivity, TomatoPlantDetailResultActivity::class.java)

        //UNTUK PLANT NAME
        val firstArrayplantName: List<String> = theData.split("))")
        val secondParsingDescription: String = firstArrayplantName[0]
        val secondArrayPlantName: List<String> = secondParsingDescription.split(", plantName=")
        val thePlantName: String = secondArrayPlantName[1]
        logd("thePlantName: $thePlantName")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_PLANTNAME, thePlantName)

        //UNTUK DESCRIPTIONS
        val prepareDescription: String = secondArrayPlantName[0]
        val firstArrayDescription : List<String> = prepareDescription.split(", description=")
        val theDescription: String = firstArrayDescription[1]
        logd("theDescription: $theDescription")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_DESCRIPTION, theDescription)


        //UNTUK ACCURACY
        val prepareAccuracy: String = firstArrayDescription[0]
        val firstArrayAccuracy: List<String> = prepareAccuracy.split(", accuracy=")
        val theAccuracy: String = firstArrayAccuracy[1]
        logd("theAccuracy: $theAccuracy")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_ACCURACY, theAccuracy)


        //UNTUK IMAGEURL
        val prepareImageUrl: String = firstArrayAccuracy[0]
        val firstArrayImageUrl: List<String> = prepareImageUrl.split(", imageUrl=")
        val theImageUrl: String = firstArrayImageUrl[1]
        logd("theImageUrl: $theImageUrl")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_IMAGEURL, theImageUrl)


        //UNTUK HISTORY_ID
        val prepareHistoryId: String = firstArrayImageUrl[0]
        val firstArrayHistoryId: List<String> = prepareHistoryId.split(", historyId=")
        val theHistoryId: String = firstArrayHistoryId[1]
        logd("theHistoryId: $theHistoryId")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_HISTORYID, theHistoryId)


        //UNTUK DISEASE NAME
        val prepareDiseaseName: String = firstArrayHistoryId[0]
        val firstArrayDiseaseName: List<String> = prepareDiseaseName.split(", diseasesName=")
        val theDiseaseName: String = firstArrayDiseaseName[1]
        logd("theDiseaseName: $theDiseaseName")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_DISEASENAME, theDiseaseName)


        //UNTUK WAKTU PEMBUATAN
        val prepareCreatedAt: String = firstArrayDiseaseName[0]
        val firstArrayCreatedAt: List<String> = prepareCreatedAt.split("DataTomato(result=ResultTomato(createdAt=")
        val theCreatedAt: String = firstArrayCreatedAt[1]
        logd("theCreatedAt: $theCreatedAt")
        intentTomatoPlantDetailResultActivity.putExtra(TomatoPlantDetailResultActivity.EXTRA_CREATEDAT, theCreatedAt)

        startActivity(intentTomatoPlantDetailResultActivity)



    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@TomatoPlantActivity.toString(), "$msg")
    }

}