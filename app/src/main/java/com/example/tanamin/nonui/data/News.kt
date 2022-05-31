package com.example.tanamin.nonui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class News(
    val imgItem: Int,
    val newsTittle: String,
    val newsDate: String,
    val newsDesc: String
): Parcelable
