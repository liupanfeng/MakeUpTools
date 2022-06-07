package com.meishe.libbase

import com.meicam.sdk.*


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 下午2:48
 * @Description : NvsStreamingContext 扩展
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
fun NvsStreamingContext.initTimeline(): NvsTimeline {

    val videoEditRes = NvsVideoResolution()
    videoEditRes.imageWidth = 1080
    videoEditRes.imageHeight = 720

    videoEditRes.imagePAR = NvsRational(1, 1)
    val videoFps = NvsRational(30, 1)
    videoEditRes.bitDepth =  NvsVideoResolution.VIDEO_RESOLUTION_BIT_DEPTH_8_BIT

    val audioEditRes = NvsAudioResolution()
    audioEditRes.sampleRate = 44100
    audioEditRes.channelCount = 2

    return createTimeline(videoEditRes, videoFps, audioEditRes)
}


fun NvsStreamingContext.createARSceneEffect():NvsCaptureVideoFx{
    for (i in 0..captureVideoFxCount){
        val videoFx = getCaptureVideoFxByIndex(i)
        if (videoFx===null){
            continue
        }
        if (Constants.AR_SCENE == videoFx.builtinCaptureVideoFxName){
            return videoFx
        }
    }

    val nvsCaptureVideoFx = appendBuiltinCaptureVideoFx(Constants.AR_SCENE)
//    if (BuildConfig.FACE_MODEL === 240) {
//        nvsVideoFx.setBooleanVal("Use Face Extra Info", true)
//    }

    //240点位
    nvsCaptureVideoFx.setBooleanVal("Use Face Extra Info", true)

    //支持的人脸个数，是否需要使用最小的设置
    nvsCaptureVideoFx.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true)
    val arSceneManipulate = nvsCaptureVideoFx.arSceneManipulate
    arSceneManipulate?.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE)
    return nvsCaptureVideoFx
}

fun NvsStreamingContext.seekTimeline(timeline: NvsTimeline,timestamp: Long, seekShowMode: Int){
    this.seekTimeline(
        timeline,
        timestamp,
        NvsStreamingContext.VIDEO_PREVIEW_SIZEMODE_LIVEWINDOW_SIZE,
        seekShowMode or NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME
    )
}