package com.brins.commom.dialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by buronehuang on 2019/1/18.
 */

public class DocileProgressDialog extends ProgressDialog implements DocileInterface {

    private final DialogRegulator.DocileMember member = new DialogRegulator.DocileMember(this);

    public DocileProgressDialog(Context context) {
        super(context);
    }

    public DocileProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    public final void askShow() {
        DialogAuthorityAnnotation ant = DocileProgressDialog.this.getClass()
                .getAnnotation(DialogAuthorityAnnotation.class);
        float authority = ant == null ? DialogAuthority.ORDER : ant.authority();
        askShow(authority);
    }

    @Override
    public final void askShow(float authority) {
        member.pushToShow(authority);
    }

    @Override
    public final void clearBehinds() {
        member.clearSubsequents();
    }

    @Override
    protected void onStop() {
        super.onStop();
        member.notifyStop();
    }

}
