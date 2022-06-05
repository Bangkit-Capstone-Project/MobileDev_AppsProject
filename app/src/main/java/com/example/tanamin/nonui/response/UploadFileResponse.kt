package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class UploadFileResponse(

	@field:SerializedName("data")
	val data: DataPicture,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataPicture(
	@field:SerializedName("pictureUrl")
	val pictureUrl: String
)
