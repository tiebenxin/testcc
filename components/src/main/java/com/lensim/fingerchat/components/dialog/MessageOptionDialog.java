package com.lensim.fingerchat.components.dialog;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.lensim.fingerchat.components.R;
import com.lensim.fingerchat.components.adapter.FilterListAdapter.IFilterListAdapter;


/**
 * Created by LY309313 on 2016/9/3.
 */

public class MessageOptionDialog extends BaseDialog {

    private TextView mMsgOptCancel;
    private TextView mMsgOptCopy;
    private TextView mMsgOptDelete;
    private OnMsgOptListener listener;
    private OptType type;
    private FrameLayout mMsgOptCancelContainer;

    public enum OptType{INCOMING,OUTGOING,TIMEOUT}
    public MessageOptionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public MessageOptionDialog(Context context, int theme,OptType type) {
        super(context, theme);
        this.type = type;
    }

    public MessageOptionDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.dialog_msg_option);
        mMsgOptCancel = (TextView) findViewById(R.id.mMsgOptCancel);
        mMsgOptCancelContainer = ((FrameLayout) findViewById(R.id.mMsgOptCancelContainer));
        mMsgOptCopy = (TextView) findViewById(R.id.mMsgOptCopy);
        mMsgOptDelete = (TextView) findViewById(R.id.mMsgOptDelete);
       // if(type == OptType.INCOMING || type == OptType.TIMEOUT){
            if(mMsgOptCancelContainer.getVisibility() != View.GONE)
                mMsgOptCancelContainer.setVisibility(View.GONE);
       // }

    }

    @Override
    public void initEvent() {
        mMsgOptCancelContainer.setOnClickListener(this);
        mMsgOptCopy.setOnClickListener(this);
        mMsgOptDelete.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        if (view.getId() == R.id.mMsgOptCancelContainer) {
            if(listener != null){
                listener.onCancel();
            }
        } else if (view.getId() == R.id.mMsgOptCopy) {
            if(listener != null){
                listener.onCopy();
            }
        } else if (view.getId() == R.id.mMsgOptDelete) {
            if(listener != null){
                listener.onDelete();
            }
        }
    }

    public interface OnMsgOptListener{
        void onCancel();
        void onCopy();
        void onDelete();

    }

    public OptType getType() {
        return type;
    }

    public void setType(OptType type) {
        this.type = type;

    }

    public OnMsgOptListener getListener() {
        return listener;
    }

    public void setListener(OnMsgOptListener listener) {
        this.listener = listener;
    }
}
