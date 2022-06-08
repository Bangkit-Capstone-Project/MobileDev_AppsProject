package com.example.tanamin.nonui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class History(
    val createdAt: String,
    val imageUrl: String,
    val plantId: String,
    val accuracy: String,
    val id: String,
    val diseaseId: String
): Parcelable