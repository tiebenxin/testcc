package com.lensim.fingerchat.db.work;


import android.support.annotation.NonNull;
import android.util.Log;
import com.lensim.fingerchat.db.GreenDaoManager;
import com.lensim.fingerchat.db.greendao.PasswordDao;
import com.lensim.fingerchat.db.greendao.WorkItemDao;
import com.lensim.fingerchat.db.login.Password;
import java.util.List;


/**
 * Created by ll147996 on 2018/1/19.
 *
 */

public class WorkItemManager {

    private static final WorkItemManager workItemManager = new WorkItemManager();
    private WorkItemDao workItemDao;

    private WorkItemManager() {
        workItemDao = GreenDaoManager.getInstance().getSession().getWorkItemDao();
    }


    public static WorkItemManager getInstance() {
        return workItemManager;
    }


    public WorkItem getWorkItem(String uid) {
        return queryWorkItem(uid);
    }

    private WorkItem queryWorkItem(@NonNull String uid) {
        if (workItemDao.loadAll() != null) {
            for (WorkItem workItem : workItemDao.loadAll()) {
                if (uid.equals(workItem.getUid())) {
                    return workItem;
                }
            }
        }
        return null;
    }



    public void updateSortWorks(String uid, String sortWorks) {
        WorkItem workItem = queryWorkItem(uid);
        if (workItem == null) {
            return;
        }
        workItemDao.delete(workItem);
        workItem.setSortWorks(sortWorks);
        workItemDao.insert(workItem);
    }

    public void updateWorkItem(String uid, WorkItem workItem) {
        WorkItem item = queryWorkItem(uid);
        if (item == null) {
            workItemDao.insert(workItem);
        } else if (!item.getWorks().equals(workItem.getWorks())) {
            workItemDao.delete(item);
            workItemDao.insert(workItem);
        }
    }
}

