package com.example.tanamin.nonui.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class AllDeseaseResponse(
	val data: Data,
	val status: String,
	val diseases: ArrayList<DiseasesItem>
) {

	@Parcelize
	data class DiseasesItem(
		val imageUrl: String,
		val name: String,
		val description: String,
		val id: String
	) : Parcelable
}

