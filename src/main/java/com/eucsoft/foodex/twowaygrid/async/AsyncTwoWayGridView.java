package com.eucsoft.foodex.twowaygrid.async;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.eucsoft.foodex.twowaygrid.TwoWayAbsListView;
import com.eucsoft.foodex.twowaygrid.TwoWayGridView;

public class AsyncTwoWayGridView extends TwoWayGridView {

    private final ItemManaged mItemManaged;

    public AsyncTwoWayGridView(Context context) {
        this(context, null);
    }

    public AsyncTwoWayGridView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.gridViewStyle);
    }

    public AsyncTwoWayGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mItemManaged = new ItemManaged(this);
    }

    public void setItemManager(ItemManager itemManager) {
        mItemManaged.setItemManager(itemManager);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mItemManaged.cancelAllRequests();
    }

    @Override
    public void setOnScrollListener(TwoWayAbsListView.OnScrollListener l) {
        mItemManaged.setOnScrollListener(l);
        if (!mItemManaged.hasItemManager()) {
            super.setOnScrollListener(l);
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mItemManaged.setOnTouchListener(l);
        if (!mItemManaged.hasItemManager()) {
            super.setOnTouchListener(l);
        }
    }

/*    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener l) {
        mItemManaged.setOnItemSelectedListener(l);
        if (!mItemManaged.hasItemManager()) {
            super.setOnItemSelectedListener(l);
        }
    }*/

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(mItemManaged.wrapAdapter(adapter));
    }
}
