package com.meishe.libbase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.LogUtils
import java.lang.reflect.Method


/**
 * * All rights reserved,Designed by www.meishesdk.com
 * @Author : lpf
 * @CreateDate : 2022/5/27 下午12:00
 * @Description : 基类封装
 * @Copyright :www.meishesdk.com Inc.All rights reserved.
 */
abstract class BaseActivity : AppCompatActivity() {
    open val TAG: String get() = javaClass.simpleName
    lateinit var mContext: Context

    //布局的实例了
    abstract val mViewBinding: ViewBinding?

    /**
     * 子类的布局的示例化
     */
    protected inline fun <reified T> bindLayout(): Lazy<T> where T : ViewBinding {
        return lazy {
            val clazz: Class<T> = T::class.java // 等于布局
            val method: Method = clazz.getMethod("inflate", LayoutInflater::class.java)
            method.invoke(null, layoutInflater) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        mViewBinding?.root?.apply {
            setContentView(this)
        }

        initViewModel()
        initData()
        initListener()
    }

   open fun initViewModel() {}

    abstract fun initData()
    abstract fun initListener()

    fun <T> T.logD() = LogUtils.d(TAG, this.toString())
    fun <T> T.logE() = LogUtils.e(TAG, this.toString())
    fun View.click(action: (View) -> Unit) = this.setOnClickListener { action.invoke(this) }
    fun <T> T.toast() = Toast.makeText(mContext, this.toString(), Toast.LENGTH_LONG).show()

    fun <T :Activity> start(clazz:Class<T>) =startActivity(Intent(this,clazz))

}