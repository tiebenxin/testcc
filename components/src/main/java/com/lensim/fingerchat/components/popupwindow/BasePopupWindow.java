package com.lensim.fingerchat.components.popupwindow;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import com.lensim.fingerchat.components.R;


public abstract  class BasePopupWindow extends PopupWindow implements OnClickListener{
	
	protected Context mContext;
	protected View mContentView;
	
	@SuppressWarnings("deprecation")
	public BasePopupWindow(View contentView, int width, int height,
						   boolean focusable) {
		super(contentView, width, height, focusable);
		mContentView = contentView;
		mContext = contentView.getContext();
		setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.popupwindow_bg));
		setTouchable(true);
		setOutsideTouchable(true);
		setTouchInterceptor(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		initView();
		initData();
		initListener();
		
	}
	
	public void initListener() {	
	}
	public void initData() {
		// TODO Auto-generated method stub
		
	}
	public abstract void initView();
	public abstract void processClick(View v);
	@Override
	public void onClick(View v) {
		processClick(v);
		
	}
	public View findViewById(int id) {
		return mContentView.findViewById(id);
	}
	
}
