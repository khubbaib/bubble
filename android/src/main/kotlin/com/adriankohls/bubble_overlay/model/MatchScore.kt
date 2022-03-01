package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class MatchScore (

	@SerializedName("teamShortName") var teamShortName : String,
	@SerializedName("teamID") var teamID : String,
	@SerializedName("teamFullName") var teamFullName : String,
	@SerializedName("teamScore") var teamScore : List<TeamScore>

)