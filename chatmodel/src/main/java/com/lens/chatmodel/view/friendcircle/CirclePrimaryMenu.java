package com.lens.chatmodel.view.friendcircle;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lens.chatmodel.R;
import com.lens.chatmodel.view.emoji.ChatPrimaryMenuBase;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import com.lensim.fingerchat.commons.utils.TDevice;


/**
 * 朋友圈评论输入栏主菜单栏
 *
 */
public class CirclePrimaryMenu extends ChatPrimaryMenuBase implements OnClickListener {
    private EditText editText;
    private RelativeLayout edittext_layout;
    private View buttonSend;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private RelativeLayout faceLayout;
    private Context context;

    public CirclePrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public CirclePrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.lens_widget_circle_primary_menu, this);
        editText = (EditText) findViewById(R.id.et_sendmessage);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSend = findViewById(R.id.btn_send);

        faceNormal = (ImageView) findViewById(R.id.iv_face_normal);//正常的表情
        faceChecked = (ImageView) findViewById(R.id.iv_face_checked);//被选择的表情
        faceLayout = (RelativeLayout) findViewById(R.id.rl_face);//包裹表情的区域

        edittext_layout.setBackgroundResource(R.drawable.chat_edit_bg);
        
        buttonSend.setOnClickListener(this);
        faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
        editText.requestFocus();
        
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.edittext_bg);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.chat_edit_bg);
                }

            }
        });

        editText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!AuthorityManager.getInstance().copyOutside()){
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText(null,AuthorityManager.getInstance().getCopyContent()));
                }
                return false;
            }
        });
    }
    


    /**
     * 表情输入
     * @param emojiContent
     */
    public void onEmojiconInputEvent(CharSequence emojiContent){
        editText.append(emojiContent);
    }
    
    /**
     * 表情删除
     */
    public void onEmojiconDeleteEvent(){
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
    }


    public void setEditTextHint(String hint){
        editText.setHint(hint);
        editText.requestFocus();
       // TDevice.toogleSoftKeyboard(editText);
    }


    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onClick(View view){
        int id = view.getId();
        if (id == R.id.btn_send) {
            if(listener != null){
                String s = editText.getText().toString();
                editText.setText("");
                listener.onSendBtnClicked(s);
            }
        } else if (id == R.id.et_sendmessage) {
            edittext_layout.setBackgroundResource(R.drawable.edittext_bg);
            faceNormal.setVisibility(View.VISIBLE);
            faceChecked.setVisibility(View.INVISIBLE);
//            if(listener != null)
//                listener.onEditTextClicked();
        } else if (id == R.id.rl_face) {
          //  hideKeyboard();
            toggleFaceImage();
//            if(listener != null){
//                listener.onToggleEmojiconClicked();
//            }
        } else {
        }
    }

    @Override
    public void hideKeyboard() {
        TDevice.hideSoftKeyboard(editText);
    }

    protected void toggleFaceImage(){
        if(faceNormal.getVisibility() == View.VISIBLE){
            showSelectedFaceImage();
        }else{
            showNormalFaceImage();
        }
    }
    
    private void showNormalFaceImage(){
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }
    
    private void showSelectedFaceImage(){
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }
    


    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

    @Override
    public void setSecretChat(boolean isSecret) {
        //不需设置
    }

}
