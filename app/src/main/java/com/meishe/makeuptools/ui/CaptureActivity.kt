package com.meishe.makeuptools.ui

import androidx.lifecycle.ViewModelProvider
import com.meicam.sdk.NvsStreamingContext
import com.meishe.libbase.BaseActivity
import com.meishe.makeuptools.databinding.ActivityCaptureBinding
import com.meishe.modulecapture.factory.AppModelFactory
import com.meishe.makeuptools.viewmodel.CaptureLogicViewModel

class CaptureActivity : BaseActivity() {
    override val mViewBinding by bindLayout<ActivityCaptureBinding>()
    private lateinit var mLogicViewModel: CaptureLogicViewModel

    override fun initViewModel(){
        var streamingContext=NvsStreamingContext.getInstance()
        mLogicViewModel=ViewModelProvider(this,
            AppModelFactory(streamingContext)
        ).get(CaptureLogicViewModel::class.java)
    }

    override fun initData() {
        mLogicViewModel.connectCapturePreviewWithLiveWindow(mViewBinding.liveWindow)
        mLogicViewModel.startCapturePreview(true,1)
    }

    override fun initListener() {
        mViewBinding.btnAddMakeup.click {
            mLogicViewModel.addMakeup(this)
        }
        mViewBinding.btnRemoveMakeup.click {
            mLogicViewModel.removeMakeup(this)
        }
    }


}