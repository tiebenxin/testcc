package com.lensim.fingerchat.fingerchat.ui.search;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.lens.chatmodel.interf.ISearchClickListener;
import com.lensim.fingerchat.fingerchat.R;

/**
 * Created by LL130386 on 2017/11/28.
 */

public class ControllerSearch {

  private ISearchClickListener listener;
  private TextView et_search;
  private ImageView iv_search;
  private ImageView iv_clear;

  public ControllerSearch(View v) {
    init(v);
  }

  private void init(View v) {
    iv_search = v.findViewById(R.id.iv_search);
    iv_clear = v.findViewById(R.id.iv_clear);
    et_search = v.findViewById(R.id.et_search);

//    et_search.addTextChangedListener(new TextWatcher() {
//      @Override
//      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//      }
//
//      @Override
//      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//      }
//
//      @Override
//      public void afterTextChanged(Editable editable) {
//        if (editable != null && !TextUtils.isEmpty(editable.toString())) {
//          iv_clear.setVisibility(View.VISIBLE);
//        } else {
//          iv_clear.setVisibility(View.GONE);
//
//        }
//      }
//    });

    et_search.setOnEditorActionListener(new OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
            || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
            || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
            || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
          if (listener != null) {
            if (et_search.getText() != null) {
              listener.search(et_search.getText().toString());
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
            listener.search(et_search.getText().toString());
          }
        }
      }
    });

    et_search.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        if (listener != null) {
            listener.search("");
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

  public void setHint(int s) {
    et_search.setHint(s);
  }

  public void setOnClickListener(ISearchClickListener l) {
    listener = l;
  }


}
