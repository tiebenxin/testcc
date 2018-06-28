package com.lensim.fingerchat.commons.toolbar;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;


/**
 * Created by ll147996 on 2017/12/12.
 * 自定义标题栏
 */

public class FGToolbar extends Toolbar {

    protected Context mContext;

    /**
     *
     */
    protected ImageButton mBtImage;
    /**
     * 搜索c
     */
    protected ImageButton mBtSearch;
    /**
     * 消息
     */
    protected ImageButton mBtMessage;
    /**
     * 默认为“确认” 也可以设置为其他“title”
     */
    protected Button mBtConfirm;
    /**
     * 标题
     */
    protected TextView mTvTitle;
    /**
     * 可以在“ll_title_right” 添加其他的布局
     */
    protected LinearLayout ll_title_right;

    protected ImageView iv_add;
    private OnClickListener confirmListener;

    public FGToolbar(Context context) {
        this(context, null);
    }

    public FGToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FGToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.fg_toolbar, this, true);
        ll_title_right = view.findViewById(R.id.ll_right);
        iv_add = view.findViewById(R.id.iv_add);
        mTvTitle = view.findViewById(R.id.toolbar_title_tv);
        mBtConfirm = view.findViewById(R.id.bt_invite_comfirm);
        mBtImage = view.findViewById(R.id.toolbar_imageButtom);
        mBtSearch = view.findViewById(R.id.toolbar_search);
        mBtMessage = view.findViewById(R.id.toolbar_message);

        //默认为隐藏
        iv_add.setVisibility(GONE);
        mTvTitle.setVisibility(GONE);
        mBtConfirm.setVisibility(GONE);
//        mBtImage.setVisibility(GONE);
        mBtMessage.setVisibility(GONE);
        mBtSearch.setVisibility(GONE);
    }

    public FGToolbar setTitleText(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTvTitle.setVisibility(VISIBLE);
            mTvTitle.setText(title);
        }
        return this;
    }

    public FGToolbar setTitleText(int textId) {
        mTvTitle.setVisibility(VISIBLE);
        mTvTitle.setText(textId);

        return this;
    }


    public FGToolbar setBtSearchDrawable(int resouce) {
        setBtSearchDrawable(resouce, null);
        return this;
    }

    public FGToolbar setBtSearchDrawable(int resouce, OnClickListener listener) {

        mBtSearch.setVisibility(View.VISIBLE);
        if (resouce != 0) {
            mBtSearch.setImageResource(resouce);
        }
        if (listener != null) {
            mBtSearch.setOnClickListener(listener);
        }

        return this;
    }

    public FGToolbar setBtMessageDrawable(int resouce) {
        setBtMessageDrawable(resouce, null);
        return this;
    }

    public FGToolbar setBtMessageDrawable(int resouce, OnClickListener listener) {

        mBtMessage.setVisibility(View.VISIBLE);
        mBtMessage.setImageResource(resouce);
        if (listener != null) {
            mBtMessage.setOnClickListener(listener);
        }

        return this;
    }


    public FGToolbar setBtImageDrawable(int resouce) {
        setBtImageDrawable(resouce, null);
        return this;
    }

    public FGToolbar setBtImageDrawable(int resouce, OnClickListener listener) {

        mBtImage.setVisibility(View.VISIBLE);
        if (resouce != 0) {
            mBtImage.setImageResource(resouce);
        }

        if (listener != null) {
            mBtImage.setOnClickListener(listener);
        }

        return this;
    }

    /*
    重置 密聊icon选中状态
    * */
    public void resetSecretSelected() {
        mBtMessage.setSelected(!getSecretSelected());
    }

    public boolean getSecretSelected() {
        return mBtMessage.isSelected();
    }


    public FGToolbar setConfirmBt(OnClickListener listener) {
        setConfirmBt(mContext.getResources().getString(R.string.btn_confrim), listener);
        return this;
    }

    public FGToolbar setConfirmBt(String string) {
        setConfirmBt(string, null);
        return this;
    }

    //重置确定键
    public FGToolbar resetConfirmBt(String value, boolean isCanClick) {
        if (isCanClick && confirmListener != null) {
            setConfirmBt(value, confirmListener);
        } else {
            setConfirmBt(value, null);
        }
        return this;
    }


    public FGToolbar setConfirmBt(String string, OnClickListener listener) {
        if (TextUtils.isEmpty(string)) {
            return this;
        }
        mBtConfirm.setVisibility(View.VISIBLE);
        mBtConfirm.setText(string);
        confirmListener = listener;
        if (confirmListener != null) {
            mBtConfirm.setOnClickListener(confirmListener);
        }

        return this;
    }


    public FGToolbar initRightView(View v) {
        if (v == null) {
            ll_title_right.removeAllViews();
            return this;
        }
        ll_title_right.removeAllViews();
        ll_title_right.addView(v);
        ll_title_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightFirstListener != null) {
                    mRightFirstListener.onClick();
                }
            }
        });
        v.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mRightFirstListener != null) {
                    mRightFirstListener.onClick();
                }
            }
        });

        return this;
    }


    private OnFGToolbarClickListenter mRightFirstListener;


    public void setConfirmListener(OnFGToolbarClickListenter l) {
        mRightFirstListener = l;
    }

    public interface OnFGToolbarClickListenter {

        void onClick();

    }
}
