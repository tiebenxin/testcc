package com.lensim.fingerchat.fingerchat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author LY309313
 *
 */
public class CustomTableView extends LinearLayout {
	
	private int cols = 4;
	private Context mContext;
	private int spaceH = (int) TDevice.dpToPixel(5);
	private int spaceV = (int) TDevice.dpToPixel(5);
	private List<String> names = new ArrayList<>();


	public CustomTableView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public CustomTableView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
		mContext = context;
	}

	public CustomTableView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	private void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTableView);
		int count = typedArray.length();
		for(int i=0;i<count;i++){
			int attr = typedArray.getIndex(i);
			switch (attr) {
			case R.styleable.CustomTableView_numCols:
				cols = typedArray.getInt(attr, 3);
				break;
			case R.styleable.CustomTableView_spaceH:
				spaceH = (int) typedArray.getDimension(attr, TDevice.dpToPixel(5));
				break;
			case R.styleable.CustomTableView_spaceV:
				spaceV = (int) typedArray.getDimension(attr, TDevice.dpToPixel(5));
				break;
			default:
				break;
			}
		}
		typedArray.recycle();
		this.setOrientation(LinearLayout.VERTICAL);
	}
	
	public List<String> getData() {
		return names;
	}

	public void setData(List<String> names) {
		if(names == null){
			return;
		}
		int count = names.size() + 1;
		int rawCount = count / cols;
		int lastCount = count % cols;//余数为不为零
		LayoutParams rawParams = new LayoutParams(LayoutParams.MATCH_PARENT,0,1);
		LayoutParams colParams = new LayoutParams(0, LayoutParams.MATCH_PARENT,1);
		
		//int total = 
		for(int i=0;i<rawCount;i++){
			LinearLayout rawContainer = new LinearLayout(getContext());
			rawContainer.setOrientation(LinearLayout.HORIZONTAL);
			addView(rawContainer, rawParams);
			for(int j=0;j<cols;j++){
				LinearLayout colContainer = new LinearLayout(mContext);
				colContainer.setOrientation(LinearLayout.VERTICAL);
				rawContainer.addView(colContainer, colParams);
			}
		}
	}

}
