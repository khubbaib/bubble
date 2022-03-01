package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class ReviewDetails (

	@SerializedName("teamName") var teamName : String,
	@SerializedName("review") var review : String

)