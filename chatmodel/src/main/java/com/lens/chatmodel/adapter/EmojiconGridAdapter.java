package com.lens.chatmodel.adapter;


import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.Emojicon.Type;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.utils.SmileUtils;
import java.util.List;


public class EmojiconGridAdapter extends BaseAdapter {

    private Type emojiconType;
    private List<Emojicon> objects;
    private int textViewResourceId;
    private Context context;

    public EmojiconGridAdapter(Context context, int textViewResourceId, List<Emojicon> objects,
        Emojicon.Type emojiconType) {
        this.objects = objects;
        this.textViewResourceId = textViewResourceId;
        this.emojiconType = emojiconType;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (emojiconType == Type.BIG_EXPRESSION) {
                convertView = View.inflate(context, R.layout.lens_row_big_expression, null);
            } else {
                convertView = View.inflate(context, R.layout.lens_row_expression, null);
            }
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expression);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
        //获取表情
        Emojicon emojicon = getItem(position);
        //设置表情名字
        if (textView != null && emojicon.getName() != null) {
            textView.setText(emojicon.getName());
        }
        //如果表情文字与删除一致，那么设置成删除键
        if (SmileUtils.DELETE_KEY.equals(emojicon.getEmojiText())) {
            imageView.setImageResource(R.drawable.ease_delete_expression);
        } else {
            if (emojicon.getIcon() != 0) {
                imageView.setImageResource(emojicon.getIcon());
            } else if (emojicon.getIconPath() != null) {
                ImageUploadEntity entity = ImageUploadEntity.fromJson(emojicon.getIconPath());
                if (entity != null) {
                    Glide.with(context).load(entity.getOriginalUrl())
                        .placeholder(R.drawable.ease_default_expression).into(imageView);
                }
            }
        }
        return convertView;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return objects.size();
    }

    @Override
    public Emojicon getItem(int position) {
        // TODO Auto-generated method stub
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private void previewImage(Emojicon emojicon) {

    }

}
