package com.shino72.location.utils

import android.location.Location

data class LocationState(
    val data: Location? = null,
    val error: String = "",
    val isLoading: Boolean = false
)