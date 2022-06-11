package com.example.tanamin.nonui.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlantsDiseases(
    val createdAt: String,

    val diseasesName: String,

    val historyId: String,

    val imageUrl: String,

    val accuracy: Double,

    val description: String,

    val plantName: String
):Parcelable
