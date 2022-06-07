package com.meishe.libbase

import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsVideoClip
import com.meicam.sdk.NvsVideoFx


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/30 上午11:41
 * @Description : NvsVideoClip 扩展
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */

fun NvsVideoClip.createARSceneEffect():NvsVideoFx{
    for (i in 0..fxCount){
        val videoFx = getFxByIndex(i)
        if (videoFx===null){
            continue
        }
        if (Constants.AR_SCENE == videoFx.builtinVideoFxName){
           return videoFx
        }
    }

    val nvsVideoFx = insertBuiltinFx(Constants.AR_SCENE, 0)
//    if (BuildConfig.FACE_MODEL === 240) {
//        nvsVideoFx.setBooleanVal("Use Face Extra Info", true)
//    }

    //240点位
    nvsVideoFx.setBooleanVal("Use Face Extra Info", true)

    //支持的人脸个数，是否需要使用最小的设置
    nvsVideoFx.setBooleanVal(Constants.MAX_FACES_RESPECT_MIN, true)
    val arSceneManipulate = nvsVideoFx.arSceneManipulate
    arSceneManipulate?.setDetectionMode(NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE)
    return nvsVideoFx
}