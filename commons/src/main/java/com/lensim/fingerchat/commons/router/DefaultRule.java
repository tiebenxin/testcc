package com.lensim.fingerchat.commons.router;

import android.content.Intent;

/**
 * date on 2018/3/9
 * author ll147996
 * describe
 */

public class DefaultRule implements Rule {

    @Override
    public Intent setRule(Intent intent, String uri) {
        return intent;
    }
}
