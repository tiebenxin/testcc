package com.lens.chatmodel.ui.image;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.data.bean.ImageBean;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;
    private OnIndicatorClickListener onIndicatorClickListener;

    private List<ImageBean> mImages = new ArrayList<>();
    private List<ImageBean> mSelectedImages = new ArrayList<>();

    final int mGridWidth;

    public ImageGridAdapter(Context context, boolean showCamera, int column) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            //noinspection deprecation
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / column;
    }

    /**
     * 显示选择指示器
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b) {
        if (showCamera == b) {
            return;
        }

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera() {
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     */
    public void select(ImageBean image) {
        if (mSelectedImages.contains(image)) {
            mSelectedImages.remove(image);
        } else {
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     */
    public void setDefaultSelected(List<ImageBean> resultList) {
        for (ImageBean image : resultList) {
            if (image != null) {
                mSelectedImages.add(image);
            }
        }
        if (mSelectedImages.size() > 0) {
            notifyDataSetChanged();
        }
    }


    /**
     * 设置数据集
     */
    public void setData(List<ImageBean> images) {
        mSelectedImages.clear();

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera) {
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return showCamera ? mImages.size() + 1 : mImages.size();
    }

    @Override
    public ImageBean getItem(int i) {
        if (showCamera) {
            if (i == 0) {
                return null;
            }
            return mImages.get(i - 1);
        } else {
            return mImages.get(i);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if (isShowCamera()) {
            if (i == 0) {
                view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
                return view;
            }
        }

        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
            holder = new ViewHolder(view);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if (holder != null) {
            holder.bindData(getItem(i));
            holder.indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onIndicatorClickListener != null) {
                        onIndicatorClickListener.onClick(i, getItem(i));
                    }
                }
            });
        }

        return view;
    }

    class ViewHolder {

        ImageView image, iv_video;
        ImageView indicator;
        View mask;
        TextView tv_gif;

        ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            iv_video = (ImageView) view.findViewById(R.id.moivemark);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            tv_gif = (TextView) view.findViewById(R.id.tv_gif);
            view.setTag(this);
        }

        void bindData(final ImageBean data) {
            if (data == null) {
                return;
            }
            // 处理单选和多选状态
            if (showSelectIndicator) {
                indicator.setVisibility(View.VISIBLE);
                if (mSelectedImages.contains(data)) {
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                } else {
                    // 未选择
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            } else {
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);
            if (imageFile.exists()) {
                // 显示图片
                if (ContextHelper.isGif(data.path)) {
                    tv_gif.setVisibility(View.VISIBLE);
                    iv_video.setVisibility(View.GONE);
                    Glide.with(mContext)
                        .load(imageFile)
                        .asBitmap()
                        .placeholder(R.drawable.default_error)
                        .override(mGridWidth, mGridWidth)
                        .centerCrop()
                        .into(image);
                } else if (ContextHelper.isVideo(data.path)) {
                    tv_gif.setVisibility(View.GONE);
                    iv_video.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                        .override(mGridWidth, mGridWidth)
                        .centerCrop()
                        .into(image);
                } else {
                    tv_gif.setVisibility(View.GONE);
                    iv_video.setVisibility(View.GONE);
                    Glide.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                        .override(mGridWidth, mGridWidth)
                        .centerCrop()
                        .into(image);
                }
            } else {
                image.setImageResource(R.drawable.default_error);
            }
        }
    }

    public List<ImageBean> getmImages() {
        return mImages;
    }

    public List<ImageBean> getmSelectedImages() {
        return mSelectedImages;
    }

    public OnIndicatorClickListener getOnIndicatorClickListener() {
        return onIndicatorClickListener;
    }

    public void setOnIndicatorClickListener(OnIndicatorClickListener onIndicatorClickListener) {
        this.onIndicatorClickListener = onIndicatorClickListener;
    }

    public interface OnIndicatorClickListener {

        void onClick(int position, ImageBean data);
    }
}
