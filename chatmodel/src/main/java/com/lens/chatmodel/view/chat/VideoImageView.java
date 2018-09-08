package com.lens.chatmodel.view.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.utils.TDevice;

public class VideoImageView extends AppCompatImageView {

	private Context context;
	private Bitmap iconBitmap;


	public VideoImageView(Context context) {
		super(context);
		this.context = context;
		init();

	}

	public VideoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public VideoImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}
	private void init(){

	}

	public void setVideobg(int res){
		Bitmap bg = BitmapFactory.decodeResource(getResources(),
						res);
		iconBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.video_override);
		Bitmap roundCornerImage = getRoundCornerImage(bg, iconBitmap);
		setImageBitmap(roundCornerImage);
	}



	public Bitmap getRoundCornerImage(Bitmap bitmap_bg, Bitmap bitmap_in) {
		int width = (int) TDevice.dpToPixel(240);

		int height = (int) TDevice.dpToPixel(180);
//		int width = bitmap_in.getWidth();
//		int height = bitmap_in.getHeight();
//		if(height != 0){
//		double scale = (width * 1.00) / height;
//		if (width >= height) {
//			width = getBitmapWidth();
//			height = (int) (width / scale);
//		} else {
//			height = getBitmapHeight();
//			width = (int) (height * scale);
//		}
//		}else{
//			width = 100;
//			height = 100;
//		}
		Bitmap roundConcerImage = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(roundConcerImage);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, width, height);
		Rect rectF = new Rect(0, 0, width, height);
		paint.setAntiAlias(true);
		NinePatch patch = new NinePatch(bitmap_bg,
				bitmap_bg.getNinePatchChunk(), null);
		patch.draw(canvas, rect);
		//paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
		canvas.drawBitmap(bitmap_in, rectF, rect, paint);

//		NinePatch patch1 = new NinePatch(bitmap_in,
//				bitmap_in.getNinePatchChunk(), null);
//		patch1.draw(canvas, rect);
		return roundConcerImage;
	}

	// 获取屏幕的宽度
	@SuppressWarnings("deprecation")
	public int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	// 获取屏幕的高度
	@SuppressWarnings("deprecation")
	public int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public int getBitmapWidth() {
		return getScreenWidth(context) / 3;
	}

	public int getBitmapHeight() {
		return getScreenHeight(context) / 4;
	}
}
