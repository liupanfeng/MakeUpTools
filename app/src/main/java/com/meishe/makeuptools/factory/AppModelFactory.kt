package com.meishe.modulecapture.factory

import androidx.collection.ArrayMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meicam.sdk.NvsStreamingContext
import com.meishe.makeuptools.viewmodel.CaptureLogicViewModel
import java.lang.IllegalArgumentException
import java.util.concurrent.Callable


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/31 下午5:33
 * @Description : 创建model的工厂 统一创建ViewModel
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class AppModelFactory(private val mStreamingContext: NvsStreamingContext) :
    ViewModelProvider.Factory {

    private val mCreators: ArrayMap<Class<*>, Callable<out ViewModel?>> = ArrayMap()

    init {
        mCreators[CaptureLogicViewModel::class.java] =
            Callable { CaptureLogicViewModel(mStreamingContext) }
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        var model = mCreators[modelClass]
        if (model == null) {
            mCreators.entries.forEach {
                if (modelClass.isAssignableFrom(it.key)) {
                    model = it.value
                }
            }
        }

        if (model == null) {
            throw IllegalArgumentException("unknown model class${modelClass}")
        }
        return model?.call() as T
    }
}