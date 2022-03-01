package com.adriankohls.bubble_overlay

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.adriankohls.bubble_overlay.model.BaseClass
import com.google.gson.Gson
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.serialization.*
import okhttp3.*
import java.util.*


/** BubbleOverlayPlugin */
class BubbleOverlayPlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler {
    private var activity: Activity? = null
    private var matchId: String? = ""
    private var channel: MethodChannel? = null
    private val channelName: String = "com.adriankohls/bubble_overlay"
    private val CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084
    private var mOverlayService: BubbleOverlayService? = null
    private var mOverlayVideoService: BubbleVideoOverlayService? = null
    private var connection: ServiceConnection? = null
    private var connectionVideo: ServiceConnection? = null
    private var mBound: Boolean = false
    private var mBoundVideo: Boolean = false
    private var isCurrentTimeDirty: Boolean = false
    private var currentTime: Long = -1

    // give data from service
    private val BReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //put here whaterver you want your activity to do with the intent received
            var lastCurrentTime = intent?.getLongExtra("currentTime", -1)
            Log.d("wooow currentTime:", currentTime.toString())
            if (lastCurrentTime != currentTime) {
                currentTime = lastCurrentTime!!
                isCurrentTimeDirty = true
            }
        }
    }


    private fun connect(call: MethodCall?) {
        connection = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as BubbleOverlayService.LocalBinder
                mOverlayService = binder.getService()
                mBound = true

                if (call != null)
                    if (call.method == "openBubble") {
                        val arguments = call.arguments as List<Any?>

                        val title: String? = arguments[0] as String?
                        val customText: String? = arguments[1] as String?
                        val bottomText: String? = arguments[2] as String?
                        val titleColor: String? = arguments[3] as String?
                        val textColor: String? = arguments[4] as String?
                        val bottomColor: String? = arguments[5] as String?
                        val backgroundColor: String? = arguments[6] as String?
                        val topIconAsset: String? = arguments[7] as String?
                        val bottomIconAsset: String? = arguments[8] as String?
                        matchId = arguments[9] as String?

                        if (title != null)
                            mOverlayService?.updateTitle(title)
                        if (customText != null)
                            mOverlayService?.updateText(customText)
                        if (bottomText != null)
                            mOverlayService?.updateBottomText(bottomText)
                        if (titleColor != null)
                            mOverlayService?.updateTitleColor(titleColor)
                        if (textColor != null)
                            mOverlayService?.updateTextColor(textColor)
                        if (bottomColor != null)
                            mOverlayService?.updateBottomTextColor(bottomColor)
                        if (backgroundColor != null)
                            mOverlayService?.updateBubbleColor(backgroundColor)
                        mOverlayService?.updateIconTop(topIconAsset)
                        mOverlayService?.updateIconBottom(bottomIconAsset)
                    }
            }


            override fun onServiceDisconnected(arg0: ComponentName) {
                mOverlayService?.stopSelf()
                mBound = false
            }
        }

        connectionVideo = object : ServiceConnection {

            override fun onServiceConnected(className: ComponentName, service: IBinder) {
                val binder = service as BubbleVideoOverlayService.LocalBinder
                mOverlayVideoService = binder.getService()
                mBoundVideo = true

                if (call != null)
                    if (call.method == "openVideoBubble") {
                        val arguments = call.arguments as List<Any?>
                        val data = arguments[0] as String
                        val seekAtStart = arguments[1] as Boolean
                        val startTimeInSeconds = (arguments[2] as Int).toLong()
                        val controlsType: ControlsType = enumValueOf(arguments[3] as String)
                        val uri = Uri.parse(data)
                        mOverlayVideoService?.setVideo(uri, seekAtStart, startTimeInSeconds, controlsType)
                    }

                // return data to flutter module
                LocalBroadcastManager.getInstance(activity?.applicationContext!!)
                        .registerReceiver(BReceiver, IntentFilter("message"))
            }


            override fun onServiceDisconnected(arg0: ComponentName) {
                val map = mutableMapOf<String, String>()
                map["isCurrentTimeDirty"] = isCurrentTimeDirty.toString()
                map["currentTime"] = currentTime.toString()

                channel?.invokeMethod("getCurrentTime", map)

                mOverlayVideoService?.stopSelf()
                mBoundVideo = false
            }
        }

        Intent(activity, BubbleOverlayService::class.java).also { intent ->
            if (connection != null) activity?.bindService(intent, connection!!, 0)
        }

        Intent(activity, BubbleVideoOverlayService::class.java).also { intent ->
            if (connectionVideo != null) activity?.bindService(intent, connectionVideo!!, 0)
        }

    }

    private fun release() {
        if (connection != null) activity?.unbindService(connection!!)
        mOverlayService?.stopSelf()
        mBound = false

    }

    private fun releaseVideo() {
        if (connectionVideo != null) {
            activity?.unbindService(connectionVideo!!)
        }
        mOverlayVideoService?.stopSelf()
        mBoundVideo = false
    }

    private fun reloadOverlay () {
        val t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    print("Overlay reload from Android side")
                    val client = OkHttpClient.Builder().build()
                    val mediaType = MediaType.parse("application/json")
                    val requestBody = RequestBody.create(mediaType,"{\"query\":\"    query miniScoreCard(\$matchID: String!) {\\r\\n    miniScoreCard(matchID: \$matchID) {\\r\\n        isDisplayDugout\\r\\n        batting {\\r\\n        matchID\\r\\n        playerFeedID\\r\\n        playerName\\r\\n        \\r\\n        playerTeam\\r\\n        sixes\\r\\n        fours\\r\\n        runs\\r\\n        playerOnStrike\\r\\n        playerDismissalInfo\\r\\n        }\\r\\n        bowling {\\r\\n        matchID\\r\\n        playerFeedID\\r\\n        playerName\\r\\n        playerTeam\\r\\n        wickets\\r\\n        maiden\\r\\n        RunsConceeded\\r\\n        overs\\r\\n        economy\\r\\n        }\\r\\n        partnership\\r\\n        oversRemaining\\r\\n        reviewDetails {\\r\\n        teamName\\r\\n        review\\r\\n        }\\r\\n        runRate\\r\\n        rRunRate\\r\\n        data {\\r\\n        currentinningsNo\\r\\n        currentInningteamID\\r\\n        currentInningsTeamName\\r\\n        seriesName\\r\\n        seriesID\\r\\n        homeTeamName\\r\\n        awayTeamName\\r\\n        toss\\r\\n        startEndDate\\r\\n        matchStatus\\r\\n        matchID\\r\\n        matchType\\r\\n        statusMessage\\r\\n        matchNumber\\r\\n        venue\\r\\n        matchResult\\r\\n        startDate\\r\\n        playerID\\r\\n        playerOfTheMatch\\r\\n        playerofTheMatchTeamShortName\\r\\n        firstInningsTeamID\\r\\n        secondInningsTeamID\\r\\n        thirdInningsTeamID\\r\\n        fourthInningsTeamID\\r\\n        isCricklyticsAvailable\\r\\n        isFantasyAvailable\\r\\n        isLiveCriclyticsAvailable\\r\\n        isAbandoned\\r\\n        playing11Status\\r\\n        probable11Status\\r\\n        currentDay\\r\\n        currentSession\\r\\n        teamsWinProbability {\\r\\n            homeTeamShortName\\r\\n            homeTeamPercentage\\r\\n            awayTeamShortName\\r\\n            awayTeamPercentage\\r\\n            tiePercentage\\r\\n        }\\r\\n        matchScore {\\r\\n            teamShortName\\r\\n            teamID\\r\\n            teamFullName\\r\\n            teamScore {\\r\\n            inning\\r\\n            inningNumber\\r\\n            battingTeam\\r\\n            runsScored\\r\\n            wickets\\r\\n            overs\\r\\n            runRate\\r\\n            battingSide\\r\\n            teamID\\r\\n            battingTeamShortName\\r\\n            declared\\r\\n            folowOn\\r\\n            }\\r\\n        }\\r\\n        }\\r\\n    }\\r\\n    }\\r\\n\",\"variables\":{\"matchID\":\"${matchId.toString()}\"}}")
                    val request = Request.Builder().url("https://apiv2.cricket.com/cricket")
                        .method("POST",requestBody)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    val response = client.newCall(request).execute()
                    val score_card:BaseClass? = Gson().fromJson<BaseClass>(response.body()?.string(), BaseClass::class.java)
                    print("Scorecard: "+score_card.toString())
                    activity?.runOnUiThread {
                        val score:String = score_card?.data?.miniScoreCard?.data?.last()?.matchScore?.last()?.teamScore?.last()?.runsScored.toString() +"/"+ score_card?.data?.miniScoreCard?.data?.last()?.matchScore?.last()?.teamScore?.last()?.wickets;
                        mOverlayService?.updateText(score)
                        mOverlayService?.updateBottomText(score_card?.data?.miniScoreCard?.data?.last()?.matchScore?.last()?.teamScore?.last()?.overs.toString())
                    }
                }
            },
            0,
            10000
        )
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        connect(call)
        when (call.method) {
            "openBubble" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                    val packageName = activity?.packageName
                    activity?.startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                            CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                    reloadOverlay()
                } else {
                    activity?.startService(
                            Intent(activity, BubbleOverlayService::class.java))
                    activity?.moveTaskToBack(true)
                    reloadOverlay()
                }
            }
            "openVideoBubble" -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(activity)) {
                    val packageName = activity?.packageName
                    activity?.startActivityForResult(
                            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")),
                            CODE_DRAW_OVER_OTHER_APP_PERMISSION)
                    // TODO restore previous intent visibility
                } else {
                    activity?.startService(
                            Intent(activity, BubbleVideoOverlayService::class.java))
                    activity?.moveTaskToBack(true)
                }
            }
            "isVideoBubbleOpen" -> result.success(mBoundVideo)
            "closeVideoBubble" -> if (mBoundVideo) releaseVideo()
            "isBubbleOpen" -> result.success(mBound)
            "closeBubble" -> if (mBound) release()
            "updateBubbleText" ->
                if (mBound) {
                    val text = call.arguments as String
                    mOverlayService?.updateText(text)
                } else
                    throw Exception("BubbleService not running.")

            "updateBubbleTextColor" -> if (mBound) {
                val textColor = call.arguments as String
                mOverlayService?.updateTextColor(textColor)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleTitle" -> if (mBound) {
                val text = call.arguments as String
                mOverlayService?.updateTitle(text)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleTitleColor" -> if (mBound) {
                val text = call.arguments as String
                mOverlayService?.updateTitle(text)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleBottomText" -> if (mBound) {
                val text = call.arguments as String
                mOverlayService?.updateBottomText(text)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleBottomTextColor" -> if (mBound) {
                val textColor = call.arguments as String
                mOverlayService?.updateBottomTextColor(textColor)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleColor" -> if (mBound) {
                val bubbleColor = call.arguments as String
                mOverlayService?.updateBubbleColor(bubbleColor)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleTopIcon" -> if (mBound) {
                val icon = call.arguments as String?
                mOverlayService?.updateIconTop(icon)
            } else
                throw Exception("BubbleService not running.")
            "updateBubbleBottomIcon" -> if (mBound) {
                val icon = call.arguments as String?
                mOverlayService?.updateIconBottom(icon)
            } else
                throw Exception("BubbleService not running.")
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
//        channel?.setMethodCallHandler(null)
//        release()
//        releaseVideo()
    }

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, channelName)
        channel?.setMethodCallHandler(this)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivity() {
//        release()
//        releaseVideo()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
//        release()
//        releaseVideo()
    }
}