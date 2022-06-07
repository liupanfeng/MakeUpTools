package com.meishe.makeuptools

import android.content.Context
import com.meishe.libbase.BaseApplication


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 上午11:48
 * @Description :
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
class App : BaseApplication() {

    companion object{
        fun getContext(): Context {
            return mContext
        }
    }

}