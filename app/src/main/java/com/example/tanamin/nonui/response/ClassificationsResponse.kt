package com.example.tanamin.nonui.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ClassificationsResponse(

	@field:SerializedName("data")
	val data: DataResult,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataResult(

	@field:SerializedName("result")
	val result: ResultPlant
)

data class ResultPlant(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("vegetableName")
	val vegetableName: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("accuracy")
	val accuracy: String,

	@field:SerializedName("description")
	val description: String
)
