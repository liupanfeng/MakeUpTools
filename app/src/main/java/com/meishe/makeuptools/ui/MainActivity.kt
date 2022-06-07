package com.meishe.makeuptools.ui

import com.blankj.utilcode.util.LogUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.meicam.sdk.NvsStreamingContext
import com.meishe.libbase.AndroidOS
import com.meishe.libbase.BaseActivity
import com.meishe.libcommon.path.FileUtils
import com.meishe.makeuptools.R
import com.meishe.makeuptools.databinding.ActivityMainBinding
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class MainActivity : BaseActivity() {

    private lateinit var mSubscribe: Disposable
    override val mViewBinding by bindLayout<ActivityMainBinding>()

    override fun initData() {
        requestPermission()
    }

    override fun initListener() {
        mViewBinding.btnStartEdit.click {
            start(EditActivity::class.java)
        }

        mViewBinding.btnStartCapture.click {
            start(CaptureActivity::class.java)
        }
    }

    private fun initModels() {
        mSubscribe = Observable.just(1).observeOn(Schedulers.io()).subscribe(Consumer {
            doInitModel()
        })
    }

    /**
     * 初始化模型
     */
    private fun doInitModel() {

        var modelPath = "/facemode/ms/240/ms_face240_v2.0.1.model"
        var faceModelName = "ms_face240_v2.0.1.model"
        var className = "facemode/ms/240"
        var licensePath = ""


        /**
         * 模型文件需要是本地文件路径，所以从assert内置拷贝到本地
         * The model file needs to be a local file path, so copy it from assert built-in to local
         */
        val copySuccess: Boolean =
            FileUtils.copyFileIfNeed(this@MainActivity, faceModelName, className)
        LogUtils.d(TAG, "copySuccess-->$copySuccess")
        var rootDir = applicationContext.getExternalFilesDir("")
        if (AndroidOS.USE_SCOPED_STORAGE) {
            rootDir = applicationContext.filesDir
        }

        val destModelDir = rootDir.toString() + modelPath

        /**
         * Streaming sdk 初始化基础人脸点位模型
         * Streaming sdk initializes the basic face point model
         */
        val initSuccess = NvsStreamingContext.initHumanDetection(
            applicationContext,
            destModelDir, licensePath,
            NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_LANDMARK or
                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_FACE_ACTION or
                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_SEMI_IMAGE_MODE or
                    NvsStreamingContext.HUMAN_DETECTION_FEATURE_IMAGE_MODE
        )

        "initSuccess-->$initSuccess".logD()

        /**
         * 美妆2模型初始化
         * The makeup 2 model is initialized
         */
        val makeUpPath2 = "assets:/facemode/common/makeup2_240_v1.0.0.dat"
        val makeupSuccess2 = NvsStreamingContext.setupHumanDetectionData(
            NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_MAKEUP2,
            makeUpPath2
        )
        "makeupSuccess-->$makeupSuccess2".logD()

        /**
         * 这个跟人脸240点位相关，如果使用240点位，必须加这个否则可能导致不生效
         * This is related to the 240 points of the face.
         * If 240 points are used, this must be added, otherwise it may not take effect.
         */
        val pePath = "assets:/facemode/ms/240/pe240_ms_v1.0.1.dat"
        val peSuccess = NvsStreamingContext.setupHumanDetectionData(
            NvsStreamingContext.HUMAN_DETECTION_DATA_TYPE_PE240,
            pePath
        )
        "ms240 peSuccess-->$peSuccess".logD()


    }

    override fun onDestroy() {
        super.onDestroy()
        mSubscribe.dispose()
    }

    /**
     * 获取授权
     */
    private fun requestPermission() {
        XXPermissions.with(this).permission(Permission.READ_EXTERNAL_STORAGE)
            .permission(Permission.CAMERA)
            .permission(Permission.WRITE_EXTERNAL_STORAGE)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all) {
                        initModels()
                    } else {
                        getString(R.string.get_pert_permission).toast()
                    }
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    if (never) {
                        getString(R.string.deny_permisson).toast()
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(mContext, permissions);
                    } else {
                        getString(R.string.get_permission_error).toast()
                    }
                }
            })
    }

}