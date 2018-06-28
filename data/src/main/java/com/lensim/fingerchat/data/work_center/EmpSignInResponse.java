package com.lensim.fingerchat.data.work_center;

import com.google.gson.annotations.SerializedName;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;


/**
 * date on 2018/1/9
 * author ll147996
 * describe
 */

public class EmpSignInResponse<T> {

    @SerializedName("GetEmpSignInResult")
    public RetArrayResponse<T> result;
}
