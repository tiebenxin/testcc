package com.lensim.fingerchat.fingerchat.base;

import android.support.annotation.DrawableRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lensim.fingerchat.commons.mvp.presenter.BaseMvpPresenter;
import com.lensim.fingerchat.commons.mvp.view.BaseMvpView;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Toolbar管理
 * Created by zm on 2018/6/4.
 */
public abstract class BaseToolbarAppCompatActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends FGAppCompatActivity {
    protected Toolbar toolbar;
    protected ImageButton ibLeft;
    protected ImageButton ibRight;
    protected Button btnRight;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBack();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化ToolBar
     *
     * @param title
     */
    protected void initToolBar(String title) {
        toolbar = findViewById(R.id.tool_bar);
        if (toolbar != null) {

            // title
            TextView tvTitle = toolbar.findViewById(R.id.toolbar_title);
            if (tvTitle != null) {
                tvTitle.setText(title);
            }

            ibLeft = toolbar.findViewById(R.id.ib_left);
            btnRight = toolbar.findViewById(R.id.btn_right);
            ibRight = toolbar.findViewById(R.id.ib_right);

            setSupportActionBar(toolbar);
            // 默认为非首页
            showDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * 返回上一级页面
     */
    private void onBack() {
        finish();
    }

    protected void showLeftImgBtn(@DrawableRes int drawableId) {
        if (ibLeft != null) {
            ibLeft.setVisibility(View.VISIBLE);
            ibLeft.setImageResource(drawableId);
            ibLeft.setOnClickListener(v ->
                handLeftImgBtnClick());
        }
    }

    protected void showRightImgBtn(@DrawableRes int drawableId) {
        if (ibRight != null) {
            ibRight.setVisibility(View.VISIBLE);
            ibRight.setImageResource(drawableId);
            ibRight.setOnClickListener(v ->
                handRightImgBtnClick());
        }
    }

    protected void showRightBtn() {
        if (btnRight != null) {
            btnRight.setVisibility(View.VISIBLE);
            btnRight.setOnClickListener(v ->
                handBtnClick());
        }
    }

    /**
     * 是否启用返回键
     *
     * @param isHome
     */
    protected void showDisplayHomeAsUpEnabled(boolean isHome) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(isHome);
        }
    }

    protected void handLeftImgBtnClick() {

    }

    protected void handRightImgBtnClick() {

    }

    protected void handBtnClick() {

    }
}
