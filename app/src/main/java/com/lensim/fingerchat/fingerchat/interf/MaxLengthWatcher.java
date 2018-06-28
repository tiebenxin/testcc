package com.lensim.fingerchat.fingerchat.interf;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.T;

/**
 * Created by LL130386 on 2017/8/21.
 */

public class MaxLengthWatcher implements TextWatcher {

    private int maxLen = 0;
    private EditText editText = null;
    private TextView textView = null;


    public MaxLengthWatcher(int maxLen, EditText editText, TextView textView) {
        this.maxLen = maxLen;
        this.editText = editText;
        if (null != textView) {
            this.textView = textView;
        }
    }

    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub

    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
        int arg3) {
        // TODO Auto-generated method stub

    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
        Editable editable = editText.getText();
        int len = editable.length();
        if (null != textView) {
            textView.setText(String.valueOf(len) + "/" + String.valueOf(maxLen));
        }

        if (len > maxLen) {
            int selEndIndex = Selection.getSelectionEnd(editable);
            String str = editable.toString();
            //截取新字符串
            String newStr = str.substring(0, maxLen);
            editText.setText(newStr);
            editable = editText.getText();

            //新字符串的长度
            int newLen = editable.length();
            //旧光标位置超过字符串长度
            if (selEndIndex > newLen) {
                selEndIndex = editable.length();
            }
            //设置新光标所在的位置
            Selection.setSelection(editable, selEndIndex);
            T.show(ContextHelper.getContext(), String.format("不能超过%s个字", maxLen),
                Toast.LENGTH_SHORT);

        }
    }

}
