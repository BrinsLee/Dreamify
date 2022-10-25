package com.brins.commom.base;

import com.kugou.common.skinpro.widget.ISkinViewUpdate;

public interface ISkinUpdateManager {
    boolean addSkinUpdate(ISkinViewUpdate... updates);
    boolean removeSkinUpdate(ISkinViewUpdate update);
}
