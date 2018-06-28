package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lens.chatmodel.interf.IContactItemClickListener;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.adapter.AbstractViewHolder;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LL130386 on 2017/11/30.
 * 联系人item
 */

public class ControllerContactItem extends AbstractViewHolder<IChatUser> {


    private ImageView iv_avatar;
    private TextView tv_name;
    private LinearLayout ll_identify;
    private TextView tv_company;
    private TextView tv_department;
    private IContactItemClickListener listenter;
    private IChatUser model;
    private TextView tv_head;
    private String currentIndex;//首字母
    private int currentPosition;
    private int numNormal;
    private ImageView iv_identify;
    private TextView tv_count;

    ControllerContactItem(View v, IContactItemClickListener l) {
        super(v);
        init(v);
        updateWidth(v);
        listenter = l;
    }

    @Override
    public void bindData(IChatUser item) {
        if (item != null) {
            model = item;
            ImageHelper
                .loadAvatarPrivate(item.getAvatarUrl(), iv_avatar, item.isValid(), item.isQuit());
            tv_name
                .setText(ChatHelper
                    .getUserRemarkName(item.getRemarkName(), item.getUserNick(), item.getUserId()));
            ChatHelper.setAuthenticationDrawable((UserBean) item, iv_identify);
            tv_company.setText(item.getWorkAddress());
            tv_department.setText(item.getDptName());
            initFirstChar();
        }
    }

    private void init(View v) {
        tv_head = v.findViewById(R.id.tv_header);
        iv_avatar = v.findViewById(R.id.iv_avatar);
        tv_name = v.findViewById(R.id.tv_name);
        ll_identify = v.findViewById(R.id.ll_identify);
        iv_identify = v.findViewById(R.id.iv_identify);
        tv_company = v.findViewById(R.id.tv_company);
        tv_department = v.findViewById(R.id.tv_department);
        tv_count = v.findViewById(R.id.tv_count);

        v.setOnClickListener(view -> {
            if (listenter != null && model != null) {
                listenter.onClick(model);
            }
        });

        v.setOnLongClickListener(v1 -> {
            if (listenter != null && model != null) {
                listenter.onLongClick(model);
            }
            return false;
        });
        int factor = SPHelper.getInt("font_size", 1) * 2;
        if (factor <= 2) {
            factor = 2;
        } else if (factor > 4) {
            factor = 4;
        }
        tv_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 12);
        tv_company.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 10);
        tv_department.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 10);
    }

    private void updateWidth(View v) {
        v.setMinimumWidth((int) TDevice.getScreenWidth());
    }

    void setPreModel(IChatUser user, int position) {
        if (user != null) {
            if (user.isStar()) {
                currentIndex = "星标好友";
            } else {
                currentIndex = user.getFirstChar();

            }
        }
        currentPosition = position;
    }

    //首字母索引
    private void initFirstChar() {
        if (currentPosition == 0) {
            if (model.isStar()) {
                tv_head.setVisibility(View.VISIBLE);
                tv_head.setText("星标好友");
                currentIndex = "星标好友";
            } else {
                if (!TextUtils.isEmpty(model.getFirstChar())) {
                    String header = model.getFirstChar().substring(0, 1);
                    if (!TextUtils.isEmpty(header)) {
                        tv_head.setVisibility(View.VISIBLE);
                        tv_head.setText(header);
                        currentIndex = header;
                    } else {
                        tv_head.setVisibility(View.GONE);
                    }
                } else {
                    tv_head.setVisibility(View.GONE);
                }
            }
        } else if (currentPosition < numNormal) {
            if (model.isStar()) {
                tv_head.setVisibility(View.GONE);
            } else {
                if (!TextUtils.isEmpty(model.getFirstChar())) {
                    String header = model.getFirstChar().substring(0, 1);
                    if (TextUtils.isEmpty(currentIndex)) {
                        if (!TextUtils.isEmpty(header)) {
                            tv_head.setVisibility(View.VISIBLE);
                            tv_head.setText(header);
                            currentIndex = header;
                        }
                    } else {
                        if (!TextUtils.isEmpty(header)) {
                            if (currentIndex.equals(header)) {
                                tv_head.setVisibility(View.GONE);
                            } else {
                                tv_head.setVisibility(View.VISIBLE);
                                tv_head.setText(header);
                                currentIndex = header;
                            }
                        } else {
                            tv_head.setVisibility(View.GONE);
                        }
                    }
                } else {
                    tv_head.setVisibility(View.GONE);
                }
            }
        } else if (currentPosition >= numNormal) {
            if (currentPosition == numNormal) {
                tv_head.setVisibility(View.VISIBLE);
                tv_head.setText("#");
            } else {
                tv_head.setVisibility(View.GONE);
            }
        }
    }

    void setNormalSize(int num) {
        numNormal = num;
    }

    public void showCountBottom(int count) {
        if (tv_count == null) {
            return;
        }
        if (count > 0) {
            tv_count.setVisibility(View.VISIBLE);
            tv_count.setText("总共有" + count + "位联系人");
        } else {
            tv_count.setVisibility(View.GONE);
        }
    }

}
