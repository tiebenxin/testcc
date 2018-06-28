package com.lensim.fingerchat.data.hrcs;

import com.google.gson.annotations.SerializedName;
import com.lensim.fingerchat.data.response.ret.RetArrayResponse;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;

/**
 * date on 2018/1/16
 * author ll147996
 * describe
 */

public class GetHRCSResult<T> {
    @SerializedName("GetHRCSResult")
    public RetArrayResponse<T> result;
}
