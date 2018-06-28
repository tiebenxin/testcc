package com.lens.chatmodel.view.photoedit;

import android.graphics.Rect;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.popupwindow.BasePopupWindow;

/**
 * Created by LY309313 on 2017/4/7.
 */

public class InputTextWindow extends BasePopupWindow {

    private Rect rect;
    private ScrollView sc;
    private LinearLayout main;
    private FrameLayout colorviewContainer;
    private ColorPickView colorPickView;
    private TextView btCancel;
    private TextView btconfrim;
    private EditText etContent;
    private OnConfrimListener onConfrimListener;
    public InputTextWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override
    public void initView() {
        main = (LinearLayout) findViewById(R.id.pop_input_main);
        sc = (ScrollView) findViewById(R.id.pop_input_sc);
        colorviewContainer = (FrameLayout) findViewById(R.id.colorviewContainer);
        btCancel = (TextView) findViewById(R.id.pop_cancel);
        btconfrim = (TextView) findViewById(R.id.pop_confrim);
        etContent = (EditText) findViewById(R.id.pop_et);
        colorPickView = (ColorPickView) findViewById(R.id.colorview);
        rect = new Rect();
        main.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                main.getLocalVisibleRect(rect);
                L.i("bottom:" + bottom);
                L.i("oldbottom:" + oldBottom);
                L.i("left:" + rect.left + "top:" + rect.top + "right:" + rect.right + "bottom:" + rect.bottom);
                if((bottom - rect.bottom) > 300){
                    //键盘弹起
                    colorviewContainer.layout(left,rect.bottom - (int) TDevice.dpToPixel(56),right,rect.bottom);
                }else{
                    colorviewContainer.layout(left,bottom - (int)TDevice.dpToPixel(56),right,bottom);
                }
            }
        });
    }

    @Override
    public void initData() {
        int currColor = colorPickView.getCurrColor();
        etContent.setTextColor(currColor);
        UIHelper.setCursorColor(etContent,currColor);
    }

    @Override
    public void initListener() {
        btCancel.setOnClickListener(this);
        btconfrim.setOnClickListener(this);
        colorPickView.setOnColorPickListenr(new ColorPickView.OnColorPickListenr() {
            @Override
            public void onColorPick(int color) {
                etContent.setTextColor(color);
                UIHelper.setCursorColor(etContent,color);
            }
        });
    }

    @Override
    public void processClick(View v) {
        int i = v.getId();
        if (i == R.id.pop_cancel) {
            dismiss();

        } else if (i == R.id.pop_confrim) {
            String s = etContent.getText().toString();
            if (!StringUtils.isEmpty(s) && onConfrimListener != null) {
                onConfrimListener.onConfrim(s, colorPickView.getCurrColor());
            }
            dismiss();

        }
    }

    public void setOnConfrimListener(OnConfrimListener onConfrimListener) {
        this.onConfrimListener = onConfrimListener;
    }

    public interface OnConfrimListener{
        void onConfrim(String text, int color);
    }
}
