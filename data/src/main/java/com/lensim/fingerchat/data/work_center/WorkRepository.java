package com.lensim.fingerchat.data.work_center;

import com.lensim.fingerchat.db.work.*;
import com.lensim.fingerchat.db.work.WorkItem;

/**
 * date on 2018/8/10
 * author ll147996
 * describe
 */

public class WorkRepository {

    public static String getWorks(String uid) {
        if (WorkItemManager.getInstance().getWorkItem(uid) == null) return null;
        return WorkItemManager.getInstance().getWorkItem(uid).getWorks();
    }

    public static String getSortWorks(String uid) {
        if (WorkItemManager.getInstance().getWorkItem(uid) == null) return null;
        return WorkItemManager.getInstance().getWorkItem(uid).getSortWorks();
    }


    public static void updateWorkItem(String uid, String works) {
        com.lensim.fingerchat.db.work.WorkItem workItem = new WorkItem();
        workItem.setUid(uid);
        workItem.setWorks(works);
        WorkItemManager.getInstance().updateWorkItem(uid, workItem);
    }

    public static void updateSortWorks(String uid, String sortWorks) {
        WorkItemManager.getInstance().updateSortWorks(uid, sortWorks);
    }
}
