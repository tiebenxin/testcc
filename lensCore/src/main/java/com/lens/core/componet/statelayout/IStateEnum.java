package com.lens.core.componet.statelayout;

/**
 * Created by sj on 21/03/2017.
 */

public enum IStateEnum {

    onLoading,

    onEmpty,

    onError,

    onSucceed;

    public boolean isChangeFlag() {
        return this == onLoading;
    }

    public void invoke(IState iState, String lastFlag) {
        if(iState == null) {
            return;
        }
        switch (this) {
            case onLoading:
                iState.onLoading(lastFlag);
                break;
            case onEmpty:
                iState.onEmpty(lastFlag);
                break;
            case onError:
                iState.onError(lastFlag);
                break;
            case onSucceed:
                iState.onSucceed(lastFlag);
                break;
        }
    }
}
