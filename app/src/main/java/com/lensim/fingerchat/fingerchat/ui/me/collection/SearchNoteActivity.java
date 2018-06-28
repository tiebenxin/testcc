package com.lensim.fingerchat.fingerchat.ui.me.collection;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.FragmentSearchNoteBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.SearchNoteFragment;

/**
 * 收藏——搜索
 * Created by LL117394 on 2017/05/27
 */

public class SearchNoteActivity extends BaseActivity {

    public final static int REQUEST_FOR_SEARCH = 332;
    SearchNoteFragment mainFragment = null;
    private boolean isMarkChaned = false;
    FragmentSearchNoteBinding ui;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.fragment_search_note);
        setSupportActionBar(ui.mSearchNoteToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ui.mSearchSection.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override public void afterTextChanged(Editable s) {
                if (null != mainFragment) mainFragment.search(s);
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mainFragment = (SearchNoteFragment) getSupportFragmentManager().findFragmentByTag(mainFragment.getClass().getName());
            getSupportFragmentManager().beginTransaction().show(mainFragment).commit();
        } else {
            mainFragment = SearchNoteFragment.newInstance("Main");
            getSupportFragmentManager().beginTransaction().add(R.id.mSearchContainer, mainFragment, mainFragment.getClass().getName()).commit();
        }
    }


    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        if (null != mainFragment && isMarkChaned) {
            Intent intent = new Intent();
            intent.putExtra("dataChanged", true);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onReturn();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CollectionDetailActivity.REQUEST_FOR_DETAIL && resultCode == Activity.RESULT_OK) {
            boolean isMarkChanged = false;
            if (data != null) {
                isMarkChanged = data.getBooleanExtra("isMarkChanged", false);
            }
            if (mainFragment != null && isMarkChanged) {
                mainFragment.refresh();
                isMarkChaned = true;
            }
        }
    }
}

