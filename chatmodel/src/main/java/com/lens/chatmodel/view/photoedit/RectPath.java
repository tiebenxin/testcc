package com.lens.chatmodel.view.photoedit;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 方
 * 
 * @author Administrator
 * 
 */
public class RectPath {
	private float left, top, right, bottom;
	private Paint paint;

	public float getLeft() {
		return left;
	}

	public void setLeft(float left) {
		this.left = left;
	}

	public float getTop() {
		return top;
	}

	public void setTop(float top) {
		this.top = top;
	}

	public float getRight() {
		return right;
	}

	public void setRight(float right) {
		this.right = right;
	}

	public float getBottom() {
		return bottom;
	}

	public void setBottom(float bottom) {
		this.bottom = bottom;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public RectPath() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RectPath(float left, float top, float right, float bottom, Paint paint) {
		super();
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
		this.paint = paint;
	}

	public void reset(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		left = (int) ((left - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		right = (int) ((right - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		top = (int) ((top - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
		bottom = (int) ((bottom - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
	}

}
