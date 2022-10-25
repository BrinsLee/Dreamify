package com.brins.commom.rxlifecycle;

/**
 * This is an exception that can be thrown to indicate that the caller has attempted to bind to a lifecycle outside
 * of its allowable window.
 */
public class OutsideLifecycleException extends IllegalStateException {

    public OutsideLifecycleException( String detailMessage) {
        super(detailMessage);
    }
}
