package com.lensim.fingerchat.fingerchat.ui.search;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.interf.ISearchTypeClickListener;
import com.lensim.fingerchat.commons.global.CommonEnum.ESearchTabs;
import com.lensim.fingerchat.fingerchat.R;
import com.lens.chatmodel.interf.ISearchClickListener;

/**
 * Created by LL130386 on 2017/11/28.
 */

public class ControllerSearchContacts {

    private ISearchTypeClickListener listener;
    private EditText et_search;
    private ImageView iv_search;
    private ImageView iv_clear;
    private LinearLayout ll_search_container;
    private TextView tv_search_content;
    private ESearchTabs mSearchTab;

    public ControllerSearchContacts(View v) {
        init(v);
    }

    private void init(View v) {
        iv_search = v.findViewById(R.id.iv_search);
        iv_clear = v.findViewById(R.id.iv_clear);
        et_search = v.findViewById(R.id.et_search);

        tv_search_content = v.findViewById(R.id.tv_search_content);

        ll_search_container = v.findViewById(R.id.ll_search_container);
        ll_search_container.setVisibility(View.GONE);

        ll_search_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && et_search.getText() != null) {
                    listener.search(et_search.getText().toString(), true);
                }
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && !TextUtils.isEmpty(editable.toString())) {
                    iv_clear.setVisibility(View.VISIBLE);
                    setSearchContainerVisible(true);
                    tv_search_content.setText(editable.toString());
                } else {
                    iv_clear.setVisibility(View.GONE);
                    setSearchContainerVisible(false);
                    listener.search("", false);
                }
            }
        });

        iv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && et_search.getText() != null) {
                    listener.search(et_search.getText().toString(), true);
                }
            }
        });

        et_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    if (mSearchTab == ESearchTabs.SEARCH_TYPE) {
                        listener.search("", true);
                    } else {
                        listener.search("", false);
                    }
                }
            }
        });

        et_search.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                    || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                    || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (listener != null) {
                        if (et_search.getText() != null) {
                            listener.search(et_search.getText().toString(), true);
                        }
                    }
                }
                return true;
            }
        });

        iv_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    if (et_search.getText() != null) {
                        listener.search(et_search.getText().toString(), true);
                    }
                }
            }
        });

        iv_clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
            }
        });
    }

    public void setSearchContainerVisible(boolean flag) {
        if (flag) {
            ll_search_container.setVisibility(View.VISIBLE);
        } else {
            ll_search_container.setVisibility(View.GONE);
        }
    }

    public void clearText() {
        et_search.setText("");
    }

    public void setHint(int s) {
        et_search.setHint(s);
    }

    public void setOnClickListener(ISearchTypeClickListener l) {
        listener = l;
    }


    public void setSearchType(ESearchTabs tabs) {
        mSearchTab = tabs;
    }

    public void focus(boolean isFocus) {
        if (isFocus) {
            et_search.requestFocus();
        } else {
            et_search.clearFocus();
        }
    }

    public EditText getEditText() {
        return et_search;
    }
}
