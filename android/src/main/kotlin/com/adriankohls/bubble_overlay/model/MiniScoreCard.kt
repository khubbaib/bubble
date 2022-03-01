package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class MiniScoreCard (

	@SerializedName("isDisplayDugout") var isDisplayDugout : Boolean,
	@SerializedName("batting") var batting : List<Batting>,
	@SerializedName("bowling") var bowling : List<Bowling>,
	@SerializedName("partnership") var partnership : String,
	@SerializedName("oversRemaining") var oversRemaining : String,
	@SerializedName("reviewDetails") var reviewDetails : List<ReviewDetails>,
	@SerializedName("runRate") var runRate : String,
	@SerializedName("rRunRate") var rRunRate : String,
	@SerializedName("data") var data : List<Data>

)