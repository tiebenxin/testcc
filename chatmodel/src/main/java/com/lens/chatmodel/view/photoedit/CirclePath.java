package com.lens.chatmodel.view.photoedit;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 圆
 * 
 * @author Administrator
 * 
 */
public class CirclePath {
	private float cx, cy, radius;
	private Paint paint;

	public float getCx() {
		return cx;
	}

	public void setCx(float cx) {
		this.cx = cx;
	}

	public float getCy() {
		return cy;
	}

	public void setCy(float cy) {
		this.cy = cy;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public CirclePath(float cx, float cy, float radius, Paint paint) {
		super();
		this.cx = cx;
		this.cy = cy;
		this.radius = radius;
		this.paint = paint;
	}

	public CirclePath() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void reset(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		cx = (int) ((cx - values[Matrix.MTRANS_X]) / values[Matrix.MSCALE_X]);
		cy = (int) ((cy - values[Matrix.MTRANS_Y]) / values[Matrix.MSCALE_Y]);
		radius = radius / values[Matrix.MSCALE_X];

	}

}
