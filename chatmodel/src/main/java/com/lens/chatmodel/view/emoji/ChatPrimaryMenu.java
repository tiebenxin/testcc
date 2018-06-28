package com.lens.chatmodel.view.emoji;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lens.chatmodel.R;
import com.lens.chatmodel.helper.SettingsManager;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.authority.AuthorityManager;
import java.util.Timer;


/**
 * 聊天输入栏主菜单栏
 */
public class ChatPrimaryMenu extends ChatPrimaryMenuBase implements OnClickListener {

    private static final int STOP_TYPING_DELAY = 3000;
    private EditText editText;
    private View buttonSetModeKeyboard;
    private RelativeLayout edittext_layout;
    private View buttonSetModeVoice;
    private View buttonSend;
    private View buttonPressToSpeak;
    private ImageView faceNormal;
    private ImageView faceChecked;
    private Button buttonMore;
    private RelativeLayout faceLayout;
    private Context context;
    //  private VoiceRecorderView voiceRecorderView;
    private String preStr;
    private Timer stopTypingTimer;

    //    private BaseEntity chat;
    private boolean skipOnTextChanges = false;
    private ImageView iv_secret;

    public ChatPrimaryMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public ChatPrimaryMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChatPrimaryMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(final Context context, AttributeSet attrs) {
        this.context = context;
//        if (context instanceof ActivityChat) {
//            chat = ((ActivityChat) context).getSelectedChat();
//        }
        LayoutInflater.from(context).inflate(R.layout.lens_widget_chat_primary_menu, this);
        editText = (EditText) findViewById(R.id.et_sendmessage);
        buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
        edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
        buttonSetModeVoice = findViewById(R.id.btn_set_mode_voice);
        buttonSend = findViewById(R.id.btn_send);
        buttonPressToSpeak = findViewById(R.id.btn_press_to_speak);
        faceNormal = (ImageView) findViewById(R.id.iv_face_normal);//正常的表情
        faceChecked = (ImageView) findViewById(R.id.iv_face_checked);//被选择的表情
        faceLayout = (RelativeLayout) findViewById(R.id.rl_face);//包裹表情的区域
        buttonMore = (Button) findViewById(R.id.btn_more);//更多按钮
        edittext_layout.setBackgroundResource(R.drawable.chat_edit_bg);
        iv_secret = findViewById(R.id.btn_secret);

        buttonSend.setOnClickListener(this);
        buttonSetModeKeyboard.setOnClickListener(this);
        buttonSetModeVoice.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        //faceLayout.setOnClickListener(this);
        editText.setOnClickListener(this);
        editText.clearFocus();
        //  editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, BaseApplication.getInt("字体大小",16));
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edittext_layout.setBackgroundResource(R.drawable.edittext_bg);
                } else {
                    edittext_layout.setBackgroundResource(R.drawable.chat_edit_bg);
                }
                if (listener != null) {
                    listener.onInputFocusChange(hasFocus);
                }

            }
        });
        editText.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!AuthorityManager.getInstance().copyOutside()) {
                    L.i("Authority", "不能拷贝到外面：" + AuthorityManager.getInstance().getCopyContent());
                    ClipboardManager clipboard = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(
                        ClipData
                            .newPlainText(null, AuthorityManager.getInstance().getCopyContent()));
                }
                return false;
            }
        });
        // 监听文字框
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
//                if (!skipOnTextChanges && stopTypingTimer != null) {
//                    stopTypingTimer.cancel();
//                }
                if (listener != null) {
                    listener.onEditTextInputting();
                }
                L.d("文字框变化:" + str);

                String text = s.toString().trim();
                if (!TextUtils.isEmpty(text)
                    && /*!text.equals(MessageManager.MSG_TYPE_SECRET) && */!text
                    .equals(preStr)) {
                    buttonMore.setVisibility(View.GONE);
                    buttonSend.setVisibility(View.VISIBLE);
                } else {
                    buttonMore.setVisibility(View.VISIBLE);
                    buttonSend.setVisibility(View.GONE);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if(s.length() == 0 || NoteStringUtils.isZhCn(s.toString().substring(s.length() - 1)) ){
//                    if(s.charAt(0) == '@'){
//                        //处产生一个回调
//                        if(listener != null){
//                            listener.onPrivateCall();
//                        }
//                  }
//
//                }
                //if(before == 0 && s.charAt(0) == '@'){
                //处产生一个回调

                // }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (!StringUtils.isEmpty(str) && str.charAt(str.length() - 1) == '@') {
                    //  L.d(String.valueOf(str.charAt(str.length() - 2)));
                    if (str.length() == 1 || !StringUtils
                        .isDigitOrLetter(str.substring(str.length() - 2, str.length() - 1))) {
                        if (listener != null) {
                            listener.onPrivateCall();
                        }
                    }

                } else if (!StringUtils.isEmpty(str) && str.charAt(0) == '*') {
                    if (str.length() == 1) {
                        if (listener != null) {
                            listener.onSecretCall();
                        }
                    }

                }

//                if (chat != null) {
//                    if (skipOnTextChanges) {
//                        return;
//                    }
//                }

            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (SettingsManager.chatsSendByEnter() && (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                    && KeyEvent.ACTION_DOWN == event.getAction()))) {
                    if (listener != null) {
                        String s = editText.getText().toString();
                        skipOnTextChanges = true;
                        editText.setText("");
                        skipOnTextChanges = false;
                        listener.onSendBtnClicked(s);
                    }
                    return true;
                }
                return false;
            }
        });

        //录音按钮
        buttonPressToSpeak.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (listener != null) {
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });
    }

    /**
     * 设置长按说话录制控件
     */
//  public void setPressToSpeakRecorderView(VoiceRecorderView voiceRecorderView) {
//    this.voiceRecorderView = voiceRecorderView;
//  }
    public void removeFocus() {
        editText.clearFocus();
    }

    public void setEditText(String s) {
        skipOnTextChanges = true;
        if (StringUtils.isEmpty(editText.getText().toString())) {
            editText.setText(s);
            editText.setSelection(s.length());
        } else if (!editText.getText().toString().contains(s)) {
            editText.append(s);
            editText.setSelection(editText.getText().length());
        }
        skipOnTextChanges = false;
    }
//    public void setEditText(CharSequence s, TextView.BufferType type){
//        editText.setText(s,type);
//    }

    public void setPreStr(String str) {
        preStr = str;
    }

    public String getText() {

        return editText.getText().toString();
    }

    /**
     * 表情输入
     */
    public void onEmojiconInputEvent(CharSequence emojiContent) {
        editText.append(emojiContent);
    }

    public void setEmojicon(CharSequence s) {
        skipOnTextChanges = true;
        String text;
        if (s instanceof SpannableString) {
            SpannableString spannable = (SpannableString) s;
            text = spannable.toString();
        } else {
            text = (String) s;
        }

        editText.setText(text);
        editText.setSelection(text.length());
        skipOnTextChanges = false;
    }

    /**
     * 表情删除
     */
    public void onEmojiconDeleteEvent() {
        skipOnTextChanges = true;
        if (!TextUtils.isEmpty(editText.getText())) {
            KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0,
                KeyEvent.KEYCODE_ENDCALL);
            editText.dispatchKeyEvent(event);
        }
        skipOnTextChanges = false;
    }

    @Override
    public void hideKeyboard() {
        TDevice.hideSoftKeyboard(editText);
    }

    @Override
    public void showSoftKeyboard() {
        TDevice.showSoftKeyboard(editText);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_send) {
            if (listener != null) {
                String s = editText.getText().toString();
                skipOnTextChanges = true;
                editText.setText("");
                skipOnTextChanges = false;
                listener.onSendBtnClicked(s);
            }
        } else if (id == R.id.btn_set_mode_voice) {
            setModeVoice();
            showNormalFaceImage();
            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        } else if (id == R.id.btn_set_mode_keyboard) {
            setModeKeyboard();
            showNormalFaceImage();
            if (listener != null) {
                listener.onToggleVoiceBtnClicked();
            }
        } else if (id == R.id.btn_more) {
            buttonSetModeVoice.setVisibility(View.VISIBLE);
            buttonSetModeKeyboard.setVisibility(View.GONE);
            edittext_layout.setVisibility(View.VISIBLE);
            buttonPressToSpeak.setVisibility(View.GONE);
            showNormalFaceImage();
        } else if (id == R.id.et_sendmessage) {
            edittext_layout.setBackgroundResource(R.drawable.edittext_bg);
            faceNormal.setVisibility(View.VISIBLE);
            faceChecked.setVisibility(View.INVISIBLE);
        } else if (id == R.id.rl_face) {
            toggleFaceImage();

        } else {
        }
    }


    /**
     * 显示语音图标按钮
     */
    protected void setModeVoice() {
        hideKeyboard();
        edittext_layout.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.GONE);
        buttonSetModeKeyboard.setVisibility(View.VISIBLE);
        buttonSend.setVisibility(View.GONE);
        buttonMore.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.VISIBLE);
        faceLayout.setVisibility(GONE);
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);

    }

    /**
     * 显示键盘图标
     */
    protected void setModeKeyboard() {
        edittext_layout.setVisibility(View.VISIBLE);
        buttonSetModeKeyboard.setVisibility(View.GONE);
        buttonSetModeVoice.setVisibility(View.VISIBLE);
        // mEditTextContent.setVisibility(View.VISIBLE);
        editText.requestFocus();
        faceLayout.setVisibility(VISIBLE);
        //buttonSend.setVisibility(View.VISIBLE);
        buttonPressToSpeak.setVisibility(View.GONE);
        if (TextUtils.isEmpty(editText.getText())) {
            buttonMore.setVisibility(View.VISIBLE);
            buttonSend.setVisibility(View.GONE);
        } else {
            buttonMore.setVisibility(View.GONE);
            buttonSend.setVisibility(View.VISIBLE);
        }

    }

    public void setSecretChat(boolean isSecret) {
        iv_secret.setVisibility(isSecret ? VISIBLE : GONE);
    }


    protected void toggleFaceImage() {
        if (faceNormal.getVisibility() == View.VISIBLE) {
            showSelectedFaceImage();
        } else {
            showNormalFaceImage();
        }
    }

    private void showNormalFaceImage() {
        faceNormal.setVisibility(View.VISIBLE);
        faceChecked.setVisibility(View.INVISIBLE);
    }

    private void showSelectedFaceImage() {
        faceNormal.setVisibility(View.INVISIBLE);
        faceChecked.setVisibility(View.VISIBLE);
    }


    @Override
    public void onExtendMenuContainerHide() {
        showNormalFaceImage();
    }

}
