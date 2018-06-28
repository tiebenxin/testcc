package com.lensim.fingerchat.commons.dialog;


import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.interf.IDialogItemClickListener;
import com.lensim.fingerchat.commons.utils.L;


/**
 * Created by LY309313 on 2016/9/21.
 */

public class NewMsgDialog extends BaseDialog {
    private TextView mDeleteRecoder;
    private String userjid;
    private Context context;
    private boolean isGroup;
    private TextView mMarkUnread;
    private TextView mRecordToTop;
    private boolean isUnread;
    private boolean isTop;
    private IDialogItemClickListener mListener;

    public NewMsgDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public NewMsgDialog(Context context, int theme, String userjid, boolean isGroup,
        boolean isUnread,
        boolean isTop) {
        super(context, theme);
        this.userjid = userjid;
        this.context = context;
        this.isGroup = isGroup;
        this.isUnread = isUnread;
        this.isTop = isTop;
    }


    public NewMsgDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.pop_newmsg_menu);
        mDeleteRecoder = (TextView) findViewById(R.id.pop_delete_recoder);
        mMarkUnread = (TextView) findViewById(R.id.pop_mark_unread);
        mRecordToTop = (TextView) findViewById(R.id.pop_record_to_top);
        if (isUnread) {
            mMarkUnread.setText("标为已读");
        } else {
            mMarkUnread.setText("标为未读");
        }

        if (isTop) {
            mRecordToTop.setText("取消置顶");

        } else {
            mRecordToTop.setText("置顶聊天");
        }
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    @Override
    public void initEvent() {
        mMarkUnread.setOnClickListener(this);
        mRecordToTop.setOnClickListener(this);
        mDeleteRecoder.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int i = view.getId();
        if (i == R.id.pop_delete_recoder) {
            L.d("被删除的用户id:" + userjid);
            if (mListener != null) {
                mListener.dele();
            }
        } else if (i == R.id.pop_mark_unread) {
            if (mListener != null) {
                mListener.markUnread();
            }
        } else if (i == R.id.pop_record_to_top) {
            if (mListener != null) {
                mListener.markTop();
            }
        }

        dismiss();
    }


    public void setItemClickListener(IDialogItemClickListener l) {
        mListener = l;
    }


}
