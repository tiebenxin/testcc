package com.lens.chatmodel.interf;

import com.lens.chatmodel.bean.message.RecentMessage;

/**
 * Created by LL130386 on 2017/11/25.
 */

public interface IChatItemClickListener {

    void clickAvatar(RecentMessage model);

    void click(RecentMessage model);

    void onLongClick(RecentMessage model);

}
