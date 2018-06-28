package com.lens.chatmodel.view.photoedit;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 椭圆
 * @author Administrator
 *
 */
public class OvalPath {
	private RectF rectF;
	private Paint paint;

	public RectF getRectF() {
		return rectF;
	}

	public void setRectF(RectF rectF) {
		this.rectF = rectF;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public OvalPath() {
		super();
		// TODO Auto-generated constructor stub
	}

	public OvalPath(RectF rectF, Paint paint) {
		super();
		this.rectF = rectF;
		this.paint = paint;
	}

	public void reset(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		rectF.left = (int) ((rectF.left - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		rectF.right = (int) ((rectF.right - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		rectF.top = (int) ((rectF.top - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
		rectF.bottom = (int) ((rectF.bottom - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
		
	}

}
