package com.brins.commom.base.ui;

import android.view.KeyEvent;

/**
 * Created by burone on 2017/7/27.
 *
 * 代表所有工作于Window内的ui元素
 */

public interface IKugouPage extends KeyEvent.Callback {

    boolean isAlive();

}
