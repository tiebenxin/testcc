package com.lensim.fingerchat.fingerchat.ui.work_center.sign;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.components.adapter.BaseListAdapter;
import com.lensim.fingerchat.fingerchat.R;
import java.util.ArrayList;

/**
 * date on 2018/1/8
 * author ll147996
 * describe
 */

public class ClockInAdapter extends BaseListAdapter<String> {

    private Activity context;
    private LayoutInflater inflater;
    public static final int REQUEST_IMGS = 25;

    private ImageViewClickListener listener;

    public void setListener(ImageViewClickListener listener) {
        this.listener = listener;
    }


    public ClockInAdapter(Activity ctx) {
        super(ctx);
        context = ctx;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size() + 1;
    }

    @Override
    public String getItem(int position) {

        if (position == items.size()) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_statu_img, parent, false);
        ImageView imageView = convertView.findViewById(R.id.statu_img);
        String uri = getItem(position);
        if (!StringUtils.isEmpty(uri)) {
            Glide.with(context).load(uri).centerCrop().into(imageView);
        }
        if (position == items.size()) {
            imageView.setImageResource(R.drawable.punch_the_clock);
            if (position == 3) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setOnClickListener((view) -> {
                    if (listener != null) {
                        listener.onClick();
                    }
                });
            }
        } else {
            imageView.setOnClickListener((view) -> {
                Intent intent = new Intent(context, ImagePagerOptActivity.class);
                intent.putStringArrayListExtra(ImagePagerOptActivity.INTENT_IMGURLS, new ArrayList<>(items));
                intent.putExtra(ImagePagerOptActivity.INTENT_POSITION, position);
                context.startActivityForResult(intent, REQUEST_IMGS);
            });
        }
        return convertView;
    }

    public interface ImageViewClickListener {
        void onClick();
    }

}
