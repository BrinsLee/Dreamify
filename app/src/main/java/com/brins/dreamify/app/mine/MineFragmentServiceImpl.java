package com.brins.dreamify.app.mine;

import android.content.Context;
import com.brins.arouter_annotation.ARouter;
import com.brins.commom.base.service.MineFragmentService;
import com.brins.commom.delegate.DelegateFragment;

import static com.brins.commom.constant.PathKt.APP_MINE_FRAGMENT;

/**
 * @author lipeilin
 * @date 2022/10/22
 * @desc
 */
@ARouter(path = "/app/provideMineFragment")
public class MineFragmentServiceImpl implements MineFragmentService {
    @Override public DelegateFragment provideMineFragment() {
        return new MineMainFragment();
    }

}
