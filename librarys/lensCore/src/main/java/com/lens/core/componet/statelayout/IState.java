package com.lens.core.componet.statelayout;

/**
 * Created by zm on 2018/4/11.
 */

public interface IState {

    void onLoading(String flag);

    void onEmpty(String flag);

    void onError(String flag);

    void onSucceed(String flag);
}
