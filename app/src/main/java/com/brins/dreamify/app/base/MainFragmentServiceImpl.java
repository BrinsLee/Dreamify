package com.brins.dreamify.app.base;


import com.brins.arouter_annotation.ARouter;
import com.brins.commom.base.service.MainFragmentService;
import com.brins.commom.base.service.MineFragmentService;
import com.brins.commom.delegate.DelegateFragment;

/**
 * @author lipeilin
 * @date 2022/10/22
 * @desc
 */
@ARouter(path = "/app/provideMineFragment")
public class MainFragmentServiceImpl implements MainFragmentService {
    @Override public DelegateFragment provideMainFragment() {
        return new TingMainFragment();
    }
}
