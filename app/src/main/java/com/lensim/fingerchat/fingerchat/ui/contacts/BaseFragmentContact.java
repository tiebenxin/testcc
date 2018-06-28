package com.lensim.fingerchat.fingerchat.ui.contacts;

import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lens.chatmodel.interf.IContactItemClickListener;

/**
 * Created by LL130386 on 2018/2/6.
 */

public class BaseFragmentContact extends BaseFragment {

    public IContactItemClickListener mItemClickListener;

    @Override
    protected void initView() {

    }

    public void setItemClickListener(IContactItemClickListener l){
        mItemClickListener = l;
    }
}
