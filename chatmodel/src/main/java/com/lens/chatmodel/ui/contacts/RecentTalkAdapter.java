package com.lens.chatmodel.ui.contacts;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.helper.ChatHelper;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.widgt.AvatarImageView;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import java.util.ArrayList;
import java.util.List;


public class RecentTalkAdapter extends RecyclerView.Adapter<RecentTalkAdapter.RcTalkViewHolder> {


    private static final String TAG = "RecentTalkAdapter";
    List<String> list;
    private List<UserBean> userList;
    private List<UserBean> selected;
    private final LayoutInflater layoutInflater;

    private String condition;
    private boolean notiyfyByFilter;
    private boolean showImage;
    private Context context;

    private final int TYPE_WITH_HEAD = 11;
    private final int NORMAL_LIST = 1;

    //	public int transforType = InviteActivity.TRANSF_MSG;
    private RecentTalkAdapter.OnItemClickListener onItemClickListener;


    public RecentTalkAdapter(Context context, List<UserBean> objects) {
        this.context = context;
        this.userList = objects;
        this.layoutInflater = LayoutInflater.from(context);

    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_WITH_HEAD;
        } else {
            return NORMAL_LIST;
        }
    }


    public void clear() {
        userList.clear();
        notifyChange();
    }

    public void addAll(ArrayList<UserBean> users) {
        userList.addAll(users);
        notifyChange();
    }

    public void changeMode(boolean isSingleMode) {
        showImage = !isSingleMode;
    }


    public class RcTalkViewHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView nameView;
        private TextView headerView;
        private ImageView checkBox;
        private ImageView valid;
        private TextView mSingleView;
        private TextView mSummary;
        private AvatarImageView avatarView;

        public RcTalkViewHolder(View convertView, int viewtype) {
            super(convertView);
            if (viewtype == TYPE_WITH_HEAD) {
                mSingleView = ((TextView) convertView.findViewById(R.id.title));
            } else {
                bindContact(convertView, viewtype);
            }

        }

        private void bindContact(View view, int viewtype) {
            nameView = view.findViewById(R.id.name);
            headerView = view.findViewById(R.id.header);
            checkBox = view.findViewById(R.id.iv_checkbox);
            valid = view.findViewById(R.id.isValid);
            avatarView = view.findViewById(R.id.iv_msg_stub);
            mSummary = view.findViewById(R.id.contact_summary);
        }

        public void setNick(String nick) {
            nameView.setText(nick);
        }

        public void setCheck(boolean b) {
            if (b) {
                checkBox.setImageResource(R.drawable.click_check_box);
            } else {
                checkBox.setImageResource(R.drawable.check_box);
            }

        }
    }


    @Override
    public RcTalkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View convertView;
        if (viewType == TYPE_WITH_HEAD) {
            convertView = layoutInflater.inflate(R.layout.single_text_item, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onMoreContactClick();
                    }
                }
            });
        } else {
            convertView = layoutInflater.inflate(R.layout.lens_row_contact, parent, false);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        Object tag = convertView.getTag();
                        RcTalkViewHolder holder = null;
                        if (tag instanceof RcTalkViewHolder) {
                            holder = ((RcTalkViewHolder) tag);
                        }
                        if (holder != null) {
                            onItemClickListener
                                .onItemClick(convertView, ((UserBean) holder.nameView.getTag()));
                        }
                    }
                }
            });


        }
        RcTalkViewHolder contactViewHolder = new RcTalkViewHolder(convertView, viewType);
        convertView.setTag(contactViewHolder);
        return contactViewHolder;
    }

    @Override
    public void onBindViewHolder(RcTalkViewHolder holder, int position) {

        int type = getItemViewType(position);
        if (type == TYPE_WITH_HEAD) {
            holder.mSingleView.setText("创建新聊天");
//			bindHead(holder);
            return;
        }

        int index;
        int startIndex;

        index = position - 1;
        startIndex = 1;

        UserBean user = userList.get(index);

        if (!showImage) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
            if (selected != null) {
                if (selected.contains(user)) {
                    holder.checkBox.setImageResource(R.drawable.click_check_box);
                } else {
                    holder.checkBox.setImageResource(R.drawable.check_box);
                }
            }

        }
        UIHelper.setTextSize(10, holder.headerView, holder.mSummary);
        UIHelper.setTextSize(14, holder.nameView);
        String username =
            ChatHelper
                .getUserRemarkName(user.getRemarkName(), user.getUserNick(), user.getUserId());
//		if(user.getUserJid().contains("@" + ConnectionItem.DEFAULT_SERVER_MUC)){
//			AbstractChat chat = MessageManager.getInstance().getChat(AccountManager.getInstance().getUserjid(), user.getUserJid());
//			if(chat!= null && chat instanceof RoomChat){
//				username = ((RoomChat) chat).getGroupName();
//				if(username == null){
//					username = MUCManager.getInstance().getRoomname(chat.getUser());
//				}
//			}
//		}

        if (position == startIndex) {
            holder.headerView.setVisibility(View.VISIBLE);
            holder.headerView.setText("最近聊天");
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        holder.nameView.setTag(user);
        if (condition != null && !condition.equals("")) {
            if (user.getUserId().contains(condition) && !user.getUserId()
                .contains("@" /*+ ConnectionItem.DEFAULT_SERVER_MUC*/)) {
                //点亮summary
                holder.nameView.setText(username);
                holder.mSummary.setVisibility(View.VISIBLE);
                Spannable span = getSpan("账号:" + user.getUserId(), condition);
                holder.mSummary.setText(span);
            } else {
                holder.mSummary.setVisibility(View.GONE);
                Spannable nickSpan = getNickSpan(user, condition);
                holder.nameView.setText(nickSpan);
            }
        } else {
            holder.nameView.setText(username);
            holder.mSummary.setVisibility(View.GONE);
        }

        L.i("NewMsgAdapter", "消息列表重绘");
        if (ChatHelper.isGroupChat(user.getChatType())) {//是群聊
            holder.avatarView.setDrawText(
                MucInfo.selectMucUserNickList(ContextHelper.getContext(), user.getUserId()));
            holder.nameView.setText(user.getMucName());
        } else {
            ImageHelper.loadAvatarPrivate(user.getAvatarUrl(), holder.avatarView);
        }

    }


    private Spannable getNickSpan(UserBean bean, String condition) {
        String nick =
            StringUtils.isEmpty(bean.getUserNick()) ? bean.getUserId() : bean.getUserNick();
        String alpha = bean.getFirstChar();
        SpannableString spannableString;
        if (nick.contains(condition)) {
            spannableString = new SpannableString(nick);
            int start = nick.indexOf(condition);
            int end = start + condition.length();
            spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (alpha != null && alpha.contains(condition)) {
            spannableString = new SpannableString(nick);
            int start = alpha.indexOf(condition);
            int end = start + condition.length();
            spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString = new SpannableString(nick);
            spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), 0, 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return spannableString;
    }

    private Spannable getSpan(String message, String condition) {
        SpannableString ss = new SpannableString(message);
        int start = message.indexOf(condition);
        int end = start + condition.length();
        ss.setSpan(new ForegroundColorSpan(Color.GREEN), start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    @Override
    public int getItemCount() {

        return userList.size() + 1;
    }


    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public List<UserBean> getUserList() {
        return userList;
    }

    public void setUserList(List<UserBean> userList) {
        this.userList = userList;
        notifyDataSetChanged();
    }

    public void notifyChange() {
        notifyDataSetChanged();
    }


    public List<UserBean> getSelected() {
        return selected;
    }

    public void setSelected(List<UserBean> selected) {
        this.selected = selected;
    }


    public void setOnItemClickListener(RecentTalkAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {

        void onMoreContactClick();

        void onItemClick(View view, UserBean bean);
    }
}
