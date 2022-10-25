package com.brins.commom.base.service;

import com.brins.arouter_api.Call;
import com.brins.commom.delegate.DelegateFragment;

/**
 * @author lipeilin
 * @date 2022/10/22
 * @desc
 */
public interface MineFragmentService extends Call {
    DelegateFragment provideMineFragment();
}
