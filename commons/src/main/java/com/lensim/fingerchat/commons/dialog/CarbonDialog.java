package com.lensim.fingerchat.commons.dialog;

import android.content.Context;
import android.view.View;
import com.lensim.fingerchat.commons.R;


/**
 * Created by LY309313 on 2017/5/31.
 */

public class CarbonDialog extends BaseDialog {


    private View messageByOne;
    private View messageByAll;
    private View packAllMessage;
    private OnItemClickListener onItemClickListener;
    public CarbonDialog(Context context, int theme) {
        super(context, theme);

    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_carbon);
        messageByOne = findViewById(R.id.ll_one_message);
        messageByAll = findViewById(R.id.ll_all_message);
        packAllMessage = findViewById(R.id.ll_pack_message);
    }

    @Override
    public void initEvent() {
        messageByOne.setOnClickListener(this);
        messageByAll.setOnClickListener(this);
        packAllMessage.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int i = view.getId();
        if (i == R.id.ll_one_message) {
            if (onItemClickListener != null) {
                onItemClickListener.onMessageByOne();
            }

        } else if (i == R.id.ll_all_message) {
            if (onItemClickListener != null) {
                onItemClickListener.onMessageByAll();
            }

        } else if (i == R.id.ll_pack_message) {
            if (onItemClickListener != null) {
                onItemClickListener.onPackAllMessage();
            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onMessageByOne();

        void onMessageByAll();

        void onPackAllMessage();
    }
}
