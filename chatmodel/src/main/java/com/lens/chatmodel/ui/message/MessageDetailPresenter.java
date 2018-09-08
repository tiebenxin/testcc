package com.lens.chatmodel.ui.message;


import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import java.util.List;

/**
 * Created by LL130386 on 2018/9/5.
 */

public class MessageDetailPresenter extends BaseMvpPresenter<MessageDetailView> {

    protected List<MucMemberItem> getReadedMembers() {
        return null;
    }

    protected List<MucMemberItem> getUnreadedMembers() {
        return null;
    }


}
