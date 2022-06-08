package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("data")
	val data: DataHistory,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataHistory(

	@field:SerializedName("predictionHistorys")
	val predictionHistorys: List<PredictionHistorysItem>
)

data class PredictionHistorysItem(

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("plantId")
	val plantId: String,

	@field:SerializedName("accuracy")
	val accuracy: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("diseaseId")
	val diseaseId: String
)
