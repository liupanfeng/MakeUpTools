package com.meishe.libbase

import android.content.Context
import android.os.Build


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 下午12:00
 * @Description : 适配分区存储
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
object AndroidOS {

    @JvmField
    var USE_SCOPED_STORAGE: Boolean = false

    fun initConfig(context: Context?) {
        //android11的适配版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            USE_SCOPED_STORAGE = true
        }
    }

}