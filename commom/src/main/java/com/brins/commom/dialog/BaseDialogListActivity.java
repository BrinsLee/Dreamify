
package com.brins.commom.dialog;

import android.os.Handler;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.brins.commom.R;
import com.brins.commom.skin.SkinSetting;

/**
 * 通用标题Activity
 * 
 * @author HalZhang
 * @version 2011-11-24下午05:16:00
 */
public class BaseDialogListActivity extends BaseDialogActivity {

    private ListView mList;

    private Handler mHandler = new Handler();

    private ListAdapter mAdapter;

    private boolean mFinishedStart = false;


    public static final long ADD_TO_CLOUD_TYPE = -1L;

    public static final long ADD_FROM_PLAYERFRAGMENT = 0L;

    public static final long ADD_FORM_PLAYERQUEUE = 1L;

    public static final long ADD_TO_CLOUD_TYPE_MULTI = 2L; //多选

    /**
     * 添加到云音乐统计<br>
     * -1代表的是单曲，可以判断歌曲的类型<br>
     * 0代表的是本地音乐<br>
     * >0是收藏列表，要判断本地还是网络<br>
     * 其他归为网络榜单<br>
     */
    public static final String ADD_TO_CLOUD_TYPE_KEY = "addtype";

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mList = (ListView) findViewById(android.R.id.list);
        if (mList == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is "
                    + "'android.R.id.list'");
        }

        if (mFinishedStart) {
            setListAdapter(mAdapter);
        }
        mHandler.post(mRequestFocus);
        mFinishedStart = true;

    }

    private void ensureList() {
        if (mList != null) {
            return;
        }
        setContentView(R.layout.common_listview2);
    }

    public void setListAdapter(ListAdapter adapter) {
        synchronized (this) {
            ensureList();
            mAdapter = adapter;
            mList.setAdapter(adapter);
        }
    }

    private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    public ListView getListView() {
        ensureList();
        return mList;
    }

    /**
     * 刷新适配 moto 尼玛的！
     * 
     * @param adapter
     */
    protected void notifyDataSetChanged(BaseAdapter adapter) {
        if (adapter == null) {
            return;
        }
        adapter.notifyDataSetChanged();
        int listSize = mList.getHeaderViewsCount() + adapter.getCount()
                + mList.getFooterViewsCount();
        int listHeight = listSize
                * (int) (mContext.getResources().getDimension(R.dimen.list_item_height));
        ViewGroup.LayoutParams l = (ViewGroup.LayoutParams) mList.getLayoutParams();
        if (listHeight >= ((int) mContext.getResources().getDimension(R.dimen.dialog_height)
                - (int) mContext.getResources().getDimension(R.dimen.dialog_title_bar_height) - (int) mContext
                .getResources().getDimension(R.dimen.dialog_bottom_bar_height))) {
            l.height = ViewGroup.LayoutParams.FILL_PARENT;
        } else {
            l.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        mList.setLayoutParams(l);
    }

    @Override
    protected void onSkinColorChanged() {
        super.onSkinColorChanged();
        mList.setSelector(SkinSetting.getStateColorResDrawable(this));
    }
}
