package com.lens.chatmodel.eventbus;

import com.lensim.fingerchat.commons.interf.IEventProduct;


/**
 * Created by xhdl0002 on 2018/2/12.
 */

public class MucRefreshEvent implements IEventProduct {
    private Integer type;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public MucRefreshEvent(Integer type) {
        this.type = type;
    }

    /***
     * 创建muc刷新event
     * @param refreshEnum 刷新enum
     * @return
     */
    public static MucRefreshEvent createMucRefreshEvent(MucRefreshEvent.MucRefreshEnum refreshEnum) {
        return (MucRefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MUC_REFRESH_MESSAGE, Integer.valueOf(refreshEnum.value));
    }

    public enum MucRefreshEnum {

        MUC_OPTION(0),//群option操作
        GROUP_LIST_REFRESH(1);//群聊刷新

        public final int value;

        MucRefreshEnum(int value) {
            this.value = value;
        }

        public static MucRefreshEnum fromInt(int value) {
            MucRefreshEnum result = null;
            for (MucRefreshEnum item : MucRefreshEnum.values()) {
                if (item.value == value) {
                    result = item;
                    break;
                }
            }
            if (result == null) {
                throw new IllegalArgumentException("MucRefreshEnum - fromInt");
            }
            return result;
        }
    }
}
