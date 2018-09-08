package com.lensim.fingerchat.fingerchat.model.result;

import com.lensim.fingerchat.commons.base.BaseResponse;

public class NewOATokenResult extends BaseResponse<NewOATokenResult.Data>{

    public final static class Data {
        private String oaToken;
        private long lifetime;

        public String getOaToken() {
            return oaToken;
        }

        public void setOaToken(String oaToken) {
            this.oaToken = oaToken;
        }

        public long getLifetime() {
            return lifetime;
        }

        public void setLifetime(long lifetime) {
            this.lifetime = lifetime;
        }
    }

}
