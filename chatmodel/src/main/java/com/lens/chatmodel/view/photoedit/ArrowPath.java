package com.lens.chatmodel.view.photoedit;

import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * 箭头的路径
 * 
 * @author yyw
 * 
 */
public class ArrowPath {
	private int sX, sY, eX, eY;
	private Paint paint;

	public int getsX() {
		return sX;
	}

	public void setsX(int sX) {
		this.sX = sX;
	}

	public int getsY() {
		return sY;
	}

	public void setsY(int sY) {
		this.sY = sY;
	}

	public int geteX() {
		return eX;
	}

	public void seteX(int eX) {
		this.eX = eX;
	}

	public int geteY() {
		return eY;
	}

	public void seteY(int eY) {
		this.eY = eY;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public ArrowPath(int sX, int sY, int eX, int eY, Paint paint) {
		super();
		this.sX = sX;
		this.sY = sY;
		this.eX = eX;
		this.eY = eY;
		this.paint = paint;
	}

	public ArrowPath() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void reset(Matrix matrix) {
		float[] values = new float[9];
		matrix.getValues(values);
		sX = (int) ((sX-values[Matrix.MTRANS_X])/values[Matrix.MSCALE_X]);
		eX = (int) ((eX-values[Matrix.MTRANS_X])/values[Matrix.MSCALE_X]);
		sY = (int) ((sY-values[Matrix.MTRANS_Y])/values[Matrix.MSCALE_Y]);
		eY = (int) ((eY-values[Matrix.MTRANS_Y])/values[Matrix.MSCALE_Y]);
	}

}
