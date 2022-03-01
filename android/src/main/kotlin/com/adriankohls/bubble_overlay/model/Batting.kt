package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class Batting (

	@SerializedName("matchID") var matchID : String,
	@SerializedName("playerFeedID") var playerFeedID : String,
	@SerializedName("playerName") var playerName : String,
	@SerializedName("playerTeam") var playerTeam : String,
	@SerializedName("sixes") var sixes : String,
	@SerializedName("fours") var fours : String,
	@SerializedName("runs") var runs : String,
	@SerializedName("playerOnStrike") var playerOnStrike : Boolean,
	@SerializedName("playerDismissalInfo") var playerDismissalInfo : String

)