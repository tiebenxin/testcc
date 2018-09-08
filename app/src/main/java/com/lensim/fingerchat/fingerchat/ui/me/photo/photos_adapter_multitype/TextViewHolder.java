package com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.CharacterStyle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.photo.CommentDetailActivity;
import com.lensim.fingerchat.fingerchat.ui.me.photo.PhotosActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * date on 2017/12/20
 * author ll147996
 * describe
 */

public class TextViewHolder extends ItemViewBinder<NewComment, TextViewHolder.ViewHolder> {

    private Context context;
    private boolean isToday;
    private int mHeaderCount = 1;//头部View个数，先固定为一个
    private boolean isMyPhoto;//是不是我本人的相册
    private List<FriendCircleEntity> entities;

    public TextViewHolder(Context ctx, boolean isMyPhoto) {
        context = ctx;
        this.isMyPhoto = isMyPhoto;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_photo_text, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull NewComment item) {

        int position = holder.getAdapterPosition();
        int realPosition = isMyPhoto ? position - mHeaderCount : position;

        holder.tvContent.setText(CyptoConvertUtils.decryptString(item.getPHO_Content()));
        holder.fl_content.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentDetailActivity.class);
            intent.putExtra("newComment", item);
            intent.putExtra("photoSerno", item.getPHO_Serno());
            ((PhotosActivity) context)
                .startActivityForResult(intent, PhotosActivity.PHOTOS_REQUEST_NEW_STATUS);
        });
        SpannableStringBuilder sb = getMonthAndDay(item.getPHO_CreateDT());
        holder.tvDate.setText(sb, TextView.BufferType.SPANNABLE);
        holder.tvDate.setTag(realPosition);
        holder.tvDate.setVisibility(isToday ? View.INVISIBLE : View.VISIBLE);
        if (!isMyPhoto && position == 0) {
            holder.tvDate.setVisibility(View.VISIBLE);
        }
    }

    private SpannableStringBuilder getMonthAndDay(String rq) {
        int index = rq.indexOf("T");
        String rqStr = rq.substring(0, index);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currDate = format.format(new Date(System.currentTimeMillis()));
        SpannableStringBuilder builder = new SpannableStringBuilder();
        String day = "";
        String month = "";
        if (rqStr.equals(currDate)) {
            builder.append("今天");
            isToday = true;
        } else {
            isToday = false;
            String[] split = rqStr.split("-");
            day = split[2];
            if (split[1].charAt(0) == '0') {
                month = split[1].substring(1, 2) + "月";
            } else {
                month = split[1] + "月";
            }
            builder.append(day);
            builder.append(month);
            String newRq = day + month;
            CharacterStyle span = new AbsoluteSizeSpan(20, true);
            int start = newRq.indexOf(month);
            builder.setSpan(span, start, start + month.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return builder;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvContent;
        TextView tvDate;
        FrameLayout fl_content;

        public ViewHolder(View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.photo_date);
            fl_content = itemView.findViewById(R.id.fl_content);

        }
    }
}
