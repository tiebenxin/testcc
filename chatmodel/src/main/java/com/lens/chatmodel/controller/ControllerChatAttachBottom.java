package com.lens.chatmodel.controller;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.lens.chatmodel.ChatEnum.ETransforModel;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.commons.dialog.CarbonDialog;
import com.lens.chatmodel.interf.IChatBottomAttachListener;
import java.util.List;

/**
 * Created by LL130386 on 2017/12/19.
 */

public class ControllerChatAttachBottom {

    private View rootView;
    private ImageView iv_forward;
    private ImageView iv_collect;
    private ImageView iv_delete;
    private IChatBottomAttachListener listener;
    private List<IChatRoomModel> mSelectedModels;
    private final Context context;
    private CarbonDialog dialog;

    public ControllerChatAttachBottom(Context c, View v) {
        context = c;
        init(v);
    }


    private void init(View v) {
        rootView = v;
        iv_forward = v.findViewById(R.id.iv_forward);
        iv_collect = v.findViewById(R.id.iv_collect);
        iv_delete = v.findViewById(R.id.iv_delete);

        iv_forward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showTransforDialog();
            }

        });
        iv_collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.clickCollect();
                }
            }

        });
        iv_delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.clickDele();
                }
            }

        });


    }

    private void showTransforDialog() {
        if (dialog == null) {
            dialog = new CarbonDialog(context, R.style.MyDialog);
        }
        dialog.setOnItemClickListener(new CarbonDialog.OnItemClickListener() {
            @Override
            public void onMessageByOne() {
                if (listener != null) {
                    listener.clickForword(ETransforModel.MODE_ONE_BY_ONE);
                }
                dialog.dismiss();

            }

            @Override
            public void onMessageByAll() {
                if (listener != null) {
                    listener.clickForword(ETransforModel.MODE_ALL);
                }
                dialog.dismiss();

            }

            @Override
            public void onPackAllMessage() {
                if (listener != null) {
                    listener.clickAttach();
                }
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    public void dimissTansforDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setOnClickListener(IChatBottomAttachListener l) {
        listener = l;
    }

    public void setVisible(boolean b) {
        rootView.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public boolean isVisible() {
        return rootView.getVisibility() == View.VISIBLE;
    }

}
