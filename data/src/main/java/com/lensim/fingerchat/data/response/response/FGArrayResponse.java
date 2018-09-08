package com.lensim.fingerchat.data.response.response;

import com.lensim.fingerchat.data.response.ret.RetResponse;
import java.util.List;

/**
 * Created by LL130386 on 2018/8/14.
 */

public class FGArrayResponse<T> extends RetResponse {

    public List<T> content;

}
