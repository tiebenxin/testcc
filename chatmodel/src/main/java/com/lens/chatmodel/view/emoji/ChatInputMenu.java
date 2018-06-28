package com.lens.chatmodel.view.emoji;

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
import com.lens.chatmodel.bean.EmojiconDefaultGroupData;
import com.lens.chatmodel.bean.EmojiconGroupEntity;
import com.lens.chatmodel.view.emoji.ChatExtendMenu.EaseChatExtendMenuItemClickListener;
import com.lens.chatmodel.view.emoji.ChatPrimaryMenuBase.EaseChatPrimaryMenuListener;
import com.lens.chatmodel.view.emoji.EmojiconMenuBase.EaseEmojiconMenuListener;
import com.lensim.fingerchat.commons.utils.L;
import com.lens.chatmodel.utils.SmileUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天页面底部的聊天输入菜单栏 <br/>
 * 主要包含3个控件:EaseChatPrimaryMenu(主菜单栏，包含文字输入、发送等功能), <br/>
 * EaseChatExtendMenu(扩展栏，点击加号按钮出来的小宫格的菜单栏), <br/>
 * 以及EaseEmojiconMenu(表情栏)
 */
public class ChatInputMenu extends LinearLayout {

    FrameLayout primaryMenuContainer, emojiconMenuContainer;
    protected ChatPrimaryMenuBase chatPrimaryMenu;
    protected EmojiconMenuBase emojiconMenu;
    protected ChatExtendMenu chatExtendMenu;
    protected FrameLayout chatExtendMenuContainer;
    protected LayoutInflater layoutInflater;

    private Handler handler = new Handler();
    private ChatInputMenuListener listener;
    private Context context;
    private boolean inited;

    public ChatInputMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public ChatInputMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ChatInputMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.lens_widget_chat_input_menu, this);
        //主菜单
        primaryMenuContainer = (FrameLayout) findViewById(R.id.primary_menu_container);
        //表情容器
        emojiconMenuContainer = (FrameLayout) findViewById(R.id.emojicon_menu_container);
        //表情+拓展菜单栏(总容器)
        chatExtendMenuContainer = (FrameLayout) findViewById(R.id.extend_menu_container);
        // 扩展栏容器
        chatExtendMenu = (ChatExtendMenu) findViewById(R.id.extend_menu);


    }

    /**
     * init view 此方法需放在registerExtendMenuItem后面及setCustomEmojiconMenu，
     * setCustomPrimaryMenu(如果需要自定义这两个menu)后面
     * 如果不执行初始化，将没有表情以及拓展栏位
     *
     * @param emojiconGroupList 表情组类别，传null使用easeui默认的表情
     */
    public void init(EmotionKeyboard manager, List<EmojiconGroupEntity> emojiconGroupList) {
        if (inited) {
            return;
        }
        // 主按钮菜单栏,没有自定义的用默认的
        if (chatPrimaryMenu == null) {
            chatPrimaryMenu = (ChatPrimaryMenu) layoutInflater
                .inflate(R.layout.lens_layout_chat_primary_menu, null);
        }
        primaryMenuContainer.addView(chatPrimaryMenu);

        // 表情栏，没有自定义的用默认的
        if (emojiconMenu == null) {
            emojiconMenu = (EmojiconMenu) layoutInflater
                .inflate(R.layout.lens_layout_emojicon_menu, null);
            if (emojiconGroupList == null) {
                emojiconGroupList = new ArrayList<EmojiconGroupEntity>();
                emojiconGroupList.add(new EmojiconGroupEntity(R.drawable.ee_1, Arrays.asList(
                    DefaultEmojiconDatas.getData())));
                emojiconGroupList.add(EmojiconDefaultGroupData.getData());
            }
            ((EmojiconMenu) emojiconMenu).init(emojiconGroupList);
        }
        emojiconMenuContainer.addView(emojiconMenu);

        processChatMenu();
        // 初始化extendmenu
        chatExtendMenu.init();
        manager.setEmotionView(chatExtendMenuContainer)
            .bindToEditText((EditText) chatPrimaryMenu.findViewById(R.id.et_sendmessage))
            .bindToEmotionButton(chatPrimaryMenu.findViewById(R.id.rl_face))
            .bindEmotionContainer(emojiconMenuContainer)
            .bindExternContainer(chatExtendMenu)
            .bindExternButton(findViewById(R.id.btn_more))
            .build();

        inited = true;
    }

//    public void init(){
//        init(null,null);
//    }

    /**
     * 添加表情组
     */
    public void addEmojiconGroup(List<EmojiconGroupEntity> groupEntitys) {
        ((EmojiconMenu) emojiconMenu).reset(groupEntitys);
    }

    /**
     * 移除表情组
     */
    public void removeEmojiconGroup(int position) {
        ((EmojiconMenu) emojiconMenu).removeEmojiconGroup(position);
    }


    /**
     * 设置自定义的表情栏，该控件需要继承自EaseEmojiconMenuBase，
     * 以及回调你想要回调出去的事件给设置的EaseEmojiconMenuListener
     */
    public void setCustomEmojiconMenu(EmojiconMenuBase customEmojiconMenu) {
        this.emojiconMenu = customEmojiconMenu;
    }

    public void setEditText(String s) {
        ((ChatPrimaryMenu) chatPrimaryMenu).setEditText(s);
    }

    public void setPreStr(String s) {
        ((ChatPrimaryMenu) chatPrimaryMenu).setPreStr(s);
    }

    public void setEmojicon(CharSequence s) {
        ((ChatPrimaryMenu) chatPrimaryMenu).setEmojicon(s);
    }

    public String getText() {
        return ((ChatPrimaryMenu) chatPrimaryMenu).getText();
    }

    /**
     * 设置自定义的主菜单栏，该控件需要继承自EaseChatPrimaryMenuBase，
     * 以及回调你想要回调出去的事件给设置的EaseEmojiconMenuListener
     */
    public void setCustomPrimaryMenu(ChatPrimaryMenuBase customPrimaryMenu) {
        this.chatPrimaryMenu = customPrimaryMenu;
    }

    public ChatPrimaryMenuBase getPrimaryMenu() {
        return chatPrimaryMenu;
    }

    public ChatExtendMenu getExtendMenu() {
        return chatExtendMenu;
    }

    public EmojiconMenuBase getEmojiconMenu() {
        return emojiconMenu;
    }


    /**
     * 注册扩展菜单的item
     *
     * @param name item名字
     * @param drawableRes item背景
     * @param itemId id
     * @param listener item点击事件
     */
    public void registerExtendMenuItem(String name, int drawableRes, int itemId,
        EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(name, drawableRes, itemId, listener);
    }

    /**
     * 注册扩展菜单的item
     *
     * @param nameRes item名字
     * @param drawableRes item背景
     * @param itemId id
     * @param listener item点击事件
     */
    public void registerExtendMenuItem(int nameRes, int drawableRes, int itemId,
        EaseChatExtendMenuItemClickListener listener) {
        chatExtendMenu.registerMenuItem(nameRes, drawableRes, itemId, listener);
    }


    protected void processChatMenu() {
        // 主编辑栏的事件
        chatPrimaryMenu.setChatPrimaryMenuListener(new EaseChatPrimaryMenuListener() {

            @Override
            public void onSendBtnClicked(String content) {
                if (listener != null) {
                    listener.onSendMessage(content);
                }
            }

            @Override
            public void onToggleVoiceBtnClicked() {
                hideExtendMenuContainer();//隐藏拓展栏
            }

            @Override
            public void onToggleExtendClicked() {
                toggleMore();//更多按钮的显示与隐藏
            }

            @Override
            public void onToggleEmojiconClicked() {
                //   toggleEmojicon();//表情的显示与隐藏
            }

            @Override
            public void onEditTextClicked() {
                // hideExtendMenuContainer();//隐藏拓展栏
            }

            @Override
            public void onPrivateCall() {
                if (listener != null) {
                    listener.onPrivateCall();
                }
            }

            @Override
            public void onSecretCall() {
                if (listener != null) {
                    listener.onSecretCall();
                }
            }

            @Override
            public void onInputFocusChange(boolean hasfocus) {
                if (listener != null) {
                    listener.onFocusChange(hasfocus);
                }
            }

            @Override
            public void onEditTextInputting() {
                if (listener != null){
                    listener.onEditTextInputting();
                }
            }


            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                if (listener != null) {
                    return listener.onPressToSpeakBtnTouch(v, event);
                }
                return false;
            }
        });

        // emojicon menu
        emojiconMenu.setEmojiconMenuListener(new EaseEmojiconMenuListener() {

            @Override
            public void onExpressionClicked(Emojicon emojicon) {
                if (emojicon.getType() != Emojicon.Type.BIG_EXPRESSION) {
                    if (emojicon.getEmojiText() != null) {
                        chatPrimaryMenu.onEmojiconInputEvent(
                            SmileUtils.getSmiledText(context, emojicon.getEmojiText(),
                                TDevice.sp2px(24)));
                    }
                } else {
                    if (listener != null) {
                        listener.onBigExpressionClicked(emojicon);
                    }
                }
            }

            @Override
            public void onDeleteImageClicked() {
                chatPrimaryMenu.onEmojiconDeleteEvent();
            }
        });

    }

    /**
     * 显示或隐藏图标按钮页
     */
    public void toggleMore() {
        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.VISIBLE);
                    emojiconMenu.setVisibility(View.GONE);
                }
            }, 50);
        } else {
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                emojiconMenu.setVisibility(View.GONE);
                chatExtendMenu.setVisibility(View.VISIBLE);
            } else {
                chatExtendMenuContainer.setVisibility(View.GONE);
            }

        }

    }

    /**
     * 显示或隐藏表情页
     */
    protected void toggleEmojicon() {

        if (chatExtendMenuContainer.getVisibility() == View.GONE) { //总容器隐藏的情况
            hideKeyboard();
            handler.postDelayed(new Runnable() {
                public void run() {
                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
                    chatExtendMenu.setVisibility(View.GONE);
                    emojiconMenu.setVisibility(View.VISIBLE);
                }
            }, 50);
//            chatExtendMenuContainer.setVisibility(VISIBLE);
//            chatExtendMenu.setVisibility(View.GONE);
//            emojiconMenu.setVisibility(View.VISIBLE);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    hideKeyboard();
//                }
//            },100);
        } else { //如果总容器是可见的
            if (emojiconMenu.getVisibility() == View.VISIBLE) {
                L.i("弹出键盘");
                showKeyboard();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        chatExtendMenu.setVisibility(View.GONE);
                        emojiconMenu.setVisibility(View.GONE);
                    }
                }, 100);
            } else {
                L.i("隐藏键盘");
                chatExtendMenu.setVisibility(View.GONE);
                emojiconMenu.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideKeyboard();
                    }
                }, 100);
            }
        }

//        if (chatExtendMenuContainer.getVisibility() == View.GONE) {
//            hideKeyboard();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    chatExtendMenuContainer.setVisibility(View.VISIBLE);
//                    chatExtendMenu.setVisibility(View.GONE);
//                    emojiconMenu.setVisibility(View.VISIBLE);
//                }
//            }, 50);
//        } else {
//            showKeyboard();
//            if (emojiconMenu.getVisibility() == View.VISIBLE) {
//                chatExtendMenuContainer.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.GONE);
//            } else {
//                chatExtendMenu.setVisibility(View.GONE);
//                emojiconMenu.setVisibility(View.VISIBLE);
//            }
//
//        }
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        ((ChatPrimaryMenu) chatPrimaryMenu).hideKeyboard();

    }

    private void showKeyboard() {
        ((ChatPrimaryMenu) chatPrimaryMenu).showSoftKeyboard();
    }

    /**
     * 隐藏整个扩展按钮栏(包括表情栏)
     */
    public void hideExtendMenuContainer() {
        // chatExtendMenu.setVisibility(View.GONE);
        // emojiconMenu.setVisibility(View.GONE);
        chatExtendMenuContainer.setVisibility(View.GONE);
        chatPrimaryMenu.onExtendMenuContainerHide();
    }

    /**
     * 系统返回键被按时调用此方法
     *
     * @return 返回false表示返回键时扩展菜单栏时打开状态，true则表示按返回键时扩展栏是关闭状态<br/> 如果返回时打开状态状态，会先关闭扩展栏再返回值
     */
    public boolean onBackPressed() {
        if (chatExtendMenuContainer.getVisibility() == View.VISIBLE) {
            hideExtendMenuContainer();
            return false;
        } else {
            return true;
        }

    }

    public void setSecretChat(boolean isSecret) {
        chatPrimaryMenu.setSecretChat(isSecret);
    }


    public void setChatInputMenuListener(ChatInputMenuListener listener) {
        this.listener = listener;
    }

    public void removeFocus() {
        ((ChatPrimaryMenu) chatPrimaryMenu).removeFocus();
    }

    public interface ChatInputMenuListener {

        /**
         * 发送消息按钮点击
         *
         * @param content 文本内容
         */
        void onSendMessage(String content);

        /**
         * 大表情被点击
         */
        void onBigExpressionClicked(Emojicon emojicon);

        /**
         * 长按说话按钮touch事件
         */
        boolean onPressToSpeakBtnTouch(View v, MotionEvent event);

        void onPrivateCall();

        void onSecretCall();

        void onFocusChange(boolean hasFocus);

        void onEditTextInputting();
    }

}
