package com.lens.chatmodel.base;

import com.lens.chatmodel.bean.EmojiconDefaultGroupData;
import com.lens.chatmodel.db.LensEmojiProvider;
import com.lens.chatmodel.interf.EmojiconInfoProvider;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.permission.PermissionExecutor;
import com.lensim.fingerchat.commons.permission.PermissionFactory;

/**
 * Created by LL130386 on 2018/3/1.
 */

public class ChatEnvironment {

    private static ChatEnvironment INSTANCE = new ChatEnvironment();
    private PermissionExecutor mPermissionExecutor;
    private EmojiconInfoProvider emojiconInfoProvider;


    public ChatEnvironment() {
    }

    public static ChatEnvironment getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatEnvironment();
        }
        return INSTANCE;
    }

    public void init() {
        initPermissionExcutor();
        initExpresionProvider();

    }

    private void initPermissionExcutor() {
        mPermissionExecutor = new PermissionExecutor(ContextHelper.getContext(),
            new PermissionFactory());
    }

    public PermissionExecutor getPermissionExecutor() {
        return mPermissionExecutor;
    }

    /**
     * 初始化表情
     */
    public void initExpresionProvider() {
        LensEmojiProvider provider = new LensEmojiProvider(EmojiconDefaultGroupData.getData());
        setEmojiconInfoProvider(provider);
    }

    /**
     * 获取表情提供者
     */
    public EmojiconInfoProvider getEmojiconInfoProvider() {
        return emojiconInfoProvider;
    }

    /**
     * 设置表情信息提供者
     */
    public void setEmojiconInfoProvider(EmojiconInfoProvider emojiconInfoProvider) {
        this.emojiconInfoProvider = emojiconInfoProvider;
    }


}
