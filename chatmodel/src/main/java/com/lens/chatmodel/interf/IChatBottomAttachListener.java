package com.lens.chatmodel.interf;

import com.lens.chatmodel.ChatEnum.ETransforModel;

/**
 * Created by LL130386 on 2017/12/19.
 */

public interface IChatBottomAttachListener {

    void clickForword(ETransforModel type);

    void clickCollect();

    void clickDele();


    void clickAttach();

}
