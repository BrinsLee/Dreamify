package com.brins.dreamify.app.startask

import android.graphics.PixelFormat
import com.brins.commom.app.TaskType
import com.brins.commom.base.FrameworkUtil
import com.brins.commom.base.MainFragmentContainer
import com.brins.commom.utils.KGThreadPool
import com.brins.dreamify.app.MainActivity
import com.kugou.android.app.MainController
import com.brins.dreamify.app.base.MainFragmentServiceImpl
import com.brins.dreamify.app.globalbusiness.GlobalWorkHandler
import com.brins.dreamify.app.globalbusiness.MainHandler
import com.brins.dreamify.app.mine.MineFragmentServiceImpl
import com.brins.dreamify.framework.uiframe.FragmentViewFactory
import com.brins.dreamify.framework.uiframe.KGFragmentViewFactory
import com.kugou.page.framework.FragmentViewFactories

/**
 * @author lipeilin
 * @date 2022/10/24
 * @desc
 */
class AppStartCreateTask (var mainController: MainController) {

    private var mMainActivity : MainActivity = mainController.attachActivity
    init {

    }

    fun preOnCreate() {
        initStackAndRestoreStatus()
    }

    fun afterSuperOnCreate() {
        mainController.isFrist = true
        initMainFragmentService()
        initWeakRefAndWindowFlag()
        initHandler()
        initRegisterBroadcast()
        // initRegisterLocalAssetsBroadcast()
        initRegisterInitEventbus()
        initFragmentCallback()
        initLoadUiFramework()

    }

    private fun initMainFragmentService() {
        mMainActivity.mMainFragmentService = MainFragmentServiceImpl()
        mMainActivity.mMineFragmentService = MineFragmentServiceImpl()
    }

    private fun initStackAndRestoreStatus() {
        FrameworkUtil.setCurrentStack(TaskType.TYPE_MAIN)
    }

    private fun initWeakRefAndWindowFlag() {
        mMainActivity.getWindow().setFormat(PixelFormat.TRANSLUCENT)
    }

    private fun initHandler() {
        mainController.mHandler = MainHandler(mainController)
        mainController.mWorkHandler =
            GlobalWorkHandler(mainController, KGThreadPool.getInstance().getMainWorkLooper())
    }

    private fun initRegisterBroadcast() {

    }

    private fun initRegisterInitEventbus() {
        /*mainController.mGlobalEventBusHandler = GlobalEventBusHandler(mMainController)
        mMainController.mGlobalEventBusHandler.register()
        ClassifyLogicHelper.getInstance().registerEvent()*/
    }

    private fun initFragmentCallback() {
        FragmentViewFactories.getInstance()
            .registerCreateViewInterface(KGFragmentViewFactory.getInstance())
        FragmentViewFactories.getInstance()
            .registerCreateViewInterface(FragmentViewFactory.getInstance())
        // mMediaActivity.getDelegate().registerKGFrameworkCallback(mMainController.lifecycleCallback)
    }

    private fun initLoadUiFramework() {
        mMainActivity.getDelegate().startAndRestoreFragments(
            MainFragmentContainer(),
            mMainActivity.getSavedInstanceState()
        )
    }
}