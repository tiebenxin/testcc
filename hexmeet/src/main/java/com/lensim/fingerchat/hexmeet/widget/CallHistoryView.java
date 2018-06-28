package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CallHistoryView extends View {

  private String peerSip;

  private static final int ICON_OFFSET = ScreenUtil.dp_to_px(3);

  private static final int START_MARGIN = ScreenUtil.dp_to_px(15);
  private static final int END_MARGIN = ScreenUtil.dp_to_px(15);

  private static final int CALL_DATE_HEIGHT = ScreenUtil.dp_to_px(15);
  private static final int CALL_RECORD_DATA_HEIGHT = ScreenUtil.dp_to_px(13);

  private static final int FIRST_DATE_GAP = ScreenUtil.dp_to_px(20);
  private static final int CALL_RECORD_LINE_GAP = ScreenUtil.dp_to_px(9);
  private static final int NORMAL_DATE_GAP = ScreenUtil.dp_to_px(27);

  private static final int DATE_LINE_HEIGHT = NORMAL_DATE_GAP + CALL_DATE_HEIGHT;
  private static final int CALL_RECORD_LINE_HEIGHT = CALL_RECORD_LINE_GAP + CALL_RECORD_DATA_HEIGHT;

  private static final int TIME_X_START = START_MARGIN + CALL_RECORD_DATA_HEIGHT + CALL_RECORD_LINE_GAP;
  private static final int TYPE_X_START = TIME_X_START + ScreenUtil.dp_to_px(90);
  private final int DURATION_X_END = ScreenUtil.getWidth(getContext()) - END_MARGIN;

  private Paint p_yyyymmdd;
  private Paint p_icon;
  private Paint p_hhmm;
  private Paint p_text_missed;
  private Paint p_text_normal;

  private SimpleDateFormat sdf_date;
  private SimpleDateFormat sdf_time;

  private Bitmap videoIcon;
  private Bitmap audioIcon;
  private Rect srcIconRect;
  private Rect dstIconRect = new Rect();

  private String MISSED = getContext().getResources().getString(R.string.missed);
  private String DIAL_IN = getContext().getResources().getString(R.string.dial_in);
  private String DIAL_OUT = getContext().getResources().getString(R.string.dial_out);
  private String HOUR = getContext().getResources().getString(R.string.duration_hour);
  private String MIN = getContext().getResources().getString(R.string.druation_minute);
  private String SEC = getContext().getResources().getString(R.string.duration_second);

  public CallHistoryView(Context context) {
    super(context);
    init();
  }

  public CallHistoryView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    p_yyyymmdd = new Paint(Paint.ANTI_ALIAS_FLAG);
    p_yyyymmdd.setColor(Color.parseColor("#313131"));
    p_yyyymmdd.setTextSize(CALL_DATE_HEIGHT);
    p_yyyymmdd.setFakeBoldText(true);

    p_icon = new Paint(Paint.ANTI_ALIAS_FLAG);

    p_hhmm = new Paint(Paint.ANTI_ALIAS_FLAG);
    p_hhmm.setColor(Color.parseColor("#919191"));
    p_hhmm.setTextSize(CALL_RECORD_DATA_HEIGHT);

    p_text_missed = new Paint(Paint.ANTI_ALIAS_FLAG);
    p_text_missed.setColor(Color.parseColor("#f04848"));
    p_text_missed.setTextSize(CALL_RECORD_DATA_HEIGHT);

    p_text_normal = new Paint(Paint.ANTI_ALIAS_FLAG);
    p_text_normal.setColor(Color.parseColor("#919191"));
    p_text_normal.setTextSize(CALL_RECORD_DATA_HEIGHT);

    sdf_date = new SimpleDateFormat(this.getResources().getString(R.string.date_format), Locale.SIMPLIFIED_CHINESE);
    sdf_date.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

    sdf_time = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
    sdf_time.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

    videoIcon = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.icon_video_gray);
    audioIcon = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.icon_audio_gray);
    srcIconRect = new Rect(0, 0, videoIcon.getWidth(), videoIcon.getHeight());
    dstIconRect.left = START_MARGIN;
    dstIconRect.right = START_MARGIN + CALL_RECORD_DATA_HEIGHT;
  }

  public void setPeerSip(String peerSip) {
    this.peerSip = peerSip;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    final List<RestCallRow_> callRecords = CallRecordManager.getCallRecords(peerSip);
    if (callRecords == null || callRecords.size() == 0) {
      return;
    }

    int y = getTop();
    int new_day = 0;
    for (RestCallRow_ record : callRecords) {
      Calendar c = Calendar.getInstance();
      c.setTimeInMillis(record.getStartTime());
      int day = c.get(Calendar.DAY_OF_YEAR);
      if (new_day != day) {
        y += (new_day == 0 ? FIRST_DATE_GAP + CALL_DATE_HEIGHT : DATE_LINE_HEIGHT);
        new_day = day;
        canvas.drawText(sdf_date.format(record.getStartTime()), START_MARGIN, y, p_yyyymmdd);
      }

      dstIconRect.top = y + CALL_RECORD_LINE_GAP + ICON_OFFSET;
      dstIconRect.bottom = y + CALL_RECORD_LINE_GAP + CALL_RECORD_DATA_HEIGHT + ICON_OFFSET;
      canvas.drawBitmap(record.getIsVideoCall() ? videoIcon : audioIcon, srcIconRect, dstIconRect, p_icon);

      y += CALL_RECORD_LINE_HEIGHT;
      canvas.drawText(sdf_time.format(record.getStartTime()), TIME_X_START, y, p_hhmm);

      if (record.getIsOutgoing()) {
        canvas.drawText(DIAL_OUT, TYPE_X_START, y, p_text_normal);
        if (record.getDuration() > 1) {
          String duration = getDuration(record.getDuration());
          canvas.drawText(duration, DURATION_X_END - p_text_normal.measureText(duration), y, p_text_normal);
        }
      } else {
        if (record.getDuration() <= 1) {
          canvas.drawText(MISSED, TYPE_X_START, y, p_text_missed);
        } else {
          canvas.drawText(DIAL_IN, TYPE_X_START, y, p_text_normal);
          String duration = getDuration(record.getDuration());
          canvas.drawText(duration, DURATION_X_END - p_text_normal.measureText(duration), y, p_text_normal);
        }
      }
    }

    int contentTotalHeight = y + FIRST_DATE_GAP;
    if (contentTotalHeight > ScreenUtil.dp_to_px(100)) {
      LayoutParams params = getLayoutParams();
      params.height = contentTotalHeight;
      setLayoutParams(params);
    }
  }

  private String getDuration(long duration) {
    long hour = duration / 3600000;
    long minute = duration % 3600000 / 60000;
    long second = duration % 60000 / 1000;

    if (hour > 0) {
      return hour + HOUR + minute + MIN + second + SEC;
    }

    if (minute > 0) {
      return minute + MIN + second + SEC;
    }

    return second + SEC;
  }
}
