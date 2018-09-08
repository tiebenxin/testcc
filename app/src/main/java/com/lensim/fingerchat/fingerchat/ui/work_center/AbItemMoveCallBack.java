package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * date on 2018/2/12
 * author ll147996
 * describe
 */

public abstract class AbItemMoveCallBack extends ItemTouchHelper.Callback  {

    private boolean mDragEnabled = true; //是否能够通过长按切换位置

    private RecyclerView.ViewHolder viewHolder;


    @Override
    public boolean isLongPressDragEnabled() {
        setDragEnabled();
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
    public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
        if (target.getAdapterPosition() <= getDragFirstPosition(viewHolder)
            || target.getAdapterPosition() >= getDragLastPosition(viewHolder)) {
            return true;
        }
        onMoveDrag(recyclerView, viewHolder, target);
        return true;
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
    private float getLimitedDy(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dY) {
        return dY;
    }
    /**
     * 设置能否拖拽
     */
    private void setDragEnabled() {
        mDragEnabled = isDragEnabled(viewHolder);
    }


    /**
     * Item 切换位置时能移动的范围
     */
    abstract int getDragFirstPosition(RecyclerView.ViewHolder viewHolder);

    /**
     * Item 切换位置时能移动的范围
     */
    abstract int getDragLastPosition(RecyclerView.ViewHolder viewHolder);

    abstract boolean isDragEnabled(RecyclerView.ViewHolder viewHolder);

    abstract void onMoveDrag(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target);
}
