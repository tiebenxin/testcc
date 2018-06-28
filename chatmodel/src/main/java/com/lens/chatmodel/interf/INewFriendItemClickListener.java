package com.lens.chatmodel.interf;

import com.lensim.fingerchat.commons.interf.IChatUser;

/**
 * Created by LL130386 on 2018/3/7.
 */

public interface INewFriendItemClickListener {

    void onAccept(IChatUser bean);

    void onClick(IChatUser bean);

    void onLongClick(IChatUser bean, int position);

}
