package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * date on 2018/2/12
 * author ll147996
 * describe
 */

public abstract class AbItemMoveCallBack extends ItemMoveHelper  {

    private boolean mDragEnabled = true; //是否能够通过长按切换位置
    private int mDragFirstPosition; //能够拖拽范围的第一个位置
    private int mDragLastPosition; //能够拖拽范围的最后一个位置

    RecyclerView.ViewHolder viewHolder;


    @Override
    public boolean isLongPressDragEnabled() {
        setDragEnabled();
        setDragFirstPosition();
        setDragLastPosition();
        return mDragEnabled ;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        this.viewHolder = viewHolder;
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;//拖拽
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }


    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
        float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    /**
     * 根据方向和条件获取限制在RecyclerView内部的DY值
     *
     * @param recyclerView 列表
     * @param viewHolder   drag的ViewHolder
     * @param dY           限制前的DY值
     * @return 限制后的DY值
     */
//    private float getLimitedDy(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dY) {
//        return dY;
//    }
    /**
     * 设置能否拖拽
     */
    private void setDragEnabled() {
        mDragEnabled = isDragEnabled(viewHolder);
    }
    /**
     * 设置拖拽范围的第一个位置
     */
    private void setDragFirstPosition() {
        mDragFirstPosition = setFirstPosition(viewHolder);
    }
    /**
     * 设置拖拽范围的最后一个位置
     */
    private void setDragLastPosition() {
        mDragLastPosition = setLastPosition(viewHolder);
    }

}
