package com.brins.commom.toast;

import android.support.annotation.NonNull;
import android.widget.Toast;


/**
 * @author drakeet
 */
public interface BadTokenListener {

    void onBadTokenCaught(@NonNull Toast toast);
}
