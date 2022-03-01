package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class TeamsWinProbability (

	@SerializedName("homeTeamShortName") var homeTeamShortName : String,
	@SerializedName("homeTeamPercentage") var homeTeamPercentage : String,
	@SerializedName("awayTeamShortName") var awayTeamShortName : String,
	@SerializedName("awayTeamPercentage") var awayTeamPercentage : String,
	@SerializedName("tiePercentage") var tiePercentage : String

)