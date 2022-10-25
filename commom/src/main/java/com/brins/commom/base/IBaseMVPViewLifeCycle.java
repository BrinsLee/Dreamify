package com.brins.commom.base;

public interface IBaseMVPViewLifeCycle {
    public static final String BASE_MVP_TAG = "BASE_MVP_TAG";
    void onStart();
    void onResume();
    void onFragmentResume();
    void onPause();
    void onFragmentPause();
    void onStop();
    void onDestroy();
}
