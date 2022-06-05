package com.example.tanamin.nonui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Diseases(
    val imageUrl: String,
    val name: String,
    val description: String,
    val id: String
): Parcelable