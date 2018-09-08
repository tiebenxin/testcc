package com.lensim.fingerchat.fingerchat.ui.work_center.sign;

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

public class ClockInRecordDetailAdapter extends BaseListAdapter<String> {

    private Activity context;
    private LayoutInflater inflater;
    private static final int REQUEST_IMGS = 25;

    public ClockInRecordDetailAdapter(Activity ctx) {
        super(ctx);
        context = ctx;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return items != null ? items.size() + 1 : 1;
    }


    @Override
    public String getItem(int position) {
        if (position == items.size()) {
            return null;
        }
        return items.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_statu_img, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String uri = getItem(position);
        if (!StringUtils.isEmpty(uri)) {
            Glide.with(context).load(uri).centerCrop().into(holder.imageView);
        }

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImagePagerOptActivity.class);
            intent.putStringArrayListExtra(ImagePagerOptActivity.INTENT_IMGURLS,
                new ArrayList<>(items));
            intent.putExtra(ImagePagerOptActivity.INTENT_POSITION, position);
            intent.putExtra(ImagePagerOptActivity.INTENT_HAS_BUTTON, true);
            context.startActivityForResult(intent, REQUEST_IMGS);
        });
        holder.iv_delete.setVisibility(View.GONE);
        return convertView;
    }

    static class ViewHolder {

        ImageView imageView;
        ImageView iv_delete;

        public ViewHolder(View v) {
            imageView = v.findViewById(R.id.statu_img);
            iv_delete = v.findViewById(R.id.ib_delete);
        }
    }

}
