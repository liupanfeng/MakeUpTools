package com.meishe.libmakeup

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.meicam.sdk.*
import com.meishe.libmakeup.bean.*
import java.io.File
import java.io.FileNotFoundException


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 下午3:30
 * @Description : 应用美妆帮助类
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class MakeUpHelper {

    private var mMakeupInfo: Makeup? = null

    companion object {
        val instances by lazy { MakeUpHelper() }
    }

    fun applyMakeupEffect(
        context: Context,
        videoClip: NvsVideoClip,
        videoFx: NvsVideoFx,
        effectFileDir: File
    ) {
        if (!effectFileDir.exists()) {
            throw FileNotFoundException("param effectDir is error，file not found")
        }

        val nvsStreamingContext = NvsStreamingContext.getInstance()
        if (nvsStreamingContext === null) {
            return
        }
        val assetPackageManager = nvsStreamingContext.assetPackageManager
        if (assetPackageManager === null) {
            return
        }
        parseAndInstallEffects(effectFileDir,nvsStreamingContext)
        addMakeUpEffect(context, videoClip, videoFx , mMakeupInfo)

    }

    /**
     * 添加拍摄美妆特效
     */
    fun applyCaptureMakeupEffect(
        context: Context,
        nvsCaptureVideoFx: NvsCaptureVideoFx,
        effectFileDir: File
    ) {
        if (!effectFileDir.exists()) {
            throw FileNotFoundException("param effectDir is error，file not found")
        }

        var nvsStreamingContext=NvsStreamingContext.getInstance()
        if (nvsStreamingContext===null){
            return
        }
        parseAndInstallEffects(effectFileDir,nvsStreamingContext)
        addCaptureMakeUpEffect(
            context, nvsStreamingContext,
            nvsCaptureVideoFx, mMakeupInfo
        )
    }


    /**
     * 清理编辑的美妆
     */
    fun removeMakeupEffect( context: Context,
                             videoClip: NvsVideoClip,
                             videoFx: NvsVideoFx) {
        addMakeUpEffect(context, videoClip, videoFx, Makeup())
    }

    /**
     * 清理编辑的美妆
     */
    fun removeCaptureMakeupEffect( context: Context,
                                   nvsCaptureVideoFx: NvsCaptureVideoFx) {
        var nvsStreamingContext=NvsStreamingContext.getInstance()
        if (nvsStreamingContext===null){
            return
        }
        addCaptureMakeUpEffect(context, nvsStreamingContext, nvsCaptureVideoFx, Makeup())
    }


    private fun parseAndInstallEffects(
        effectFileDir: File,
        nvsStreamingContext: NvsStreamingContext
    ) {
        val assetPackageManager = nvsStreamingContext.assetPackageManager
        if (assetPackageManager === null) {
            return
        }
        effectFileDir.list { file, s ->
            if (s.equals("info.json")) {
                val infoJsonFilePath = effectFileDir.absolutePath + File.separator + s
                val infoJsonStr = ParseJsonFile.readSdCardJsonFile(infoJsonFilePath)
                //得到数据bean
                mMakeupInfo = ParseJsonFile.fromJson(infoJsonStr, Makeup::class.java)
            }

            val infoJsonFilePath = effectFileDir.absolutePath + File.separator + s
            val parseFileName = parseFileName(s)
            parseFileName?.let {
                val licPath = effectFileDir.absolutePath + File.separator + it + ".lic"
                installNewMakeup(assetPackageManager, infoJsonFilePath, licPath)
            }
            //安装特效
            //            installNewMakeup(assetPackageManager, infoJsonFilePath, "")
            true
        }
    }


    /**
     * 添加美妆效果
     */
    private fun addCaptureMakeUpEffect(
        context: Context,
        nvsStreamingContext: NvsStreamingContext,
        nvsCaptureVideoFx:  NvsCaptureVideoFx,
        makeupInfo: Makeup?
    ) {

        if (makeupInfo === null) {
            return
        }

        //妆容清理全部的单妆
        BeautyEffectUtil.clearAllCustomMakeup(context, nvsCaptureVideoFx)
        //清理滤镜
        BeautyEffectUtil.resetCaptureFilterFx(nvsStreamingContext)

        val makeupEffectContent: MakeupEffectContent = makeupInfo.effectContent ?: return
        //添加效果包中带的美颜
        BeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.beauty, false)
        //添加效果包中带的美型
        BeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.shape, false)
        //添加效果包中带的微整形
        BeautyEffectUtil.setMakeupBeautyArgs(nvsCaptureVideoFx, makeupEffectContent.microShape, true)
        //添加效果包中带的滤镜
        val filter: List<FilterArgs> = makeupEffectContent.filter
        BeautyEffectUtil.setCaptureFilterContent(nvsStreamingContext, filter)

        setMakeupEffect(makeupEffectContent, nvsCaptureVideoFx)

    }

    private fun addMakeUpEffect(
        context: Context,
        videoClip: NvsVideoClip,
        nvsVideoFx: NvsVideoFx, makeupInfo: Makeup?
    ) {

        if (makeupInfo === null) {
            return
        }

        //妆容清理全部的单妆
        BeautyEffectUtil.clearAllCustomMakeup(context, nvsVideoFx)
        //清理滤镜
        BeautyEffectUtil.resetMakeupFx(videoClip)

        val makeupEffectContent: MakeupEffectContent = makeupInfo.effectContent ?: return
        //添加效果包中带的美颜
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.beauty, false)
        //添加效果包中带的美型
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.shape, false)
        //添加效果包中带的微整形
        BeautyEffectUtil.setMakeupBeautyArgs(nvsVideoFx, makeupEffectContent.microShape, true)
        //添加效果包中带的滤镜
        val filter: List<FilterArgs> = makeupEffectContent.filter
        BeautyEffectUtil.setFilterContent(videoClip, filter)

        setMakeupEffect(makeupEffectContent, nvsVideoFx)
    }

    private fun setMakeupEffect(
        makeupEffectContent: MakeupEffectContent,
        nvsVideoFx: NvsFx
    ) {
        //添加美妆
        val makeupArgs: List<MakeupArgs>? = makeupEffectContent.makeupArgs
        if (makeupArgs != null) {
            for (args in makeupArgs) {
                if (args == null) {
                    continue
                }
                if (nvsVideoFx != null) {
                    nvsVideoFx.setIntVal(
                        "Makeup Custom Enabled Flag",
                        NvsMakeupEffectInfo.MAKEUP_EFFECT_CUSTOM_ENABLED_FLAG_ALL
                    )
                    nvsVideoFx.setColorVal(
                        "Makeup " + args.getType().toString() + " Color",
                        NvsColor(0f, 0f, 0f, 0f)
                    )
                    nvsVideoFx.setFloatVal(
                        "Makeup " + args.getType().toString() + " Intensity",
                        args.getValue().toDouble()
                    )
                    nvsVideoFx.setStringVal(args.getClassName(), args.getUuid())
                    Log.d(
                        "=====",
                        "className:" + args.getClassName().toString() + " value:" + args.getUuid()
                    )
                }
            }
        }
    }


    /**
     * 安装妆容相关的特效
     */
    private fun installNewMakeup(
        assetPackageManager: NvsAssetPackageManager,
        filePath: String, licPath: String
    ) {

        val uuid = StringBuilder()
        if (filePath.endsWith(".makeup")) {
            val makeupResult = assetPackageManager.installAssetPackage(
                filePath, licPath,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_MAKEUP, true, uuid
            )
        } else if (filePath.endsWith(".warp")) {
            val warpResult = assetPackageManager.installAssetPackage(
                filePath, licPath,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_WARP, true, uuid
            )
        } else if (filePath.endsWith(".facemesh")) {
            val facemeshResult = assetPackageManager.installAssetPackage(
                filePath, licPath,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_FACE_MESH, true, uuid
            )
        } else if (filePath.endsWith(".videofx")) {
            val filterResult = assetPackageManager.installAssetPackage(
                filePath, licPath,
                NvsAssetPackageManager.ASSET_PACKAGE_TYPE_VIDEOFX, true, uuid
            )
        }
    }

    private fun parseFileName(filename: String): String? {
        if (!TextUtils.isEmpty(filename)) {
            val split = filename.split(".")
            return split[0];
        }
        return null
    }

}