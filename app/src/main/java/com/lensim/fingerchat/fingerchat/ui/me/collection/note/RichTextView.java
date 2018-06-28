package com.lensim.fingerchat.fingerchat.ui.me.collection.note;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.utils.NoteStringUtils;
import com.lensim.fingerchat.fingerchat.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.List;

/**
 * Created by sendtion on 2016/6/24.
 * 显示富文本
 */
public class RichTextView extends LinearLayout {

    private static final int EDIT_PADDING = 10; // edittext常规padding是10dp

    private int viewTagIndex = 1; // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private int editNormalPadding = 0; //

    public RichTextView(Context context) {
        this(context, null);
    }

    public RichTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflater = LayoutInflater.from(context);

        // 1. 初始化allLayout
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        //allLayout.setBackgroundColor(Color.WHITE);//去掉背景
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 300);
        allLayout.setPadding(0, 0, 0, 0);//设置间距，防止生成图片时文字太靠边
        addView(allLayout, layoutParams);
        //
        LayoutParams firstEditParam = new LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT);
        //editNormalPadding = dip2px(EDIT_PADDING);
        TextView firstText = createTextView(dip2px(context, EDIT_PADDING));
        firstText.setText("------------------------------------------------");
        allLayout.addView(firstText, firstEditParam);
    }

    public int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 清除所有的view
     */
    public void clearAllLayout() {
        allLayout.removeAllViews();
    }

    /**
     * 获得最后一个子view的位置
     */
    public int getLastIndex() {
        int lastEditIndex = allLayout.getChildCount();
        return lastEditIndex;
    }

    /**
     * 生成文本输入框
     */
    public TextView createTextView(int paddingTop) {
        TextView textView = (TextView) inflater.inflate(R.layout.note_item_textview, null);
        textView.setTag(viewTagIndex++);
        textView.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
        return textView;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater
            .inflate(R.layout.note_item_imageview, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setVisibility(GONE);
        return layout;
    }

    /**
     * 异步方式显示数据
     */
    public void showDataSync(final String html) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                showEditData(e, html);
            }
        }).subscribeOn(Schedulers.io())//生产事件在io
//    }).onBackpressureBuffer().subscribeOn(Schedulers.io())//生产事件在io
            .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onComplete() {

                }


                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                    //ToastUtil.show("解析错误：图片不存在或已损坏");
                }

                @Override
                public void onNext(String text) {
                    if (text.endsWith(NoteStringUtils.IDENTIFER_OF_IMG)) {
                        text = text.replace(NoteStringUtils.IDENTIFER_OF_IMG, "");
                        if (text.indexOf(NoteStringUtils.SPLIT_AND) != -1) {
                            addImageViewAtIndex(getLastIndex(), text, NoteImageView.TYPE_MAP);
                        } else {
                            addImageViewAtIndex(getLastIndex(), text, NoteImageView.TYPE_IMG);
                        }
                    } else {
                        addTextViewAtIndex(getLastIndex(), text);
                    }
                }
            });
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index 位置
     * @param editStr EditText显示的文字
     */
    public void addTextViewAtIndex(final int index, CharSequence editStr) {
        TextView textView = createTextView(EDIT_PADDING);
        textView.setText(editStr);
        allLayout.addView(textView, index);
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index, String url, int type) {
        RelativeLayout imageLayout = createImageLayout();
        NoteImageView imageView = (NoteImageView) imageLayout.findViewById(R.id.edit_imageView);
        if (type == NoteImageView.TYPE_IMG) {
            Glide.with(getContext()).load(url).centerCrop().into(imageView);
        } else if (type == NoteImageView.TYPE_MAP) {
            Glide.with(getContext()).load(NoteStringUtils.divideImagePath(url, false)).centerCrop()
                .into(imageView);
        }
        imageView.setAbsolutePath(url);//保留这句，后面保存数据会用

        allLayout.addView(imageLayout, index);
    }

    /**
     * 显示数据
     */
    protected void showEditData(ObservableEmitter<String> subscriber, String html) {
        try {
            List<String> textList = NoteStringUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                if (text.contains("<img")) {
                    String imagePath = NoteStringUtils.getImgSrc(text);
                    //if (new File(imagePath).exists()) {
                    subscriber.onNext(imagePath);
                    //} else {
                    //showToast("图片" + i + "已丢失，请重新插入！");
                    //}
                } else {
                    subscriber.onNext(text);
                }
            }
            subscriber.onComplete();
        } catch (Exception e) {
            e.printStackTrace();
            subscriber.onError(e);
        }
    }

    /**
     * 根据view的宽度，动态缩放bitmap尺寸
     *
     * @param width view的宽度
     */
    //public Bitmap getScaledBitmap(String filePath, int width) {
    //  BitmapFactory.Options options = new BitmapFactory.Options();
    //  options.inJustDecodeBounds = true;
    //  BitmapFactory.decodeFile(filePath, options);
    //  int sampleSize = options.outWidth > width ? options.outWidth / width + 1 : 1;
    //  options.inJustDecodeBounds = false;
    //  options.inSampleSize = sampleSize;
    //  return BitmapFactory.decodeFile(filePath, options);
    //}
}
