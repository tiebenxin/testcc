package com.lensim.fingerchat.commons.base.data;

import com.google.gson.Gson;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class BaseRequestBody<T> {
    private static final String MEDIA_TYPE = "application/json; charset=utf-8";

    T body;

    public BaseRequestBody(T body) {
        this.body = body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public RequestBody toRequestBody() {
        String jsonParams;
        try {
            Gson gson = new Gson();
            jsonParams = gson.toJson(body);
        }catch (Exception e) {
            jsonParams = "";
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse(MEDIA_TYPE), jsonParams);
        return requestBody;
    }
}
