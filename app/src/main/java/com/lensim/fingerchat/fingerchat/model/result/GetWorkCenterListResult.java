package com.lensim.fingerchat.fingerchat.model.result;

import com.lensim.fingerchat.data.work_center.WorkItem;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.fingerchat.model.bean.PageBean;

import java.util.List;

public class GetWorkCenterListResult extends BaseResponse<GetWorkCenterListResult.Data> {

    public final static class Data {
        private PageBean page;
        private List<WorkItem> data;

        public PageBean getPage() {
            return page;
        }

        public void setPage(PageBean page) {
            this.page = page;
        }

        public List<WorkItem> getData() {
            return data;
        }

        public void setData(List<WorkItem> data) {
            this.data = data;
        }
    }
}
