package com.klmn.napp.model

import android.os.Parcelable
import com.klmn.napp.data.network.OpenFoodFactsAPI
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filter(
    val criterion: String,
    val value: String,
    val contains: Boolean = true
) : Parcelable
