package com.lensim.fingerchat.fingerchat.ui.me.collection;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityGroupsCreateBinding;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 收藏——添加标签
 * Created by LL117394 on 2017/5/15.
 */

public class CollectionMarkActivity extends FGActivity {

    private List<String> names;
    private List<String> dels;
    private ArrayList<String> mAddedList;
    private ArrayList<String> mAllLabelList;
    private final String SP_TAG_GROUP_LABELS = "group_labels";
    ActivityGroupsCreateBinding ui;

    public static Intent newIntent(Context context, ArrayList<String> addedlabels) {
        Intent intent = new Intent(context, CollectionMarkActivity.class);
        intent.putStringArrayListExtra("mAddedList", addedlabels);
        return intent;
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_groups_create);
        initBackButton(ui.addGroupsToolbar, true);
        ui.addGroupsToolbar.setTitleText("添加标签");
        ui.addGroupsToolbar.setConfirmBt(v -> myReturn());
        UIHelper.setTextSize2(14, ui.tvAllLabel);
        dels = new ArrayList<>();
        mAddedList = getIntent().getStringArrayListExtra("mAddedList");
        if (null == mAddedList) {
            mAddedList = new ArrayList<>();
        }
        getAllLabel();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        names = new ArrayList<>(mAllLabelList);
        ui.GroupsAll.setTags(names);
        ui.GroupsAll.setOnTagClickListener(position -> {
            if (position >= 0) {
                String name = names.get(position);
                L.i("GroupCreateActivity", "分组名称:" + name);
                if (mAllLabelList == null) return;

                if (mAddedList.contains(name)) {
                    mAddedList.remove(name);
                    if (!dels.contains(name)) {
                        dels.add(name);
                    }
                } else {
                    mAddedList.add(name);
                    if (dels.contains(name)) {
                        dels.remove(name);
                    }
                }
                ui.GroupsTop.setTags(mAddedList);
            }
        });

        ui.GroupsTop.setTags(mAddedList);
        ui.GroupsTop.setOnTagClickListener(position -> {
            if (position >= 0) {
                String remove = mAddedList.remove(position);
                if (!dels.contains(remove)) {
                    dels.add(remove);
                }
                ui.GroupsTop.setTags(mAddedList);
            }
        });
    }

    //左上角返回键
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            myReturn();
        }
        return super.onOptionsItemSelected(item);
    }

    //返回键
    @Override
    public void onBackPressed() {
        myReturn();
    }

    public void myReturn() {
        dismissProgress();
        String text = ui.GroupsTop.getText();
        Intent intent = new Intent();

        if (!StringUtils.isEmpty(text) && text.length() > 0) {
            if (!mAddedList.contains(text)) {
                mAddedList.add(text);
            }
            if (!mAllLabelList.contains(text)) {
                mAllLabelList.add(text);
            }
            saveAllLabel();
        }

        intent.putStringArrayListExtra("user_labels", mAddedList);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void getAllLabel() {
        mAllLabelList = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("CollectionMarkLabels", MODE_PRIVATE);
        String json = preferences.getString(SP_TAG_GROUP_LABELS, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<String>>() {
            }.getType();
            List<String> alterSamples = gson.fromJson(json, type);
            for (int i = 0, len = alterSamples.size(); i < len; i++) {
                mAllLabelList.add(alterSamples.get(i));
            }
        }
    }

    public void saveAllLabel() {
        if (null == mAllLabelList) {
            return;
        }
        SharedPreferences.Editor editor =
            getSharedPreferences("CollectionMarkLabels", MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(mAllLabelList);
        editor.remove(SP_TAG_GROUP_LABELS);
        editor.commit();
        editor.putString(SP_TAG_GROUP_LABELS, json);
        editor.commit();
    }
}
