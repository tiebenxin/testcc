package com.lensim.fingerchat.commons.mvp.factory;

import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by ll147996 on 2017/12/12.
 * 标注创建 Presenter 的注解
 */

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface CreatePresenter {
    Class<? extends BaseMvpPresenter> value();
}
