package com.example.tanamin.ui.mainfeature.casavaplant

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
import com.example.tanamin.databinding.ActivityCassavaPlantBinding
import com.example.tanamin.nonui.api.ApiConfig
import com.example.tanamin.nonui.response.CassavaDiseaseResponse
import com.example.tanamin.nonui.response.ClassificationsResponse
import com.example.tanamin.nonui.response.RefreshTokenResponse
import com.example.tanamin.nonui.response.UploadFileResponse
import com.example.tanamin.nonui.userpreference.UserPreferences
import com.example.tanamin.ui.ViewModelFactory
import com.example.tanamin.ui.mainfeature.camerautil.reduceFileImage
import com.example.tanamin.ui.mainfeature.camerautil.rotateBitmap
import com.example.tanamin.ui.mainfeature.camerautil.uriToFile
import com.example.tanamin.ui.mainfeature.casavaplant.result.CassavaPlantDetailResultActivity
import com.example.tanamin.ui.mainfeature.tomatoplant.result.TomatoPlantDetailResultActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class CassavaPlantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCassavaPlantBinding
    private lateinit var viewModel: CassavaPlantActivityViewModel
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
        binding = ActivityCassavaPlantBinding.inflate(layoutInflater)

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

        //Handling Backbutton
        val actionbar = supportActionBar
        actionbar!!.title = "TANAMIN"
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraCassavaPlantActivity::class.java)
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
            val myFile = uriToFile(selectedImg, this@CassavaPlantActivity)
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
                            cassavaDiseasePrediction(responseBody.data.toString())
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

    //THIS FUNCTION IS TO SEND THE LINK PLUS THE ENDPOINT TO THE SERVER TO GET THE PREDICTION
    private fun cassavaDiseasePrediction(theUrl: String){
        val endpoint = "4257194673539383296"
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

        val service = ApiConfig.getApiService().getCassavaDisease(userToken, url, endpoint)
        service.enqueue(object : Callback<CassavaDiseaseResponse>{
            override fun onResponse(
                call: Call<CassavaDiseaseResponse>,
                response: Response<CassavaDiseaseResponse>
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
            override fun onFailure(call: Call<CassavaDiseaseResponse>, t: Throwable) {
                logd("Checking Failed")
            }
        })
    }

    //TO GET THE TOKEN AND THE REFRESH TOKEN KEY
    private fun setupModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[CassavaPlantActivityViewModel::class.java]

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
                    logd("Token sesudah diubah $token")
                    logd("Ini token yang di get dari viewmodel ${token}")
                    Log.d(this@CassavaPlantActivity.toString(), "onResponse: ${responseBody?.data!!.accessToken}")
                }else{
                    logd("data yang di ambil itu ${responseBody?.message}")
                }
            }

            override fun onFailure(call: Call<RefreshTokenResponse>, t: Throwable) {
                Log.d(this@CassavaPlantActivity.toString(), "${t.message}")
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
        val intentCassavaPlantDetailResultActivity = Intent(this@CassavaPlantActivity, CassavaPlantDetailResultActivity::class.java)

        //UNTUK PLANT NAME
        val firstArrayplantName: List<String> = theData.split("))")
        val secondParsingDescription: String = firstArrayplantName[0]
        val secondArrayPlantName: List<String> = secondParsingDescription.split(", plantName=")
        val thePlantName: String = secondArrayPlantName[1]
        logd("thePlantName: $thePlantName")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_PLANTNAME, thePlantName)

        //UNTUK DESCRIPTIONS
        val prepareDescription: String = secondArrayPlantName[0]
        val firstArrayDescription : List<String> = prepareDescription.split(", description=")
        val theDescription: String = firstArrayDescription[1]
        logd("theDescription: $theDescription")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_DESCRIPTION, theDescription)


        //UNTUK ACCURACY
        val prepareAccuracy: String = firstArrayDescription[0]
        val firstArrayAccuracy: List<String> = prepareAccuracy.split(", accuracy=")
        val theAccuracy: String = firstArrayAccuracy[1]
        logd("theAccuracy: $theAccuracy")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_ACCURACY, theAccuracy)


        //UNTUK IMAGEURL
        val prepareImageUrl: String = firstArrayAccuracy[0]
        val firstArrayImageUrl: List<String> = prepareImageUrl.split(", imageUrl=")
        val theImageUrl: String = firstArrayImageUrl[1]
        logd("theImageUrl: $theImageUrl")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_IMAGEURL, theImageUrl)


        //UNTUK HISTORY_ID
        val prepareHistoryId: String = firstArrayImageUrl[0]
        val firstArrayHistoryId: List<String> = prepareHistoryId.split(", historyId=")
        val theHistoryId: String = firstArrayHistoryId[1]
        logd("theHistoryId: $theHistoryId")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_HISTORYID, theHistoryId)


        //UNTUK DISEASE NAME
        val prepareDiseaseName: String = firstArrayHistoryId[0]
        val firstArrayDiseaseName: List<String> = prepareDiseaseName.split(", diseasesName=")
        val theDiseaseName: String = firstArrayDiseaseName[1]
        logd("theDiseaseName: $theDiseaseName")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_DISEASENAME, theDiseaseName)


        //UNTUK WAKTU PEMBUATAN
        val prepareCreatedAt: String = firstArrayDiseaseName[0]
        val firstArrayCreatedAt: List<String> = prepareCreatedAt.split("DataCassava(result=ResultCassava(createdAt=")
        val theCreatedAt: String = firstArrayCreatedAt[1]
        logd("theCreatedAt: $theCreatedAt")
        intentCassavaPlantDetailResultActivity.putExtra(CassavaPlantDetailResultActivity.EXTRA_CREATEDAT, theCreatedAt)

        startActivity(intentCassavaPlantDetailResultActivity)



    }

    //THIS FUNCTION IS FOR DEBUGGING :)
    private fun logd(msg: String) {
        Log.d(this@CassavaPlantActivity.toString(), "$msg")
    }

    //Handling onBackPressed for the Backbutton
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}