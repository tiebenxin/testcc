package com.lensim.fingerchat.fingerchat.ui.settings;

import android.graphics.Color;
import android.widget.TextView;

import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.MainActivity;

/**
 * Created by LY305512 on 2017/12/26.
 */

public class AdjustTextSizeActivity extends BaseActivity {

    private FontAdjustView fontAdjustView;
    private SettingColorPickView sendColorPickView;
    private ReceivedColorPickView receivedColorPickView;
    private TextView tv_msg_send;
    private TextView tv_msg_recevied;
    private TextView tv_remind;
    private FGToolbar toolbar;
    private boolean hasChange;

    @Override
    public void initView() {
        setContentView(R.layout.activity_adjust_textsize);
        toolbar = findViewById(R.id.viewTitleBar);
        initToolBar();
        fontAdjustView = findViewById(R.id.fontadjustview);
        sendColorPickView = findViewById(R.id.colorpickview);
        receivedColorPickView = findViewById(R.id.receivedcolorpickview);
        tv_msg_send = findViewById(R.id.tv_msg);
        tv_msg_recevied = findViewById(R.id.tv_received_msg);
        tv_remind = findViewById(R.id.tv_remind);
        int factor = SPHelper.getInt("font_size", 1);
        int size = SPHelper.getInt("font_size", 1) * 2 + 12;
        int colorSend = SPHelper.getInt("font_send_color", Color.WHITE);
        int colorReceive = SPHelper.getInt("font_receive_color", Color.BLACK);
        tv_msg_send.setText("预览文字大小");
        tv_msg_send.setTextSize(size);
        tv_msg_send.setTextColor(colorSend);
        tv_msg_recevied.setTextSize(size);
        tv_remind.setTextSize(size);
        tv_msg_recevied.setTextColor(colorReceive);
        tv_remind.setTextColor(colorReceive);
        fontAdjustView.setPosition(factor);
        sendColorPickView.setSelectedColor(colorSend);
        receivedColorPickView.setSelectedColor(colorReceive);
        setListener();
    }

    private void initToolBar() {
        initBackButton(toolbar, true);
        toolbar.setTitleText("字体设置");
    }

    @Override
    public void backPressed() {
        if (hasChange) {
            MainActivity.start(this, MainActivity.SETTING);
        } else {
            finish();
        }
    }

    private void setListener() {
        fontAdjustView.setOnFontSelectListener(new FontAdjustView.OnFontSelectListener() {
            @Override
            public void onFontSelect(int pos, int size) {
                SPHelper.saveValue("font_size", pos);
                tv_msg_send.setTextSize(14 + pos * 2);
                tv_msg_recevied.setTextSize(14 + pos * 2);
                tv_remind.setTextSize(14 + pos * 2);
                hasChange = true;
            }
        });
        sendColorPickView.setOnColorPickListenr(new SettingColorPickView.OnColorPickListenr() {
            @Override
            public void onColorPick(int color) {
                SPHelper.saveValue("font_send_color", color);
                tv_msg_send.setTextColor(color);
                hasChange = true;


            }
        });
        receivedColorPickView
            .setOnReceivedColorPickListenr(new ReceivedColorPickView.OnReceivedColorPickListenr() {
                @Override
                public void onReceivedColorPick(int color) {
                    SPHelper.saveValue("font_receive_color", color);
                    tv_msg_recevied.setTextColor(color);
                    tv_remind.setTextColor(color);
                    hasChange = true;

                }
            });
    }

}
