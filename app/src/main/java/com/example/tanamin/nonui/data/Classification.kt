package com.example.tanamin.nonui.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Classification(
    val createdAt: String,
    val vegetableName: String,
    val imageUrl: String,
    val accuracy: String,
    val description: String
        ): Parcelable