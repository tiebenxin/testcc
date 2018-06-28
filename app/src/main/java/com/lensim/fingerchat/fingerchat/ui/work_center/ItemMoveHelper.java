package com.lensim.fingerchat.fingerchat.ui.work_center;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * date on 2018/2/12
 * author ll147996
 * describe
 */

public abstract class ItemMoveHelper extends ItemTouchHelper.Callback {

    /**
     * Item 切换位置时能移动的范围
     */
    abstract int setFirstPosition(RecyclerView.ViewHolder viewHolder);

    /**
     * Item 切换位置时能移动的范围
     */
    abstract int setLastPosition(RecyclerView.ViewHolder viewHolder);

    abstract boolean isDragEnabled(RecyclerView.ViewHolder viewHolder);

}
