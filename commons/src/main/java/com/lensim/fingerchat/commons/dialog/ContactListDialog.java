package com.lensim.fingerchat.commons.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IContactDialogListener;

/**
 * Created by LY309313 on 2016/8/12.
 */

public class ContactListDialog extends BaseDialog {


    private TextView mTvDelete;
    private TextView mTvDivide;
    private IContactDialogListener listener;
    IChatUser user;


    public ContactListDialog(Context context, int theme, IChatUser user, IContactDialogListener l) {
        super(context, theme);
        this.listener = l;
        this.user = user;
    }

    @Override
    public void initView() {
        setContentView(R.layout.pop_contactlist_menu);
        mTvDelete = findViewById(R.id.pop_delete_friend);
        mTvDivide = findViewById(R.id.pop_divider_friend);
    }

    @Override
    public void initEvent() {
        mTvDelete.setOnClickListener(this);
        mTvDivide.setOnClickListener(this);
    }

    @Override
    public void processClick(View view) {
        int i = view.getId();
        if (i == R.id.pop_delete_friend) {
            if (listener != null && user != null) {
                listener.onDeleFriend(user);
            }
        } else if (i == R.id.pop_divider_friend) {
            if (listener != null && user != null) {
                listener.onGroupOrTagName(user);
            }
        }
        dismiss();
    }
}
