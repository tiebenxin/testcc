package com.lens.chatmodel.view.friendcircle;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;


import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.DefaultEmojiconDatas;
import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.EmojiconGroupEntity;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.emoji.ChatPrimaryMenuBase;
import com.lens.chatmodel.view.emoji.EmojiconMenu;
import com.lens.chatmodel.view.emoji.EmojiconMenuBase;
import com.lens.chatmodel.view.emoji.EmotionKeyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 朋友圈页面底部的聊天输入菜单栏
 * 主要包含2个控件:ChatPrimaryMenu(主菜单栏，包含文字输入、发送等功能)
 * 以及EmojiconMenu(表情栏)
 */
public class CircleInputMenu extends LinearLayout {
    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected CirclePrimaryMenu circlePrimaryMenu;
    protected EmojiconMenuBase emojiconMenu;
   // protected ChatExtendMenu chatExtendMenu;
   // protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;

    private Handler handler = new Handler();
    private CircleInputMenuListener listener;
    private Context context;
    private boolean inited;

    public CircleInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public CircleInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.lens_widget_circle_input_menu, this);
        //主菜单
        primaryMenuContainer = (FrameLayout) findViewById(R.id.circle_primary_menu_container);
        //表情容器
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.circle_emoji_menu_container);
//        ViewGroup.LayoutParams layoutParams = emojiconMenuContainer.getLayoutParams();
//        layoutParams.height = BaseApplication.getInt(EmotionKeyboard.SHARE_PREFERENCE_SOFT_INPUT_HEIGHT,786);
//        emojiconMenuContainer.setLayoutParams(layoutParams);

    }

    /**
     * init view 此方法需放在registerExtendMenuItem后面及setCustomEmojiconMenu，
     * setCustomPrimaryMenu(如果需要自定义这两个menu)后面
     * 如果不执行初始化，将没有表情以及拓展栏位
     * @param emojiconGroupList 表情组类别，传null使用easeui默认的表情
     */
    public void init(EmotionKeyboard manager, List<EmojiconGroupEntity> emojiconGroupList) {
        if(inited){
            return;
        }
        // 主按钮菜单栏,没有自定义的用默认的
        if(circlePrimaryMenu == null){
            circlePrimaryMenu = (CirclePrimaryMenu) layoutInflater.inflate(R.layout.lens_layout_circle_primary_menu, null);
        }
        primaryMenuContainer.addView(circlePrimaryMenu);

        // 表情栏，没有自定义的用默认的
        if(emojiconMenu == null){
            emojiconMenu = (EmojiconMenu) layoutInflater.inflate(R.layout.lens_layout_emojicon_menu, null);
            if(emojiconGroupList == null){
                emojiconGroupList = new ArrayList<EmojiconGroupEntity>();
                emojiconGroupList.add(new EmojiconGroupEntity(R.drawable.ee_1,  Arrays.asList(DefaultEmojiconDatas.getData())));
                //emojiconGroupList.add(EmojiconDefaultGroupData.getData());
            }
            ((EmojiconMenu)emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);
//        if(emojiconMenu.getVisibility() == View.VISIBLE){
//            emojiconMenu.setVisibility(View.GONE);
//        }
        processChatMenu();

        manager.setEmotionView(emojiconMenuContainer)
                .bindToEditText((EditText) circlePrimaryMenu.findViewById(R.id.et_sendmessage))
                .bindToEmotionButton(circlePrimaryMenu.findViewById(R.id.rl_face))
                .build();

        inited = true;
    }

    //public void init(){
//        init(null);
//    }

    /**
     * 设置自定义的表情栏，该控件需要继承自EaseEmojiconMenuBase，
     * 以及回调你想要回调出去的事件给设置的EaseEmojiconMenuListener
     * @param customEmojiconMenu
     */
    public void setCustomEmojiconMenu(EmojiconMenuBase customEmojiconMenu){
        this.emojiconMenu = customEmojiconMenu;
    }


    public boolean emojiconContainerShowed(){
        return emojiconMenuContainer.getVisibility() == VISIBLE;
    }
    public ChatPrimaryMenuBase getPrimaryMenu(){
        return circlePrimaryMenu;
    }

    public EmojiconMenuBase getEmojiconMenu(){
        return emojiconMenu;
    }


    protected void processChatMenu() {
        // 主编辑栏的事件
        circlePrimaryMenu.setChatPrimaryMenuListener(new ChatPrimaryMenuBase.EaseChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null)
                    listener.onSendMessage(content);
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();//隐藏拓展栏
            }

            @Override
            public void onToggleExtendClicked() {
               //更多按钮的显示与隐藏
            }

            @Override
            public void onToggleEmojiconClicked() {
                toggleEmojicon();//表情的显示与隐藏
            }

            @Override
            public void onEditTextClicked() {
                hideExtendMenuContainer();//隐藏拓展栏
            }

            @Override
            public void onPrivateCall() {

            }

            @Override
            public void onSecretCall() {

            }

            @Override
            public void onInputFocusChange(boolean hasfocus) {

            }

            @Override
            public void onEditTextInputting() {

            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {

                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EmojiconMenuBase.EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(Emojicon emojicon) {
                if(emojicon.getType() != Emojicon.Type.BIG_EXPRESSION){
                    if(emojicon.getEmojiText() != null){
                        circlePrimaryMenu.onEmojiconInputEvent(SmileUtils.getSmiledText(context,emojicon.getEmojiText(),0));
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                circlePrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }



    /**
     * 显示或隐藏表情页
     */
    protected void toggleEmojicon() {


            if (emojiconMenuContainer.isShown()) {
                emojiconMenuContainer.setVisibility(View.GONE);
            } else {
                hideKeyboard();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        emojiconMenuContainer.setVisibility(View.VISIBLE);
                    }
                }, 50);

            }
    }





    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        circlePrimaryMenu.hideKeyboard();
    }
    public void showKeyboard(){
        circlePrimaryMenu.showSoftKeyboard();
    }
    /**
     * 隐藏整个扩展按钮栏(包括表情栏)
     */
    public void hideExtendMenuContainer() {

        emojiconMenuContainer.setVisibility(View.GONE);
        circlePrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * 系统返回键被按时调用此方法
     * 
     * @return 返回false表示返回键时扩展菜单栏时打开状态，true则表示按返回键时扩展栏是关闭状态<br/>
     *         如果返回时打开状态状态，会先关闭扩展栏再返回值
     */
    public boolean onBackPressed() {
        if (emojiconMenu.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }
    

    public void setChatInputMenuListener(CircleInputMenuListener listener) {
        this.listener = listener;

    }

    public void setCirclePrimaryMenuHint(String hint){
        circlePrimaryMenu.setEditTextHint(hint);
    }

    public interface CircleInputMenuListener {
        /**
         * 发送消息按钮点击
         * 
         * @param content
         *            文本内容
         */
        void onSendMessage(String content);


    }

}
