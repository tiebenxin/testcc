package com.lens.chatmodel.view.photoedit;

import android.graphics.Paint;
import android.graphics.Path;

public class LinePath {
	private Path path;
	private Paint paint;

	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
		
	}

	public LinePath(Path path, Paint paint) {
		super();
		this.path = path;
		this.paint = paint;
	}

	public LinePath() {
		super();
	}

}
