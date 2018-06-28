package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.dialog.BaseDialog;
import com.lensim.fingerchat.commons.utils.L;


/**
 * Created by LY309313 on 2017/1/10.
 */

public class NewInviteDialog extends BaseDialog {

    private TextView mDeleteRecoder;
    private String userjid;
    private Context context;
    private View newinvite_dialog_divider_top, newinvite_dialog_divider;
    private removeInviteSuccessListener mItemClickListener;
    private int position;

    public NewInviteDialog(Context context, int theme, String userjid, int posiInList) {
        super(context, theme);
        this.userjid = userjid;
        this.position = posiInList;
    }

    public NewInviteDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.pop_newmsg_menu);
        mDeleteRecoder = (TextView) findViewById(R.id.pop_delete_recoder);
        newinvite_dialog_divider_top = (View) findViewById(R.id.newinvite_dialog_divider_top);
        newinvite_dialog_divider = (View) findViewById(R.id.newinvite_dialog_divider);
        TextView mMarkUnread = (TextView) findViewById(R.id.pop_mark_unread);
        mMarkUnread.setVisibility(View.GONE);
        TextView mRecordToTop = (TextView) findViewById(R.id.pop_record_to_top);
        mRecordToTop.setVisibility(View.GONE);
        newinvite_dialog_divider_top.setVisibility(View.GONE);
        newinvite_dialog_divider.setVisibility(View.GONE);

    }

    @Override
    public void initEvent() {
        mDeleteRecoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.d("被删除的用户id:" + userjid);
//                try {
//                    //先判断是不是已经是好友关系，是好友就只删除记录
//                    if (!(RosterManager.getInstance().isFriend(userjid))) {
//                        PresenceManager.getInstance().discardSubscription(
//                            AccountManager.getInstance().getAccountItem().getAccount(), userjid);
//                    }
//                } catch (NetWorkException e) {
//                    e.printStackTrace();
//                }
//                RosterManager.getInstance().deleteInviter(userjid);
                mItemClickListener.onDelete(position);
                dismiss();

            }
        });
    }

    @Override
    public void processClick(View view) {

    }


    public void seOnDeleteListener(removeInviteSuccessListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * 删除成功
     */
    public interface removeInviteSuccessListener {

        void onDelete(int posi);
    }
}
