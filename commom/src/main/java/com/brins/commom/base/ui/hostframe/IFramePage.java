package com.brins.commom.base.ui.hostframe;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by buronehuang on 2018/5/4.
 *
 * 代表所有工作在UI框架中的IKugouPage.
 * 其所处的Window级别容器是IFrameFace
 *
 * 因为处于UI框架，它有自己的和框架相关的特性；
 *
 * 分为外页和内页两种类型：
 * 外页：AbsFrameworkFragment，受控于UI框架
 * 内页：InnerPage，工作在外页中
 */

public interface IFramePage {

    void startFragment(Class<? extends Fragment> cls, Bundle args);

    void startFragment(Class<? extends Fragment> cls, Bundle args, boolean anim);

    void showPlayerFragment(boolean anim);

    void showPlayerFragment(boolean anim,Bundle bundle);

    boolean isPlayerFragmentShowing();

    boolean isMenuOpen();
}
