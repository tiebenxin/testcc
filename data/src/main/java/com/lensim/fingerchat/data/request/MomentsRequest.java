package com.lensim.fingerchat.data.request;

/**
 * Created by LY309313 on 2017/5/26.
 *
 */

public class MomentsRequest {

    /// <summary>
    /// 功能名
    /// </summary>
    private String func;
    /// <summary>
    /// 创建者ID
    /// </summary>
    private String CreateUserid;
    /// <summary>
    /// 创建者名称
    /// </summary>
    private String CreateUsername;
    /// <summary>
    /// 评论者ID
    /// </summary>
    private String CommentUserid;
    /// <summary>
    /// 评论者名称
    /// </summary>
    private String CommentUsername;
    /// <summary>
    /// 朋友圈ID
    /// </summary>
    private String photoserno;
    /// <summary>
    /// 评论内容
    /// </summary>
    private String content;
    /// <summary>
    /// 回复者ID
    /// </summary>
    private String secondid = "";
    /// <summary>
    /// 回复者名称
    /// </summary>
    private String secondname = "";

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public String getCreateUserid() {
        return CreateUserid;
    }

    public void setCreateUserid(String createUserid) {
        CreateUserid = createUserid;
    }

    public String getCreateUsername() {
        return CreateUsername;
    }

    public void setCreateUsername(String createUsername) {
        CreateUsername = createUsername;
    }

    public String getCommentUserid() {
        return CommentUserid;
    }

    public void setCommentUserid(String commentUserid) {
        CommentUserid = commentUserid;
    }

    public String getCommentUsername() {
        return CommentUsername;
    }

    public void setCommentUsername(String commentUsername) {
        CommentUsername = commentUsername;
    }

    public String getPhotoserno() {
        return photoserno;
    }

    public void setPhotoserno(String photoserno) {
        this.photoserno = photoserno;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSecondid() {
        return secondid;
    }

    public void setSecondid(String secondid) {
        this.secondid = secondid;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }
}
