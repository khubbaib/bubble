package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class TeamScore (

	@SerializedName("inning") var inning : Int,
	@SerializedName("inningNumber") var inningNumber : String,
	@SerializedName("battingTeam") var battingTeam : String,
	@SerializedName("runsScored") var runsScored : String,
	@SerializedName("wickets") var wickets : String,
	@SerializedName("overs") var overs : String,
	@SerializedName("runRate") var runRate : String,
	@SerializedName("battingSide") var battingSide : String,
	@SerializedName("teamID") var teamID : String,
	@SerializedName("battingTeamShortName") var battingTeamShortName : String,
	@SerializedName("declared") var declared : Boolean,
	@SerializedName("folowOn") var folowOn : Boolean

)