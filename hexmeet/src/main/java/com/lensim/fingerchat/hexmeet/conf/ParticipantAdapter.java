package com.lensim.fingerchat.hexmeet.conf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.RestParticipant;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import java.util.List;

public class ParticipantAdapter extends ArrayAdapter<RestParticipant> {

  private int resource;

  public ParticipantAdapter(Context context, int textViewResourceId, List<RestParticipant> objects) {
    super(context, textViewResourceId, objects);
    resource = textViewResourceId;
  }

  private class ViewHolder {

    public ImageView avatar;
    public TextView name;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    RestParticipant participant = getItem(position);
    ViewHolder holder = null;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = LayoutInflater.from(getContext()).inflate(resource, null);
      holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
      holder.name = (TextView) convertView.findViewById(R.id.name);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    String sip = participant.getSipUserName();
    if (sip.contains("@")) {
      sip = sip.substring(0, sip.indexOf('@'));
    }
    if (participant.getUserId() > 0) {
      String host = RuntimeData.getUcmServer();
      String imageUrl = "https://" + host + "/userFiles/avatar/" + participant.getUserId() + ".jpg?v=0";
      AvatarLoader.load(imageUrl, holder.avatar);
    } else {
      holder.avatar.setImageResource(R.drawable.icon_contact);
    }

    holder.name.setText(participant.getName());

    return convertView;
  }
}
