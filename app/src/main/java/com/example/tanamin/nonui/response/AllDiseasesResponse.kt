package com.example.tanamin.nonui.response

import com.google.gson.annotations.SerializedName

data class AllDiseasesResponse(

	@field:SerializedName("data")
	val dataDisease: DataDisease,

	@field:SerializedName("status")
	val status: String
)

data class DataDisease(

	@field:SerializedName("diseases")
	val diseases: List<DiseasesItem>
)

data class DiseasesItem(

	@field:SerializedName("imageUrl")
	val imageUrl: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("id")
	val id: String
)
