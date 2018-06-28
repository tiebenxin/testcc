package com.lensim.fingerchat.data.work_center;

import com.google.gson.annotations.SerializedName;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;

/**
 * date on 2018/1/9
 * author ll147996
 * describe
 */

public class SignInResponse<T> {
    @SerializedName("SignInResult")
    public RetObjectResponse<T> result;

}
