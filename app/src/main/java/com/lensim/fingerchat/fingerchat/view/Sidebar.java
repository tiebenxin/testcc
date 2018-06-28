package com.lensim.fingerchat.fingerchat.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.contacts.AdapterContacts;


public class Sidebar extends View {

    private Paint paint;
    private TextView tv_title;
    private float height;
    private RecyclerView mRecyclerView;
    private Context context;

    private SectionIndexer sectionIndexter = null;
    private RelativeLayout.LayoutParams layoutParams;
    private int totalSize;

    public void setListView(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }


    public Sidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private String[] sections;

    private void init() {
//    String st = context.getString(R.string.search);
        sections = new String[]{"星", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
        totalSize = sections.length;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#8C8C8C"));
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(TDevice.sp2px(10));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float center = getWidth() / 2;
        height = getHeight() / sections.length;
        for (int i = sections.length - 1; i > -1; i--) {
            canvas.drawText(sections[i], center, height * (i + 1), paint);
        }
    }

    private int sectionForPoint(float y) {
        int index = (int) (y / height);
        if (index < 0) {
            index = 0;
        }
        if (index > sections.length - 1) {
            index = sections.length - 1;
        }
        return index;
    }

    private void setHeaderTextAndscroll(MotionEvent event) {
        if (mRecyclerView == null) {
            return;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        String headerString = sections[sectionForPoint(event.getY())];
        if (tv_title != null) {
            tv_title.setText(headerString);
            scrollHead(sectionForPoint(event.getY()));
        }
        if (sectionIndexter == null) {
            if (adapter instanceof SectionIndexer) {
                sectionIndexter = (SectionIndexer) adapter;
            } else {
                throw new RuntimeException(
                    "listview sets adpater does not implement SectionIndexer interface");
            }
        }
        String[] adapterSections = (String[]) sectionIndexter.getSections();
        try {
            for (int i = adapterSections.length - 1; i > -1; i--) {
                if (adapterSections[i].equals(headerString)) {
                    mRecyclerView.scrollToPosition(sectionIndexter.getPositionForSection(i));
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("setHeaderTextAndscroll", e.getMessage());
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (tv_title == null) {
                    tv_title = (TextView) ((View) getParent()).findViewById(R.id.tv_pop);
                }
                setHeaderTextAndscroll(event);
                tv_title.setVisibility(View.VISIBLE);
                setBackgroundResource(R.drawable.shape_sidebar_bg);
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                setHeaderTextAndscroll(event);
                return true;
            }
            case MotionEvent.ACTION_UP:
                tv_title.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
            case MotionEvent.ACTION_CANCEL:
                tv_title.setVisibility(View.INVISIBLE);
                setBackgroundColor(Color.TRANSPARENT);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void scrollHead(int position) {
        int h;
        if (position > totalSize - 4 && position <= totalSize - 1) {//气泡有高低，如果到到最底部，会压缩变形
            position = totalSize - 4;
            h = (int) (height * (position - 1) + height / 2);
        } else {
            h = (int) (height * (position - 1) + height / 2);
        }
        int w = (int) getX() - 2 * getMeasuredWidth();
        if (layoutParams != null) {
            layoutParams.topMargin = h;
            layoutParams.leftMargin = w;
            tv_title.setLayoutParams(layoutParams);
        }

    }

    public void setLayoutParams(RelativeLayout.LayoutParams params) {
        layoutParams = params;
    }

}
