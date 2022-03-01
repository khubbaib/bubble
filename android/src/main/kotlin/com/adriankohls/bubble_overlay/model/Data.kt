package com.adriankohls.bubble_overlay.model

import com.google.gson.annotations.SerializedName


data class Data (

	@SerializedName("currentinningsNo") var currentinningsNo : String,
	@SerializedName("currentInningteamID") var currentInningteamID : String,
	@SerializedName("currentInningsTeamName") var currentInningsTeamName : String,
	@SerializedName("seriesName") var seriesName : String,
	@SerializedName("seriesID") var seriesID : String,
	@SerializedName("homeTeamName") var homeTeamName : String,
	@SerializedName("awayTeamName") var awayTeamName : String,
	@SerializedName("toss") var toss : String,
	@SerializedName("startEndDate") var startEndDate : String,
	@SerializedName("matchStatus") var matchStatus : String,
	@SerializedName("matchID") var matchID : String,
	@SerializedName("matchType") var matchType : String,
	@SerializedName("statusMessage") var statusMessage : String,
	@SerializedName("matchNumber") var matchNumber : String,
	@SerializedName("venue") var venue : String,
	@SerializedName("matchResult") var matchResult : String,
	@SerializedName("startDate") var startDate : String,
	@SerializedName("playerID") var playerID : String,
	@SerializedName("playerOfTheMatch") var playerOfTheMatch : String,
	@SerializedName("playerofTheMatchTeamShortName") var playerofTheMatchTeamShortName : String,
	@SerializedName("firstInningsTeamID") var firstInningsTeamID : String,
	@SerializedName("secondInningsTeamID") var secondInningsTeamID : String,
	@SerializedName("thirdInningsTeamID") var thirdInningsTeamID : String,
	@SerializedName("fourthInningsTeamID") var fourthInningsTeamID : String,
	@SerializedName("isCricklyticsAvailable") var isCricklyticsAvailable : Boolean,
	@SerializedName("isFantasyAvailable") var isFantasyAvailable : Boolean,
	@SerializedName("isLiveCriclyticsAvailable") var isLiveCriclyticsAvailable : Boolean,
	@SerializedName("isAbandoned") var isAbandoned : Boolean,
	@SerializedName("playing11Status") var playing11Status : Boolean,
	@SerializedName("probable11Status") var probable11Status : Boolean,
	@SerializedName("currentDay") var currentDay : Int,
	@SerializedName("currentSession") var currentSession : Int,
	@SerializedName("teamsWinProbability") var teamsWinProbability : TeamsWinProbability,
	@SerializedName("matchScore") var matchScore : List<MatchScore>

)