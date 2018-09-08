package com.lensim.fingerchat.fingerchat.ui.work_center;



import com.lensim.fingerchat.commons.mvp.view.ProcessMvpView;
import com.lensim.fingerchat.data.work_center.WorkItem;


import java.util.List;

/**
 * date on 2017/12/23
 * author ll147996
 * describe
 */

public interface WorkCenterView extends ProcessMvpView {

    void setItems(List<?> items);
    void showIdentifyDialog();
    void startBrowserActivity(String url, String title, int isHasNav);
    List<?> getItems();
    void toHexMeetActivity();
    void showMsg(String msg);
}
