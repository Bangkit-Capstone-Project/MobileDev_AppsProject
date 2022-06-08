package com.example.tanamin.nonui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val createdAt: String,
    val diseasesName: String,
    val diseasesDescription: String,
    val imageUrl: String,
    val accuracy: String,
    val id: String,
    val plantName: String
): Parcelable