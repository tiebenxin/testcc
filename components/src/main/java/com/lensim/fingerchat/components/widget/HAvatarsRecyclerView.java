package com.lensim.fingerchat.components.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.lensim.fingerchat.commons.utils.L;

/**
 * Created by LY309313 on 2017/2/4.
 */

public class HAvatarsRecyclerView extends RecyclerView {

    private OnBackListener listener;
    public HAvatarsRecyclerView(Context context) {
        super(context);
    }

    public HAvatarsRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        L.i("MyRecyclerView","重新测量");
        int childCount = getChildCount();
        if(childCount > 0){
            View child = getChildAt(0);
            int width = child.getMeasuredWidth();
            int w;
            if(childCount <= 5)
             w = MeasureSpec.makeMeasureSpec(width*childCount,MeasureSpec.EXACTLY);
            else{
                w = MeasureSpec.makeMeasureSpec(width*5,MeasureSpec.EXACTLY);
            }
            setMeasuredDimension(w,getMeasuredHeight());
        }else{
            setMeasuredDimension(0,getMeasuredHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        L.i("MyRecyclerView","重新布局");
    }

    private int flag;
    private boolean canDelete;

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
        if(!canDelete){
            flag = 0;
            if(listener!=null)
            listener.onDelCancel();
        }
    }

    public void delContact(){
        if(!canDelete)return;
        if(flag == 1){
            flag = 0;
            if(listener!=null){
                listener.onDel();
            }
            return;
        }
        if(listener!=null){
            listener.onPreDel();
        }
        flag++;
    }

    public void setListener(OnBackListener listener) {
        this.listener = listener;
    }


    public interface OnBackListener{

        void onPreDel();

        void onDel();

        void onDelCancel();
    }
}
