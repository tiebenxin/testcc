package com.lensim.fingerchat.hexmeet.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.components.adapter.BaseRecyclerAdapter;
import com.lensim.fingerchat.data.hexmeet.VideoMeeting;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.bean.VideoMeetingParticipants;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.utils.ScreenUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Lewis on 2017/10/10.
 * Description: LoadMore
 */

public class LoadMoreAdapter extends BaseRecyclerAdapter<LoadMoreAdapter.ItemVh, VideoMeeting> {

    private LayoutInflater mInflater;

    public LoadMoreAdapter(Context ctx) {
        super(ctx);
        this.mContext = ctx;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public ItemVh onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.hexmeet_conference_item, parent, false);
        return new ItemVh(view);
    }

    @Override
    public void onBindViewHolder(ItemVh holder, int position) {

        VideoMeeting meeting = items.get(position);
        holder.conf_name.setText(meeting.getMeetingName());
        holder.starttime.setTextColor(Color.parseColor("#fa8100"));
        holder.date.setTextColor(Color.parseColor("#fa8100"));
        holder.starttime_bg.setBackgroundResource(R.drawable.bg_status_approved_up);
        holder.date_bg.setBackgroundResource(R.drawable.bg_status_approved_down);
        holder.generator.setText(
            mContext.getResources().getString(R.string.generator) + " " + meeting
                .getMeetingCreater());

        long timestamp = TimeUtils.getTimeStamp(meeting.getMeetingStart());
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(timestamp);
        Calendar nowCalendar = Calendar.getInstance();
        holder.starttime.setText(new SimpleDateFormat("HH:mm").format(timestamp));

        int days = startCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR);
        if (days == 0) {
            holder.date.setText(mContext.getResources().getString(R.string.today));
        } else if (days == 1) {
            holder.date.setText(mContext.getResources().getString(R.string.tomorrow));
        } else if (days == -1) {
            holder.date.setText(mContext.getResources().getString(R.string.yesterday));
        } else {
            holder.date.setText(new SimpleDateFormat("MM-dd").format(timestamp));
        }

        if (days == 0 || days == 1 || days == -1) {
            if (App.isEnVersion()) {
                holder.date.setTextSize(8);
                holder.date.setPadding(0, 0, ScreenUtil.dp_to_px(3), ScreenUtil.dp_to_px(3));
            } else {
                holder.date.setTextSize(12);
                holder.date.setPadding(0, 0, ScreenUtil.dp_to_px(3), 0);
            }
        } else {
            holder.date.setTextSize(12);
            holder.date.setPadding(0, 0, ScreenUtil.dp_to_px(5), ScreenUtil.dp_to_px(3));
        }

        Gson gson = new Gson();
        String str = meeting.getMeetingParticipants();
        if (!StringUtils.isEmpty(str)) {
            List<VideoMeetingParticipants> peopleList = gson
                .fromJson(str, new TypeToken<ArrayList<VideoMeetingParticipants>>() {
                }.getType());
            int len = peopleList.size();
            ImageView avatars[] = new ImageView[]{holder.image0, holder.image1, holder.image2,
                holder.image3, holder.image4, holder.image5, holder.image6};
            int maxSize = avatars.length;
            for (int i = 0; i < maxSize; i++) {
                if (i >= len && len < maxSize) {
                    avatars[i].setVisibility(View.GONE);
                } else {
                    avatars[i].setVisibility(View.VISIBLE);
                    Glide.with(ContextHelper.getContext()).load(peopleList.get(i).getHeadPortrait())
                        .placeholder(R.drawable.icon_contact).into(avatars[i]);
                }
            }
        }
    }


    static class ItemVh extends BaseRecyclerAdapter.VH {

        LinearLayout starttime_bg;
        private TextView starttime;
        private LinearLayout date_bg;
        private TextView date;
        private TextView conf_name;
        private TextView generator;
        private ImageView image0;
        private ImageView image1;
        private ImageView image2;
        private ImageView image3;
        private ImageView image4;
        private ImageView image5;
        private ImageView image6;
        private TextView count;

        public ItemVh(View convertView) {
            super(convertView);
            starttime_bg = (LinearLayout) convertView.findViewById(R.id.starttime_bg);
            starttime = (TextView) convertView.findViewById(R.id.starttime);
            date_bg = (LinearLayout) convertView.findViewById(R.id.date_bg);
            date = (TextView) convertView.findViewById(R.id.date);
            conf_name = (TextView) convertView.findViewById(R.id.call_title);
            generator = (TextView) convertView.findViewById(R.id.generator);
            image0 = (ImageView) convertView.findViewById(R.id.image0);
            image1 = (ImageView) convertView.findViewById(R.id.image1);
            image2 = (ImageView) convertView.findViewById(R.id.image2);
            image3 = (ImageView) convertView.findViewById(R.id.image3);
            image4 = (ImageView) convertView.findViewById(R.id.image4);
            image5 = (ImageView) convertView.findViewById(R.id.image5);
            image6 = (ImageView) convertView.findViewById(R.id.image6);
            count = (TextView) convertView.findViewById(R.id.count);
        }

    }

}
