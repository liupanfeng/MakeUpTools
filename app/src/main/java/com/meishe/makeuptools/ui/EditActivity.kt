package com.meishe.makeuptools.ui

import com.meicam.sdk.NvsStreamingContext
import com.meicam.sdk.NvsTimeline
import com.meicam.sdk.NvsVideoClip
import com.meicam.sdk.NvsVideoFx
import com.meishe.libbase.*
import com.meishe.libcommon.path.PathUtils
import com.meishe.libmakeup.MakeUpHelper
import com.meishe.makeuptools.databinding.ActivityEditBinding
import java.io.File

class EditActivity : BaseActivity() {

    private lateinit var mNvsVideoFx: NvsVideoFx
    private lateinit var mVideoClip: NvsVideoClip
    override val mViewBinding by bindLayout<ActivityEditBinding>()
    private lateinit var mTimeline: NvsTimeline
    private lateinit var mNvsStreamingContext: NvsStreamingContext

    private val mPicFilePath = "assets:/pic.png"

    override fun initData() {
        mNvsStreamingContext = NvsStreamingContext.getInstance()
        mTimeline = mNvsStreamingContext.initTimeline()
        mNvsStreamingContext.connectTimelineWithLiveWindow(mTimeline, mViewBinding.liveWindow)

        mVideoClip = mTimeline.addClip(mPicFilePath, 0)
        mNvsStreamingContext.seekTimeline(mTimeline, 100, 0)

    }

    override fun initListener() {
        mViewBinding.btnAddMakeup.click {
            ///storage/emulated/0/Android/data/com.meishe.makeuptools/files/MakeUpTools/makeup
            val localMakeupDir = PathUtils.getLocalMakeupDir()
            localMakeupDir.logD()
            val file = File(localMakeupDir)
            if (file.exists()) {
                //遍历本地sdk 卡的妆容特效
                val list = file.list()
                if(list.isEmpty()){
                    "请添加美妆包".toast()
                    return@click
                }
                //获取首个妆容特效
                val makeupFileName:String=list[0]
                val makeupEffectFile=localMakeupDir+File.separator+makeupFileName
                makeupEffectFile.logD()

                val makeupEffectDir=File(makeupEffectFile)
                mNvsVideoFx = mVideoClip.createARSceneEffect()

                if (makeupEffectDir.exists()){

                    //------------------------应用美妆----------------------------//
                    MakeUpHelper.instances.
                    applyMakeupEffect(this,mVideoClip,mNvsVideoFx,makeupEffectDir)
                    mNvsStreamingContext.seekTimeline(mTimeline, 0,
                        NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME)
                    //-------------------------------end---------------------//
                }
            }
        }

        mViewBinding.btnRemoveMakeup.click {
            //-----------------------删除美妆效果------------------

            MakeUpHelper.instances.removeMakeupEffect(this,mVideoClip,mNvsVideoFx)
            mNvsStreamingContext.seekTimeline(mTimeline, 0,
                NvsStreamingContext.STREAMING_ENGINE_SEEK_FLAG_BUDDY_HOST_VIDEO_FRAME)

            //----------------------------end-------------------------------
        }
    }

}