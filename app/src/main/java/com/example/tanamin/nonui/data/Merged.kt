package com.example.tanamin.nonui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Merged(
    val createdAt: String,
    val diseasesName: String,
    val imageUrl: String,
    val accuracy: String,
    val description: String,
    val plantName:String
):Parcelable
