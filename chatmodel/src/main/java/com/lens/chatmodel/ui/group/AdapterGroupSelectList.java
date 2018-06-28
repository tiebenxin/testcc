package com.lens.chatmodel.ui.group;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xhdl0002 on 2018/1/12.
 */

public class AdapterGroupSelectList extends AbstractRecyclerAdapter<UserBean> {

    private final static int HERDER = 0;
    private final static int NORMAL = 1;

    public AdapterGroupSelectList(Context ctx) {
        super(ctx);
        selectUsers = new ArrayList<>();
    }

    public void setData(List<UserBean> data) {
        if (data != null) {
            mBeanList = data;
        }
        notifyDataSetChanged();
    }

    public List<UserBean> getData() {
        if (null == mBeanList) {
            return new ArrayList<>();
        }
        return mBeanList;
    }

    //群成员id
    private List<String> groupUsers;
    //选人模式
    private int selectMode;

    public ArrayList<UserBean> getSelectUsers() {
        if (null == selectUsers) {
            return new ArrayList<>();
        }
        return selectUsers;
    }

    public void setSelectUsers(ArrayList<UserBean> selectUsers) {
        this.selectUsers = selectUsers;
    }

    //选中集合
    private ArrayList<UserBean> selectUsers;

    public void setSelectListener(GroupSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    private GroupSelectListener selectListener;

    public void setGroupUsers(List<String> groupUsers) {
        this.groupUsers = groupUsers;
    }

    public void setSelectMode(int selectMode) {
        this.selectMode = selectMode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SelectHolder(View.inflate(mContext, R.layout.item_group_friend, null));
    }

    @Override
    public int getItemCount() {
        return (hasMuc() ? super.getItemCount() + 1
            : super.getItemCount());
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SelectHolder holder = (SelectHolder) viewHolder;
        if (hasMuc() && 0 == position) {
            holder.tvIndex.setVisibility(View.GONE);
            holder.ivHeader.setVisibility(View.GONE);
            holder.iv_authentication.setVisibility(View.GONE);
            holder.cb.setVisibility(View.GONE);
            holder.ll_work_info.setVisibility(View.GONE);
            holder.tvName.setText("选择一个群");
            holder.friendRoot.setOnClickListener((v) -> {
                //选择群列表
                selectListener.selectMucInfo();
            });
            return;
        }
        if (hasMuc()) {
            position = position - 1;
        }
        UserBean userBean = mBeanList.get(position);
        holder.bindData(userBean);
        holder.tvName.setText(TextUtils.isEmpty(userBean.getUserNick()) ? userBean.getUserId()
            : userBean.getUserNick());
        holder.tvWork
            .setText(
                TextUtils.isEmpty(userBean.getWorkAddress()) ? "" : userBean.getWorkAddress());
        holder.tvDptName
            .setText(TextUtils.isEmpty(userBean.getDptName()) ? "" : userBean.getDptName());
        if (ChatHelper.isGroupChat(userBean.getChatType())) {
            holder.ivHeader.setDrawText(
                MucInfo.selectMucUserNick(ContextHelper.getContext(), userBean.getUserId()));
        } else {
            holder.ivHeader.setChatType(true);
            ImageHelper.loadAvatarPrivate(userBean.getAvatarUrl(), holder.ivHeader);
        }
        holder.iv_authentication.setVisibility(View.GONE);
//        ChatHelper.setAuthenticationDrawable(userBean, holder.iv_authentication);

        switch (selectMode) {
            case Constant.GROUP_SELECT_MODE_ADD:
                ////如果添加群成员的话，需要判断是否已经在群中
                if (null != groupUsers && groupUsers.contains(userBean.getUserId())) {
                    holder.cb.setEnabled(false);
                    holder.cb.setChecked(true);
                    holder.friendRoot.setEnabled(false);
                } else {
                    holder.cb.setEnabled(true);
                    holder.friendRoot.setEnabled(true);
                    holder.cb.setChecked(selectUsers.contains(userBean) ? true : false);
                }
                break;
            default:
                holder.cb.setEnabled(true);
                holder.friendRoot.setEnabled(true);
                holder.cb.setChecked(selectUsers.contains(userBean) ? true : false);
                break;

        }
        holder.friendRoot.setOnClickListener((v) -> {
            //判断选择模式
            //单选
            if (Constant.GROUP_SELECT_MODE_CHANGE_ROLE == selectMode) {
                selectUsers.clear();
                selectUsers.add(userBean);
                selectListener.showSelectedView(true, false);
                notifyDataSetChanged();
            } else {
                //选中或反选
                if (selectUsers.contains(userBean)) {
                    selectUsers.remove(userBean);
                    holder.cb.setChecked(false);
                    selectListener.showSelectedView(false, true);
                } else {
                    holder.cb.setChecked(true);
                    selectUsers.add(userBean);
                    selectListener.showSelectedView(false, false);
                }
            }
        });
        String str = "";
        //得到当前字母
        String currentLetter = userBean.getFirstChar() + "";
        if (position == 0) {
            str = currentLetter;
        } else {
            //得到上一个字母
            String preLetter = mBeanList.get(position - 1).getFirstChar() + "";
            //如果和上一个字母的首字母不同则显示字母栏
            if (!preLetter.equalsIgnoreCase(currentLetter)) {
                str = currentLetter;
            }

            int nextIndex = position + 1;
            if (nextIndex < mBeanList.size() - 1) {
                //得到下一个字母
                String nextLetter = mBeanList.get(nextIndex).getFirstChar() + "";
                //如果和下一个字母的首字母不同则隐藏下划线
                if (!nextLetter.equalsIgnoreCase(currentLetter)) {
                    holder.tvIndex.setVisibility(View.VISIBLE);
                }
            }
        }
        //根据str是否为空决定字母栏是否显示
        if (TextUtils.isEmpty(str)) {
            holder.tvIndex.setVisibility(View.GONE);
        } else {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(str);
        }
        if (position == mBeanList.size() - 1) {
            holder.showCountTv.setVisibility(View.VISIBLE);
            holder.showCountTv.setText("总共有" + mBeanList.size() + "位联系人");
        } else {
            holder.showCountTv.setVisibility(View.GONE);
        }
    }

    static class SelectHolder extends RecyclerView.ViewHolder {

        View friendRoot;
        TextView tvIndex;
        AvatarImageView ivHeader;
        TextView tvName;
        CheckBox cb;
        TextView showCountTv;
        TextView tvWork;
        TextView tvDptName;
        ImageView iv_authentication;
        private final LinearLayout ll_work_info;

        public SelectHolder(View itemView) {
            super(itemView);
            friendRoot = itemView.findViewById(R.id.friend_root);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            ivHeader = itemView.findViewById(R.id.ivHeader);
            tvName = itemView.findViewById(R.id.tvName);
            cb = itemView.findViewById(R.id.cb);
            showCountTv = itemView.findViewById(R.id.show_count_tv);
            tvWork = itemView.findViewById(R.id.tv_work);
            tvDptName = itemView.findViewById(R.id.tv_dptName);
            iv_authentication = itemView.findViewById(R.id.ivStatus);
            ll_work_info = itemView.findViewById(R.id.ll_work_info);
        }

        public void bindData(UserBean bean) {

        }
    }

    /**
     * 通过名字或者拼音搜索
     *
     * @param str 过滤字符
     * @param mAllContactsList 过滤集合
     * @return 过滤后的集合
     */
    public List<UserBean> searchContact(final String str, List<UserBean> mAllContactsList) {
        if (TextUtils.isEmpty(str)) {
            return mAllContactsList;
        }
        List<UserBean> filterList = new ArrayList<>();// 过滤后的list
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            for (UserBean contact : mAllContactsList) {
                if (contact.getUserNick() != null) {
                    if (contact.getUserNick().contains(str)) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        } else {
            for (UserBean contact : mAllContactsList) {
                if (contact.getUserNick() != null) {
                    //姓名全匹配,姓名首字母匹配,姓名全字母匹配
                    if (contact.getUserNick().toLowerCase(Locale.CHINESE)
                        .startsWith(str.toLowerCase(Locale.CHINESE))
                        || contact.getPinYin().toLowerCase(Locale.CHINESE)
                        .contains(str.toLowerCase(Locale.CHINESE))
                        ) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    private boolean hasMuc() {
        if (selectMode == Constant.GROUP_SELECT_MODE_CARD
            || selectMode == Constant.MODE_TRANSFOR_MSG) {
            return true;
        } else {
            return false;
        }
    }
}
