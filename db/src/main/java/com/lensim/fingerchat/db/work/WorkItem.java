package com.lensim.fingerchat.db.work;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

/**
 * date on 2018/8/10
 * author ll147996
 * describe
 */

@Entity
public class WorkItem {

    @Id(autoincrement = true)
    private Long id;

    /**
     * 用户id
     */
    private String uid;

    /**
     *   未拖拽排序前的funcId”拼接而成
     */
    @NotNull
    private String works;

    /**
     *  拖拽排序后的funcId”拼接而成
     */
    private String sortWorks;

    @Generated(hash = 685928880)
    public WorkItem(Long id, String uid, @NotNull String works, String sortWorks) {
        this.id = id;
        this.uid = uid;
        this.works = works;
        this.sortWorks = sortWorks;
    }

    @Generated(hash = 553797397)
    public WorkItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getWorks() {
        return works;
    }

    public void setWorks(String works) {
        this.works = works;
    }

    public String getSortWorks() {
        return sortWorks;
    }

    public void setSortWorks(String sortWorks) {
        this.sortWorks = sortWorks;
    }
}
