package com.eucsoft.foodex.twowaygrid.async;


import android.annotation.TargetApi;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

import com.eucsoft.foodex.twowaygrid.TwoWayAbsListView;
import com.eucsoft.foodex.twowaygrid.TwoWayAbsListView.OnScrollListener;

/*import com.eucsoft.foodex.twowaygrid.TwoWayAbsListView.OnItemSelectedListener;*/
/*import android.widget.ListView;*/

class ItemManaged {
    private final TwoWayAbsListView mTwoWayAbsListView;
    private ListAdapter mWrappedAdapter;
    private ItemManager mItemManager;

    private boolean mInstallingManager;

    private OnScrollListener mOnScrollListener;
    private OnTouchListener mOnTouchListener;
    /*private OnItemSelectedListener mOnItemSelectedListener;*/

    ItemManaged(TwoWayAbsListView absListView) {
        mTwoWayAbsListView = absListView;
        mWrappedAdapter = null;
        mItemManager = null;

        mInstallingManager = false;

        mOnScrollListener = null;
        mOnTouchListener = null;
        /*mOnItemSelectedListener = null;*/
    }

    boolean hasItemManager() {
        return (mItemManager != null);
    }

    void setItemManager(ItemManager itemManager) {
        // Ensure the whatever current manager is detached
        // from this managed component.
        if (mItemManager != null) {
            mItemManager.setItemManaged(null);
            mItemManager = null;
        }

        // This is to avoid holding a reference to ItemManager's
        // listeners here while installing the new manager.
        mInstallingManager = true;

        if (itemManager != null) {
            // It's important that mItemManager is null at this point so
            // that its listeners are set properly.
            itemManager.setItemManaged(this);

            // Make sure that we wrap whatever adapter has been set
            // before the item manager was installed.
            setAdapterOnView(wrapAdapter(itemManager, mWrappedAdapter));
        } else {
            // Restore the listeners set on the view before the item
            // manager was installed.
            mTwoWayAbsListView.setOnScrollListener(mOnScrollListener);
            mTwoWayAbsListView.setOnTouchListener(mOnTouchListener);
            /*mTwoWayAbsListView.setOnItemSelectedListener(mOnItemSelectedListener);*/

            // Remove wrapper adapter and re-apply the original one
            setAdapterOnView(mWrappedAdapter);
        }

        mItemManager = itemManager;
        mInstallingManager = false;
    }

    TwoWayAbsListView getAbsListView() {
        return mTwoWayAbsListView;
    }

    OnScrollListener getOnScrollListener() {
        return mOnScrollListener;
    }

    void setOnScrollListener(OnScrollListener l) {
        if (mInstallingManager) {
            return;
        }

        mOnScrollListener = l;
    }

    OnTouchListener getOnTouchListener() {
        return mOnTouchListener;
    }

    void setOnTouchListener(OnTouchListener l) {
        if (mInstallingManager) {
            return;
        }

        mOnTouchListener = l;
    }

   /* OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    void setOnItemSelectedListener(OnItemSelectedListener l) {
        if (mInstallingManager) {
            return;
        }

        mOnItemSelectedListener = l;
    }*/

    void cancelAllRequests() {
        if (mItemManager != null) {
            mItemManager.cancelAllRequests();
        }
    }

    ListAdapter getAdapter() {
        final ListAdapter adapter = mTwoWayAbsListView.getAdapter();
        if (adapter instanceof WrapperListAdapter) {
            WrapperListAdapter wrapperAdapter = (WrapperListAdapter) adapter;
            return wrapperAdapter.getWrappedAdapter();
        }

        return adapter;
    }

    ListAdapter wrapAdapter(ListAdapter adapter) {
        return wrapAdapter(mItemManager, adapter);
    }

    ListAdapter wrapAdapter(ItemManager itemManager, ListAdapter adapter) {
        mWrappedAdapter = adapter;

        if (itemManager != null && adapter != null) {
            adapter = new AsyncBaseAdapter(itemManager, (BaseAdapter) adapter);
        }

        return adapter;
    }

    @TargetApi(11)
    void setAdapterOnView(ListAdapter adapter) {
        mTwoWayAbsListView.setAdapter(adapter);
    }
}
