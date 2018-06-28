package com.lensim.fingerchat.fingerchat.ui.work_center.sign;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.widget.TimelineView;
import com.lensim.fingerchat.data.work_center.SignInJsonRet;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ItemTimelineBinding;
import com.lensim.fingerchat.fingerchat.ui.work_center.sign.TimeLineAdapter.TimeLineViewHolder;
import java.util.List;

/**
 * Created by HP-HP on 05-12-2015.
 *
 */
public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> implements
    View.OnClickListener {

    private List<SignInJsonRet> mFeedList;
    private Context mContext;
    private Orientation mOrientation;
    private boolean mWithLinePadding;
    private OnClockItemClickListener mOnItemClickListener = null;


    public TimeLineAdapter(List<SignInJsonRet> feedList, Orientation orientation,
        boolean withLinePadding) {
        mFeedList = feedList;
        mOrientation = orientation;
        mWithLinePadding = withLinePadding;
    }

    public void setData(List<SignInJsonRet> feedList) {
        mFeedList = feedList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        ItemTimelineBinding ui = DataBindingUtil.inflate(mLayoutInflater, R.layout.item_timeline, parent, false);
//        View view = mLayoutInflater.inflate(R.layout.item_timeline, parent, false);
        View view = ui.getRoot();
        view.setOnClickListener(this);
        return new TimeLineViewHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(TimeLineViewHolder holder, int position) {
        ItemTimelineBinding ui = DataBindingUtil.getBinding(holder.itemView);
        SignInJsonRet item = mFeedList.get(position);
        try {
            if (item.getmStatus() == ClockInRecordActivity.STATUS_TODAY) {
                ui.timeMarker
                    .setMarker(ContextCompat.getDrawable(mContext, R.drawable.timeline_marker),
                        ContextCompat
                            .getColor(mContext, R.color.orange));
            } else {
                ui.timeMarker
                    .setMarker(ContextCompat.getDrawable(mContext, R.drawable.timeline_marker),
                        ContextCompat
                            .getColor(mContext, R.color.clock_in_dialog_address));
            }

            if (item.isFirstDay()) {
                ui.timeMarker.setMarkerCenter(false);
                ui.llTimelineDate.setVisibility(View.VISIBLE);
                ui.tvClockInDate.setText(TimeUtils
                    .getDateStringSign(TimeUtils.getTimeStampNoSeconds(item.getSignInTime()) + ""));
            } else {
                ui.llTimelineDate.setVisibility(View.GONE);
                ui.timeMarker.setMarkerCenter(true);
            }

            ui.textTimelineDate.setText(mContext.getString(R.string.sign_in_outer, TimeUtils
                .getDateHourString(TimeUtils.getTimeStampNoSeconds(item.getSignInTime()) + "")));
            if (!StringUtils.isEmpty(item.getLocationData())) {
                String[] address = item.getLocationData().split(",");
                ui.textTimelineTitle.setText(mContext.getString(R.string.sign_in_address, address[1]));
            }
            if (!StringUtils.isEmpty(item.getTPSignIn())) {
                String[] imgs = item.getTPSignIn().split("@");
                if (imgs[0].toLowerCase().startsWith("hnlensimage") || imgs[0].toLowerCase()
                    .startsWith("/hnlensimage")) {
                    imgs[0] = Route.Host + imgs[0];
                }
                Glide.with(mContext).load(imgs[0]).centerCrop().into(ui.imgItemPic);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return (mFeedList != null ? mFeedList.size() : 0);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public void setOnItemClickListener(OnClockItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //define interface
    public static interface OnClockItemClickListener {

        void onItemClick(View view, int position);
    }

    public class TimeLineViewHolder extends RecyclerView.ViewHolder {

        public TimeLineViewHolder(View itemView, int viewType) {
            super(itemView);
            ItemTimelineBinding ui = DataBindingUtil.getBinding(itemView);
            ui.timeMarker.initLine(viewType);
            UIHelper.setTextSize(14, ui.textTimelineDate, ui.tvClockInDate);
            UIHelper.setTextSize(10, ui.textTimelineTitle);
        }
    }

}
