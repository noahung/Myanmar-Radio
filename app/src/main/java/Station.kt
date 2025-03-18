package com.noahaung.myanmarradio

import java.io.Serializable

data class Station(
    val name: String,
    val streamUrl: String,
    val imageUrl: String
) : Serializable