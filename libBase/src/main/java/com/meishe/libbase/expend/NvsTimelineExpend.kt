package com.meishe.libbase

import com.meicam.sdk.NvsTimeline
import com.meicam.sdk.NvsVideoClip
import com.meicam.sdk.NvsVideoTrack


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 下午3:08
 * @Description : NvsTimeline 扩展
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */

fun NvsTimeline.addClip(filePath:String, inPoint:Long) :NvsVideoClip{
    var videoTrackByIndex = getVideoTrackByIndex(0)
    if (videoTrackByIndex==null){
        videoTrackByIndex= appendVideoTrack()
    }
    return videoTrackByIndex.addClip(filePath,inPoint)
}
