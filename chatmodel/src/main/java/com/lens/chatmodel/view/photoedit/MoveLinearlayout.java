package com.lens.chatmodel.view.photoedit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lens.chatmodel.R;


/**
 * 可以移动的，可以自动改变方向的LinearLayout 只能用到RelativeLayout之中
 * 
 * @author yyw
 * 
 */
public class MoveLinearlayout extends LinearLayout implements OnTouchListener {
	private ImageView headView;
	private int left, top;
	private boolean change;
	private int parentTop, parentLeft, parentRight, parentBottom;
	private boolean changeOrietation;
	private Drawable d;

	public MoveLinearlayout(Context context) {
		super(context);
		init(context);
	}

	public MoveLinearlayout(Context context, AttributeSet attrs) {
		super(context);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MoveLinearlayout);
		changeOrietation = a.getBoolean(R.styleable.MoveLinearlayout_changeOrietation, false);
		d = a.getDrawable(R.styleable.MoveLinearlayout_src);
		a.recycle();
		init(context);

	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		headView = new ImageView(context);
		if (d != null) {
			headView.setImageDrawable(d);
		} else {
			headView.setImageResource(R.drawable.ease_default_image);
		}
		addView(headView, 0);
		LayoutParams params = (LayoutParams) headView.getLayoutParams();
		params.gravity = Gravity.CENTER;
		headView.setLayoutParams(params);
		headView.setOnTouchListener(this);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:// 手指第一次触摸到屏幕
			left = (int) event.getRawX();
			top = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:// 手指移动
			int newX = (int) event.getRawX();
			int newY = (int) event.getRawY();
			int dx = newX - left;
			int dy = newY - top;
			// 计算出来控件原来的位置
			int l = this.getLeft();
			int r = this.getRight();
			int t = this.getTop();
			int b = this.getBottom();

			int newt = t + dy;
			int newb = b + dy;
			int newl = l + dx;
			int newr = r + dx;
			if (newl < parentLeft || newt < parentTop || newr > parentRight || newb > parentBottom) {
				if (changeOrietation) {
					if (newr > parentRight && newt + getChildHeight(this) < parentBottom && getOrientation() == HORIZONTAL) {
						this.setOrientation(VERTICAL);
						setChildOrientation(VERTICAL);
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
						params.leftMargin = l;
						params.topMargin = t;
						setLayoutParams(params);
					} else if (newb > parentBottom && getChildWidth(this) + newl < parentRight && getOrientation() == VERTICAL) {
						this.setOrientation(HORIZONTAL);
						setChildOrientation(HORIZONTAL);
						RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getLayoutParams();
						params.leftMargin = l;
						params.topMargin = t;
						setLayoutParams(params);
					}
				}
				break;
			}
			layout(newl, newt, newr, newb);
			// 更新iv在屏幕的位置.
			left = (int) event.getRawX();
			top = (int) event.getRawY();
			break;
		case MotionEvent.ACTION_UP: // 手指离开屏幕的一瞬间
			break;
		}
		return true;
	}
	/**
	 * 把子View中的LinearLayout布局设置方向
	 * @param vertical
	 */
	private void setChildOrientation(int vertical) {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) instanceof LinearLayout) {
				LinearLayout chiLayout = (LinearLayout) getChildAt(i);
				chiLayout.setOrientation(vertical);
			}
		}

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!change) {
			if (!(getParent() instanceof RelativeLayout)) {
				throw new RuntimeException("父布局不是RelativeLayout");
			}
			RelativeLayout parent = (RelativeLayout) getParent();
			parentTop = parent.getTop();
			parentRight = parent.getRight();
			parentBottom = parent.getBottom();
			parentLeft = parent.getLeft();
			change = true;
		}
		super.onLayout(changed, l, t, r, b);
	}
	/**
	 * 获取子View的宽度和
	 * @param layout
	 * @return
	 */
	private int getChildWidth(LinearLayout layout) {
		int width = 0;
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof LinearLayout) {
				LinearLayout chiLayout = (LinearLayout) child;
				width += getChildWidth(chiLayout);
			} else {
				MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
				width = width + child.getWidth() + params.rightMargin + params.leftMargin;
			}
		}
		return width;
	}
	/**
	 * 获取布局的所有的子View的高度和
	 * @param layout
	 * @return
	 */
	private int getChildHeight(LinearLayout layout) {
		int height = 0;
		for (int i = 0; i < layout.getChildCount(); i++) {
			View child = layout.getChildAt(i);
			if (child instanceof LinearLayout) {
				LinearLayout chiLayout = (LinearLayout) child;
				height += getChildHeight(chiLayout);
			} else {
				MarginLayoutParams params = (MarginLayoutParams) child.getLayoutParams();
				height = height + child.getWidth() + params.topMargin + params.bottomMargin;
			}
		}
		return height;
	}

	public boolean isChangeOrietation() {
		return changeOrietation;
	}

	public void setChangeOrietation(boolean changeOrietation) {
		this.changeOrietation = changeOrietation;
	}

	public void setHeadViewImageResource(int resId) {
		headView.setImageResource(resId);
	}

	public void setHeadViewImage(Bitmap bitmap) {
		headView.setImageBitmap(bitmap);
	}
}
