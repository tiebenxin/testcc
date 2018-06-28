package com.lens.chatmodel.ui.group;

import android.view.View;

/**
 * Created by xhdl0002 on 2018/1/11.
 */

public interface GroupOperationListener extends View.OnClickListener{
    void toUserView(int position);

    void operationGroupUser(boolean type);
}
