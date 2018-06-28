package com.lensim.fingerchat.data.work_center;

import android.support.annotation.NonNull;
import com.lensim.fingerchat.data.repository.SPDataRepository;
import java.util.List;


/**
 * date on 2018/1/5
 * author ll147996
 * describe
 */

public class WorkItemRepository {

    private List<WorkItem> workItems;
    private SPDataRepository<WorkItem> spDataRepository;


    private WorkItemRepository() {
        spDataRepository = new SPDataRepository<>();
    }

    public static WorkItemRepository getInstance(){
        return Singleton.INSTANCE;
    }

    private static class Singleton{
        private static final WorkItemRepository INSTANCE = new WorkItemRepository();
    }


    public List<WorkItem> getWorkItems() {
        if (workItems != null) {
            return workItems;
        } else {
            return spDataRepository.getDatas(WorkItem.class);
        }
    }

    public void setWorkItems(@NonNull Class<WorkItem> clazz, @NonNull List<WorkItem> workItems) {
        this.workItems = workItems;
        spDataRepository.saveDatas(workItems, clazz);
    }
}
