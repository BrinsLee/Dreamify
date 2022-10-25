package com.brins.commom.dialog;

/**
 * Created by buronehuang on 2019/1/18.
 */

public interface DocileInterface {

    /**
     * asking to show with Dialog-Regulate-System,
     * with default authority. It will be shown
     * after current showing dialog dismiss.
     */
    void askShow();

    /**
     * asking to show with Dialog-Regulate-System,
     * authority will be helpful in reordering if
     * there is a waiting queue. high authority
     * means a earlier show.
     *
     * @param authority value defined in {@link DialogAuthority}
     */
    void askShow(float authority);

    /**
     * clear the dialogs behind me in the same
     * queue at this moment.
     */
    void clearBehinds();

}
