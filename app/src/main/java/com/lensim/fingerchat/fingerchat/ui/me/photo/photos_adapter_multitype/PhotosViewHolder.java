package com.lensim.fingerchat.fingerchat.ui.me.photo.photos_adapter_multitype;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lens.chatmodel.helper.FileCache;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.FileUtil;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.adapter.multitype.ItemViewBinder;
import com.lensim.fingerchat.components.widget.CircleProgress;
import com.lensim.fingerchat.data.me.circle_friend.FxPhotosBean;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.DownloadApi;
import com.lensim.fingerchat.fingerchat.component.download.DownloadProgressListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;

/**
 * date on 2017/12/20
 * author ll147996
 * describe
 */

public class PhotosViewHolder extends ItemViewBinder<FxPhotosBean, PhotosViewHolder.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private LinearLayout.LayoutParams P1;
    private LinearLayout.LayoutParams P2;
    private LinearLayout.LayoutParams P3_1;
    private LinearLayout.LayoutParams P3_2;
    private LinearLayout.LayoutParams P4;
    private LinearLayout.LayoutParams LP4;
    private final DisplayImageOptions options;
    private Context context;
    private boolean isToday;
    private int mHeaderCount = 1;//头部View个数，先固定为一个
    private boolean isMyPhoto;//是不是我本人的相册
    private List<FxPhotosBean> entities;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onclick(FxPhotosBean entity, int position);
    }

    public PhotosViewHolder(Context ctx, boolean isMyPhoto) {
        context = ctx;
        this.isMyPhoto = isMyPhoto;
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ease_default_image)
            .showImageOnFail(R.drawable.ease_default_image)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_photo_photos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull FxPhotosBean item) {
        holder.itemView.setOnClickListener(v -> {
            int position = (int) holder.tvDate.getTag();
            if (onItemClickListener != null) {
                onItemClickListener.onclick(item, position);
            }
        });

        int position = holder.getAdapterPosition();
        int realPosition = isMyPhoto ? position - mHeaderCount : position;
//        final FxPhotosBean entity = entities.get(realPosition);
        final FxPhotosBean entity = item;

        if (position == 0) {
            holder.head.setVisibility(View.VISIBLE);
        } else {
            holder.head.setVisibility(View.GONE);
        }

        SpannableStringBuilder sb = getMonthAndDay(TimeUtils.timeFormat(entity.getCreateDatetime()));
        holder.tvDate.setText(sb, TextView.BufferType.SPANNABLE);
        holder.tvDate.setTag(realPosition);
        holder.tvDate.setVisibility(isToday ? View.INVISIBLE : View.VISIBLE);
        if (!isMyPhoto && position == 0) {
            holder.tvDate.setVisibility(View.VISIBLE);
        }

        //显示状态内容
        holder.tvMsg.setText(CyptoConvertUtils.decryptString(entity.getPhotoContent()));

        //显示图片数量
        holder.tvCount.setText("共" + entity.getPhotoFileNum() + "张");
        holder.videoImage.setVisibility(View.GONE);
        holder.cirleProgress.setVisibility(View.GONE);
        //显示图片，一张占满，两张横向排列，三张第二列分开，四张分开
        String nameStr = entity.getPhotoFilenames();
        String pathStr = entity.getPhotoUrl();
        if (!StringUtils.isEmpty(nameStr)) {
            holder.imgContainer.removeAllViews();
            showImage(holder,nameStr,pathStr);
        }
    }

    private void showImage(ViewHolder holder, String nameStr, String pathStr) {
        String[] names = nameStr.split(";");
        String[] paths = pathStr.split(",");
        int count = 0;
        if (names[0].contains(".mp4")){
            count = names.length;
        }else {
            count = paths.length;
        }
        if (count > 4) {
            count = 4;
        }
        switch (count) {
            case 1:
                if (P1 == null) {
                    P1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                }
                final ImageView iv1 = new ImageView(context);
                iv1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                iv1.setAdjustViewBounds(true);

                if (names[0].endsWith(".mp4")) {
                    holder.videoImage.setVisibility(View.VISIBLE);
                    String path = FileCache.getInstance().getVideoPath(paths[0]);
                    if (FileUtil.checkFilePathExists(path)) {
                        //Glide.with(context).load(url.replace(".mp4", ".jpg")).centerCrop().into(iv1);
                        Glide.with(context).load(paths[1]).centerCrop().into(iv1);
                    } else {
                        holder.cirleProgress.setVisibility(View.VISIBLE);

                        ProgressManager
                            .getInstance().addResponseListener(paths[0], new ProgressListener() {
                            @Override
                            public void onError(long id, Exception e) {

                            }

                            @Override
                            public void onProgress(ProgressInfo progressInfo) {
                                holder.cirleProgress.setPercent(progressInfo.getPercent());
                            }
                        });
                        downloadFileWithDynamicUrlAsync(paths[0], holder, iv1);
                    }
                } else {
                    String url = (pathStr.replace("C:\\HnlensWeb\\", Route.Host)/* + names[0]*/)
                        .replace("\\", "/");
                    if (ContextHelper.isGif(url)) {
                        Glide.with(context).load(url)
                            .placeholder(R.drawable.ease_default_image)
                            .centerCrop()
                            .diskCacheStrategy(
                                DiskCacheStrategy.SOURCE).thumbnail(0.1f).into(iv1);
                    } else {
                        Glide.with(context).load(url).asBitmap().placeholder(R.drawable.ease_default_image)
                            .centerCrop()
                            .diskCacheStrategy(
                                DiskCacheStrategy.SOURCE).into(iv1);
                    }
                }
                holder.imgContainer.addView(iv1, P1);
                break;
            case 2:
                if (P2 == null) {
                    P2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    int margin = ((int) TDevice.dpToPixel(2));
                    P2.setMargins(0, margin, margin, margin);
                }
                for (int i = 0; i < 2; i++) {
                    ImageView iv2 = new ImageView(context);
                    addImageView(iv2, paths, i, pathStr);
                    holder.imgContainer.addView(iv2, P2);
                }
                break;
            case 3:
                if (P3_1 == null) {
                    P3_1 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                    int margin = ((int) TDevice.dpToPixel(2));
                    P3_1.setMargins(0, margin, margin, margin);
                }
                if (P3_2 == null) {
                    P3_2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
                    int margin = ((int) TDevice.dpToPixel(2));
                    P3_2.setMargins(margin, margin, margin, margin);
                }
                LinearLayout rightContainer = new LinearLayout(context);
                rightContainer.setOrientation(LinearLayout.VERTICAL);
                for (int i = 0; i < 2; i++) {
                    ImageView iv3 = new ImageView(context);
                    addImageView(iv3, paths, i, pathStr);
                    if (i == 0) {
                        holder.imgContainer.addView(iv3, P3_1);
                        holder.imgContainer.addView(rightContainer, P3_1);
                    } else {
                        rightContainer.addView(iv3, P3_2);
                    }
                }
                break;
            case 4:
                if (P4 == null) {
                    P4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1);
                    int margin = ((int) TDevice.dpToPixel(2));
                    P4.setMargins(0, margin, margin, margin);
                }
                if (LP4 == null) {
                    LP4 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
                }

                LinearLayout LContainer = new LinearLayout(context);
                LContainer.setOrientation(LinearLayout.VERTICAL);
                holder.imgContainer.addView(LContainer, LP4);
                LinearLayout RContainer = new LinearLayout(context);
                RContainer.setOrientation(LinearLayout.VERTICAL);
                holder.imgContainer.addView(RContainer, LP4);
                for (int i = 0; i < count; i++) {
                    ImageView iv4 = new ImageView(context);
                    addImageView(iv4, paths, i, pathStr);
                    if (i < 2) {
                        LContainer.addView(iv4, P4);
                    } else {
                        RContainer.addView(iv4, P4);
                    }
                }
                break;
        }
    }

    private void addImageView(ImageView iv, String[] names, int i, String pathStr) {
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String url;
        /*url = pathStr.replace("C:\\HnlensWeb\\", Route.Host) + names[i];
        url = url.replace("\\", "/");*/
        url = names[i];
        L.d(TAG, url);
        Glide.with(context)
            .load(url)
            .placeholder(R.drawable.ease_default_image)
            .centerCrop()
            .into(iv);
    }

    private void downloadFileWithDynamicUrlAsync(String url, ViewHolder holder, ImageView iv1) {
        DownloadProgressListener downloadProgressListener = (bytesRead, contentLength, done) -> {
            // TODO更新进度条
        };
        new DownloadApi(downloadProgressListener)
            .downloadVideo(url, bytes -> {
                holder.cirleProgress.setVisibility(View.GONE);
                Glide.with(ContextHelper.getContext())
                    .load(url.replace(".mp4", ".jpg"))
                    .centerCrop()
                    .into(iv1);
            });
    }


    private SpannableStringBuilder getMonthAndDay(String rq) {
        int index = rq.indexOf("T");
        String rqStr = rq.substring(0, index);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currDate = format.format(new Date(System.currentTimeMillis()));
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (rqStr.equals(currDate)) {
            builder.append("今天");
            isToday = true;
        } else {
            String month;
            isToday = false;
            String[] split = rqStr.split("-");
            String day = split[2];
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

        TextView tvDate;
        TextView tvMsg;
        TextView tvCount;
        LinearLayout imgContainer;
        FrameLayout head;
        ImageView videoImage;
        CircleProgress cirleProgress;

        public ViewHolder(View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.photo_date);
            tvMsg = itemView.findViewById(R.id.photo_msg);
            tvCount = itemView.findViewById(R.id.photo_count);
            imgContainer = itemView.findViewById(R.id.photo_container);
            head = itemView.findViewById(R.id.photo_head);
            videoImage = itemView.findViewById(R.id.videoImage);
            cirleProgress = itemView.findViewById(R.id.progress_bar);

            UIHelper.setTextSize2(14, tvMsg, tvCount);
        }
    }
}
