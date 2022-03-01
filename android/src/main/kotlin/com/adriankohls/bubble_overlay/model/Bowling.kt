package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class Bowling (

	@SerializedName("matchID") var matchID : String,
	@SerializedName("playerFeedID") var playerFeedID : String,
	@SerializedName("playerName") var playerName : String,
	@SerializedName("playerTeam") var playerTeam : String,
	@SerializedName("wickets") var wickets : String,
	@SerializedName("maiden") var maiden : String,
	@SerializedName("RunsConceeded") var RunsConceeded : String,
	@SerializedName("overs") var overs : String,
	@SerializedName("economy") var economy : String

)