package com.lensim.fingerchat.hexmeet.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CallRecordAdapter extends ArrayAdapter<CallRecordGroup> {

  private Context context;
  private int resource;
  private List<CallRecordGroup> callRecordGroups;
  private SimpleDateFormat sdf;

  public CallRecordAdapter(Context context, int resource, List<CallRecordGroup> callRecords) {
    super(context, resource, callRecords);

    this.context = context;
    this.resource = resource;
    this.callRecordGroups = callRecords;
    sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.SIMPLIFIED_CHINESE);
    sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
  }

  private class ViewHolder {

    public ImageView call_in_out_flag;
    public ImageView peer_avatar;
    public TextView peer_name;
    public TextView extra_info;
    public TextView date;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = LayoutInflater.from(getContext()).inflate(resource, null);

      holder.call_in_out_flag = (ImageView) convertView.findViewById(R.id.call_in_out_flag);
      holder.peer_avatar = (ImageView) convertView.findViewById(R.id.peer_avatar);
      holder.peer_name = (TextView) convertView.findViewById(R.id.peer_name);
      holder.extra_info = (TextView) convertView.findViewById(R.id.extra_info);
      holder.date = (TextView) convertView.findViewById(R.id.date);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    CallRecordGroup group = callRecordGroups.get(position);
    holder.peer_name.setTextColor(Color.parseColor("#313131"));

    RestContact peer = null;
    if (group.getLatestCall().getIsOutgoing()) {
      peer = App.getContact(group.getLatestCall().getPeerSipNum());
      holder.call_in_out_flag.setImageResource(R.drawable.icon_dialed);
      holder.extra_info.setVisibility(View.GONE);
    } else {
      peer = App.getContact(group.getLatestCall().getPeerSipNum());
      if (group.getMissedCallCount() == 0) {
        holder.call_in_out_flag.setImageResource(R.drawable.icon_received);
        holder.extra_info.setVisibility(View.GONE);
      } else {
        holder.call_in_out_flag.setImageResource(R.drawable.icon_missed);
        holder.peer_name.setTextColor(Color.parseColor("#f04848"));
        if (App.isEnVersion()) {
          int count = group.getMissedCallCount();
          holder.extra_info.setText(context.getResources().getString(R.string.you_have) + " " + count + " " + context.getResources().getString(R.string.missed_call) + (count > 1 ? "s" : ""));
        } else {
          holder.extra_info.setText(context.getResources().getString(R.string.you_have) + group.getMissedCallCount() + context.getResources().getString(R.string.missed_call));
        }
        holder.extra_info.setVisibility(View.VISIBLE);
      }
    }

    if (peer != null) {
      String host = "https://" + RuntimeData.getUcmServer();
      AvatarLoader.load(host + peer.getImageURL(), holder.peer_avatar);
      holder.peer_name.setText(peer.getName());
    } else {
      holder.peer_avatar.setImageResource(R.drawable.icon_contact);
      holder.peer_name.setText(group.getLatestCall().getPeerSipNum());
    }

    holder.date.setText(sdf.format(group.getLatestCall().getStartTime()));

    return convertView;
  }
}

