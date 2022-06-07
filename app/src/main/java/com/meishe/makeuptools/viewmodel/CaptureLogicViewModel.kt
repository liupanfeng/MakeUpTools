package com.meishe.makeuptools.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.meicam.sdk.NvsCaptureVideoFx
import com.meicam.sdk.NvsLiveWindow
import com.meicam.sdk.NvsStreamingContext
import com.meishe.libbase.createARSceneEffect
import com.meishe.libbase.seekTimeline
import com.meishe.libcommon.path.PathUtils
import com.meishe.libmakeup.MakeUpHelper
import java.io.File


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/31 上午17:48
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class CaptureLogicViewModel(private val mStreamingContext: NvsStreamingContext) :
    ViewModel() {

    private var mNvsCaptureVideoFx: NvsCaptureVideoFx? = null

    /**
     * 开启预览
     */
    fun startCapturePreview(deviceChanged: Boolean, deviceIndex: Int): Boolean {
        if (deviceChanged || mStreamingContext.streamingEngineState != NvsStreamingContext.STREAMING_ENGINE_STATE_CAPTUREPREVIEW) {
            if (!mStreamingContext.startCapturePreview(
                    deviceIndex,
                    NvsStreamingContext.VIDEO_CAPTURE_RESOLUTION_GRADE_SUPER_HIGH,
                    NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_DONT_USE_SYSTEM_RECORDER or
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_CAPTURE_BUDDY_HOST_VIDEO_FRAME or
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_ENABLE_TAKE_PICTURE or
                            NvsStreamingContext.STREAMING_ENGINE_CAPTURE_FLAG_STRICT_PREVIEW_VIDEO_SIZE,
                    null
                )
            ) {
                return false
            }
        }
        return true
    }

    /**
     * 连接liveWindow
     */
    fun connectCapturePreviewWithLiveWindow(liveWindow: NvsLiveWindow) {
        mStreamingContext.connectCapturePreviewWithLiveWindow(liveWindow)
    }

    fun addMakeup(context: Context) {
        val localMakeupDir = PathUtils.getLocalMakeupDir()
        val file = File(localMakeupDir)
        if (file.exists()) {
            //遍历本地sdk 卡的妆容特效
            val list = file.list()
            if (list.isEmpty()) {
                return
            }
            //获取首个妆容特效
            val makeupFileName: String = list[0]
            val makeupEffectFile = localMakeupDir + File.separator + makeupFileName

            val makeupEffectDir = File(makeupEffectFile)
            mNvsCaptureVideoFx = mStreamingContext.createARSceneEffect()

            if (makeupEffectDir.exists()) {

                //------------------------应用美妆----------------------------//
                mNvsCaptureVideoFx?.let {
                    MakeUpHelper.instances.applyCaptureMakeupEffect(context, it, makeupEffectDir)
                }
                //-------------------------------end---------------------//
            }
        }
    }

    fun removeMakeup(context: Context) {
        mNvsCaptureVideoFx?.let {
            MakeUpHelper.instances.removeCaptureMakeupEffect(
                context,it)
        }
    }
}