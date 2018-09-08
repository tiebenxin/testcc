package com.lensim.fingerchat.fingerchat.ui.me.photo;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import com.lens.route.annotation.Path;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCommentDetailBinding;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;

/**
 * Created by ll147996 on 2017/12/15.
 * 个人中心->相册->评论->详情
 */

@Path(ActivityPath.COMMENT_DETAIL_ACTIVITY_PATH)
public class CommentDetailActivity extends FGActivity {

    public static final String NEW_COMEMNT = "newComment";
    public static final String CIRCLE_ITEM = "circleItem";
    public static final String PHOTO_SERO = "photoSerno";

    private NewComment newComment;
    private CommentDetailFragment fragment;
    private ActivityCommentDetailBinding ui;
    private PhotoBean photoBean;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_comment_detail);
        initBackButton(ui.mDetailToolBar, true);
        ui.mDetailToolBar.setTitleText("详情");
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        newComment = getIntent().getParcelableExtra(NEW_COMEMNT);
        String pSero = getIntent().getStringExtra(PHOTO_SERO);
        CircleItem item = getIntent().getParcelableExtra(CIRCLE_ITEM);

        if (savedInstanceState != null) {
            fragment = (CommentDetailFragment) getSupportFragmentManager().getFragment(savedInstanceState, newComment.getPHO_Serno());
            if (fragment == null) {
                fragment = CommentDetailFragment.newInstance(newComment, photoBean,pSero);
            }
            getSupportFragmentManager().beginTransaction().add(R.id.mFragmentContainer, fragment).commit();
        } else {
            fragment = CommentDetailFragment.newInstance(newComment, photoBean,pSero);
            getSupportFragmentManager().beginTransaction().add(R.id.mFragmentContainer, fragment).commit();
        }
    }

    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        Intent intent = fragment.setResult();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = fragment.setResult();
            setResult(RESULT_OK, intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outState, newComment.getPHO_Serno(), fragment);
        super.onSaveInstanceState(outState);
    }
}
