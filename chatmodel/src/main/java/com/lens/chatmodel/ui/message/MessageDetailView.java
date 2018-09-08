package com.lens.chatmodel.ui.message;


import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;
import java.util.List;

/**
 * Created by LL130386 on 2018/9/5.
 */

public interface MessageDetailView extends BaseMvpView {

    void setReadedMembers(List<MucMemberItem> readedMembers);

    void setUnreadedMembers(List<MucMemberItem> unreadedMembers);


}
