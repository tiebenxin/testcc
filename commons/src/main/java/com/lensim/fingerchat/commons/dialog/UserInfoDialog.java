package com.lensim.fingerchat.commons.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.lensim.fingerchat.commons.R;
import com.lensim.fingerchat.commons.app.AppManager;
import com.lensim.fingerchat.commons.global.FGEnvironment;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.ICreateListener;
import com.lensim.fingerchat.commons.utils.ImageLoader;

/**
 * Created by LY309313 on 2016/8/16.
 */

public class UserInfoDialog extends BaseDialog implements ICreateListener {

    private ImageView mIvHead;
    private TextView mTvName;
    private ImageView mIvCode;
    private Context mContext;
    private String user;
    private FGEnvironment environment;
    private ProgressBar progressBar;
    private String avatarUrl;
    private IChatUser userBean;
    private String userNick;

    public UserInfoDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public UserInfoDialog(Context context, int theme, String user, String avatar, String nick) {
        super(context, theme);
        mContext = context;
        this.user = user;
        avatarUrl = avatar;
        userNick = nick;
    }

    public UserInfoDialog(Context context) {
        super(context);
    }

    @Override
    public void initView() {
        setContentView(R.layout.pop_userinfo);
        mIvHead = findViewById(R.id.iv_userhead);
        mTvName = findViewById(R.id.tv_username);
        mIvCode = findViewById(R.id.iv_acode);
        progressBar = findViewById(R.id.progressbar);
        showACode(false);

    }

    @Override
    public void initdata() {
        environment = (FGEnvironment) FGEnvironment.getInstance();
        AppManager.getInstance()
            .initAcode(user, avatarUrl, this);//初始化个人二维码
        mTvName
            .setText(!TextUtils.isEmpty(userNick) ? userNick
                : user);
        ImageLoader.loadAvatarPrivate(avatarUrl, mIvHead);


    }

    @Override
    public void processClick(View view) {

    }

    private void showACode(boolean b) {
        if (b) {
            mIvCode.setVisibility(View.VISIBLE);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
        } else {
            mIvCode.setVisibility(View.GONE);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void createCode() {
        if (environment == null) {
            return;
        }
        ImageLoader.loadImage(environment.getAcodePath(user), mIvCode);
        showACode(true);
    }
}
