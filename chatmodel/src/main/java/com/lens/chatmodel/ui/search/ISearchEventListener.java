package com.lens.chatmodel.ui.search;

import com.lens.chatmodel.ChatEnum.EResultType;
import com.lens.chatmodel.bean.AllResult;
import com.lens.chatmodel.bean.SearchMessageBean;

/**
 * Created by LL130386 on 2018/4/27.
 */

public interface ISearchEventListener {

    void clickItem(EResultType type, SearchMessageBean bean);

    void clickMore(EResultType type, AllResult result);

}
