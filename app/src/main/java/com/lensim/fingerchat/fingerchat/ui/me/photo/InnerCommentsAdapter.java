package com.lensim.fingerchat.fingerchat.ui.me.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.adapter.BaseRecyclerAdapter;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * date on 2018/3/30
 * author ll147996
 * describe
 */

public class InnerCommentsAdapter extends BaseRecyclerAdapter<InnerCommentsAdapter.CommentsViewHolder, NewComment> {

    private LayoutInflater mInflater;
    private WeakReference<Context> contextWeakReference;
    private final DisplayImageOptions options = new DisplayImageOptions
        .Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .showImageOnFail(R.drawable.default_avatar)
        .showImageOnLoading(R.drawable.default_avatar)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .build();

    public InnerCommentsAdapter(Context ctx) {
        super(ctx);
        this.mContext = ctx;
        this.contextWeakReference = new WeakReference<>(ctx);
        mInflater = LayoutInflater.from(mContext);
        items = new ArrayList<>();
    }

    public void empty() {
        items.clear();
        notifyDataSetChanged();
    }

    public void addHeader(List<NewComment> data) {
        items.addAll(0, data);
        notifyItemRangeChanged(0, data.size());
    }

    public void addFooter(List<NewComment> data) {
        items.addAll(items.size(), data);
        notifyItemRangeChanged(items.size(), data.size());
    }


    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_lookup_item, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        NewComment item = items.get(position);
        ImageLoader.getInstance().displayImage(String.format(Route.obtainAvater, item.getPHC_CommentUserid()), holder.mLookupAvatar, options);

//        String sign = LensImUtil.getUserSign(item.getPHC_CommentUserid());
//        Glide.with(contextWeakReference.get())
//                .load(String.format(Route.obtainAvater, item.getPHC_CommentUserid()))
//                .signature(new StringSignature(sign))
//                .bitmapTransform(new GlideRoundTransform(contextWeakReference.get()))
//                .into(holder.mLookupAvatar);
        holder.mLookupName.setText(item.getPHC_CommentUsername());
        holder.mLookupName.setTag(position);

        if (item.getPHC_Zambia().equals("0")) {
            holder.mLookupContent.setVisibility(View.VISIBLE);
            holder.isValid.setVisibility(View.GONE);
            int textSize = SPSaveHelper.getIntValue("font_size", 1) * 4 + 12;
            Spannable span = SpannableUtil.getAtText(SmileUtils
                .getSmiledText(mContext, CyptoConvertUtils.decryptString(item.getPHC_Content()), (int) TDevice
                    .dpToPixel(textSize + 10)));
            holder.mLookupContent.setText(span);
        } else {
            holder.isValid.setVisibility(View.VISIBLE);
            holder.mLookupContent.setVisibility(View.GONE);
        }

        String create_DT = item.getPHC_CreateDT();
        if (create_DT != null) {
            if(create_DT.contains(".")){
                create_DT = create_DT.substring(0,create_DT.lastIndexOf("."));
            }
            holder.mLookupTimeStamp.setText(create_DT.replace("T", " "));
        }

        String pho_imageName = item.getPHO_ImageName();
        if (pho_imageName != null) {
            if (pho_imageName.endsWith(".mp4")) {
                //视频，放缩略图
                //String path = AppConfig.CIRCLE_PATH + pho_imageName;
                //if (FileUtil.checkFilePathExists(path)) Glide.with(contextWeakReference.get()).load(path).centerCrop().into(holder.mLookupPicture);
            } else {
                String[] names = pho_imageName.split(";");
                if (names.length <= 0) {
                    holder.mLookupPicture.setVisibility(View.GONE);
                } else {
                    holder.mLookupPicture.setVisibility(View.VISIBLE);
                    String url = item.getPHO_ImagePath().replace("C:\\HnlensWeb\\", Route.Host) + names[0];
                    Glide.with(contextWeakReference.get()).load(url).into(holder.mLookupPicture);
                }
            }
        }else {
            holder.mLookupPicture.setVisibility(View.GONE);
        }
    }


    static class CommentsViewHolder extends BaseRecyclerAdapter.VH {

        private ImageView mLookupAvatar;
        private TextView mLookupName;
        private ImageView isValid;
        private TextView mLookupContent;
        private ImageView mLookupPicture;
        private TextView mLookupTimeStamp;

        public CommentsViewHolder(View view) {
            super(view);
            mLookupAvatar = view.findViewById(R.id.mLookupAvatar);
            mLookupName = view.findViewById(R.id.mLookupName);
            isValid = view.findViewById(R.id.isValid);
            mLookupContent = view.findViewById(R.id.mLookupContent);
            mLookupPicture = view.findViewById(R.id.mLookupPicture);
            mLookupTimeStamp = view.findViewById(R.id.mLookupTimeStamp);
        }
    }
}
