package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.SparseIntArray;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.interf.IContactItemClickListener;
import com.lens.chatmodel.interf.IContactListener;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/30.
 * 联系人adapter
 */

public class AdapterContacts extends AbstractRecyclerAdapter<UserBean> implements SectionIndexer {

    private final int HEAD = 0;
    private final int NORMAL = 1;

    private boolean showHead;
    private IContactListener listener;

    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    ArrayList list;
    private IContactItemClickListener itemListener;
    private int numNormal;
    private int numNoNick;
    private int unreadNum;


    AdapterContacts(Context ctx) {
        super(ctx);
    }

    public void setData(List<UserBean> l, int normal, int unusual) {
        if (mBeanList != null && mBeanList.size() > 0) {
            mBeanList.clear();
            mBeanList.addAll(l);
        } else {
            mBeanList = l;
        }
        numNormal = normal;
        numNoNick = unusual;
        notifyDataSetChanged();
    }

    void showHead() {
        showHead = true;
        notifyItemChanged(0);
    }

    void hideHead() {
        showHead = false;
        notifyItemChanged(0);
    }

    public void setUnreadNum(int n) {
        unreadNum = n;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEAD && showHead) {
            return new ControllerContactHead(mInflater.inflate(R.layout.item_contact_head, null),
                listener);
        } else {
            return new ControllerContactItem(mInflater.inflate(R.layout.item_contacts_cell, null),
                itemListener);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == 0 && showHead) {
            ((ControllerContactHead) holder).setSelfAvatar(UserInfoRepository.getImage());
            ((ControllerContactHead) holder).setUnread(unreadNum);
        } else {
            ((ControllerContactItem) holder).setNormalSize(numNormal);
            if (showHead) {
                if (position > 1) {
                    ((ControllerContactItem) holder)
                        .setPreModel(mBeanList.get(position - 2), position - 1);
                } else {
                    ((ControllerContactItem) holder)
                        .setPreModel(null, position - 1);
                }
                if (position == getItemCount() -1){
                    ((ControllerContactItem) holder).showCountBottom(mBeanList.size());
                }else {
                    ((ControllerContactItem) holder).showCountBottom(0);
                }
                ((ControllerContactItem) holder).bindData(mBeanList.get(position - 1));
            } else {
                if (position > 0) {
                    ((ControllerContactItem) holder)
                        .setPreModel(mBeanList.get(position - 1), position);
                } else {
                    ((ControllerContactItem) holder)
                        .setPreModel(null, position);
                }
                if (position == getItemCount() -1){
                    ((ControllerContactItem) holder).showCountBottom(mBeanList.size());
                }else {
                    ((ControllerContactItem) holder).showCountBottom(0);
                }
                ((ControllerContactItem) holder).bindData(mBeanList.get(position));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showHead) {
            return HEAD;
        } else {
            return NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        if (showHead) {
            return mBeanList.size() + 1;
        } else {
            return mBeanList.size();
        }
    }

    public void setOnHeaderItemClick(IContactListener l) {
        listener = l;
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getItemCount();
        if (showHead) {
            count -= 2;
        } else {
            count -= 1;
        }
        list = new ArrayList();
        list.add("星");
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        int offset = showHead ? 1 : 0;

        //section->首字母在右侧列表中对应的位置，position->对应联系人列表的位置
        for (int i = 0; i < count; i++) {
            IChatUser item = mBeanList.get(i);
            String letter = item.getFirstChar();
            if (list.size() > 0) {
                int section = list.size() - 1;
                if (list.get(section) != null && list.size() > 0 && !list.get(section)
                    .equals(letter)) {
                    list.add(letter);
                    section++;
                    positionOfSection.put(section, i + offset);
                }
                sectionOfPosition.put(i + offset, section);
            }
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return positionOfSection.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    public void setOnItemClickListener(IContactItemClickListener l) {
        itemListener = l;
    }


}
