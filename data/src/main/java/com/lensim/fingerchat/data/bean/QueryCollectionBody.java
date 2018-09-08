package com.lensim.fingerchat.data.bean;

/**
 * @time 2017/11/29 8:59
 * @class describe
 */

public class QueryCollectionBody {

    /**
     * creator : string
     * keyword : string
     * page : {"pageIndex":0,"pageSize":0}
     */

    private String creator;
    private String keyword;
    private PageBean page;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public PageBean getPage() {
        return page;
    }

    public void setPage(PageBean page) {
        this.page = page;
    }

    public static class PageBean {

        /**
         * pageIndex : 0
         * pageSize : 0
         */

        private int pageIndex;
        private int pageSize;

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}
