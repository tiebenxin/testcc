package com.lensim.fingerchat.fingerchat.ui.settings;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.commons.base.BaseActivity;


/**
 * Created by LY305512 on 2017/12/26.
 */

public class GeneralActivity extends BaseActivity {
    private TextView tv_adjust_textsize;
    private TextView tv_select_voice;
    public static final int CODE_NOTIFICATION = 1;
    private String myUriStr;      //已选择的铃声
    @Override
    public void initView() {
        setContentView(R.layout.activity_general);
        tv_adjust_textsize = findViewById(R.id.tv_adjust_textsize);
        tv_select_voice = findViewById(R.id.tv_select_voice);
        setListener();
    }

    private void setListener() {
        tv_adjust_textsize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(),AdjustTextSizeActivity.class));
            }
        });

        tv_select_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doPickRingtone();
            }
        });
    }
    //弹出选择抖动铃声
    private void doPickRingtone() {
        // 打开系统铃声设置 
        Intent intent =new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        //设置铃声类型和title
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,"选择新消息提示音");
        myUriStr = SpUtil.getString(getApplicationContext(),ContantValue.myUriStr,null);
        if (myUriStr!=null){
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(myUriStr));
        }
        //设置完成返回到当前activity
        startActivityForResult(intent,CODE_NOTIFICATION);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }
        // 得到我们选择的铃声 
        Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        if (pickedUri != null){
            // 将我们选择的铃声设置成为默认通知铃声
            RingtoneManager.setActualDefaultRingtoneUri(this,RingtoneManager.TYPE_RINGTONE,pickedUri);
            myUriStr = pickedUri.toString();
            SpUtil.putString(getApplicationContext(),ContantValue.myUriStr,myUriStr);
        }
    }
}
