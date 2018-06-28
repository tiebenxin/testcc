package com.lensim.fingerchat.components.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import com.lensim.fingerchat.components.R;


/**
 * 图片裁剪界面
 */
public class ClipView extends View {

	private Context mContext;

	public ClipView(Context context) {
		super(context);
		mContext = context;
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();

		Paint paint = new Paint();
		paint.setColor(0xaa000000);
		//画四个阴影框
		canvas.drawRect(0, 0, width, height / 4, paint);//上面
		canvas.drawRect(0, height / 4, (width - height / 2) / 2,//左边
				height * 3 / 4, paint);
		canvas.drawRect((width + height / 2) / 2, height / 4, width,
				height * 3 / 4, paint);
		canvas.drawRect(0, height * 3 / 4, width, height, paint);
		//noinspection deprecation
		paint.setColor(ContextCompat.getColor(mContext, R.color.white));
		//画截图框
		canvas.drawRect((width - height / 2) / 2 - 1, height / 4 - 1,
				(width + height / 2) / 2 + 1, (height / 4), paint);
		canvas.drawRect((width - height / 2) / 2 - 1, height / 4,
				(width - height / 2) / 2, height * 3 / 4, paint);
		canvas.drawRect((width + height / 2) / 2, height / 4,
				(width + height / 2) / 2 + 1, height * 3 / 4, paint);
		canvas.drawRect((width - height / 2) / 2 - 1, height * 3 / 4,
				(width + height / 2) / 2 + 1, height * 3 / 4 + 1, paint);
	}

}
