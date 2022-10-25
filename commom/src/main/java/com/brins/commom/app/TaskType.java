package com.brins.commom.app;

import android.support.annotation.IntDef;

@IntDef({
        TaskType.TYPE_MAIN,
        TaskType.TYPE_MINI_APP1,
        TaskType.TYPE_MINI_APP2,
        TaskType.TYPE_MINI_APP3,
        TaskType.TYPE_MINI_APP4,
        TaskType.TYPE_MINI_APP5,
        TaskType.TYPE_KUGOU_PLAY,
        TaskType.TYPE_LONG_AUDIO,
        TaskType.TYPE_READ_NOVEL,
})
public @interface TaskType {

    int TYPE_MAIN      = 1;
    int TYPE_MINI_APP1 = 2;
    int TYPE_MINI_APP2 = 3;
    int TYPE_MINI_APP3 = 4;
    int TYPE_MINI_APP4 = 5;
    int TYPE_MINI_APP5 = 6;
    int TYPE_KUGOU_PLAY = 7;
    int TYPE_LONG_AUDIO = 8;
    int TYPE_READ_NOVEL = 9;
    int TYPE_UNKNOW = 0;
    int TYPE_ALL = -2;
}
