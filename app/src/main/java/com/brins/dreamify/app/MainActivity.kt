package com.brins.dreamify.app

import android.os.Bundle
import com.brins.arouter_annotation.ARouter
import com.brins.commom.app.FrameworkActivity
import com.brins.dreamify.app.startask.AppStartCreateTask
import com.kugou.android.app.MainController

@ARouter(path = "/app/MainActivity")
class MainActivity : FrameworkActivity() {

    private var mMainController: MainController =
        MainController(this)
    private var mAppStartCreateTask: AppStartCreateTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mAppStartCreateTask = AppStartCreateTask(mMainController)
        mAppStartCreateTask?.preOnCreate()
        super.onCreate(savedInstanceState)
        mAppStartCreateTask?.afterSuperOnCreate()
    }

    override fun initAdditionalContent() {
        super.initAdditionalContent()
        mMainController.initAdditionalContent()
    }

    fun getMainController(): MainController? {
        return mMainController
    }
}