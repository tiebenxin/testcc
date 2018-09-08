package com.lens.chatmodel.bean;


import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.Emojicon.Type;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;


public class EmojiconDefaultGroupData {

    private static int[] bigIcons = new int[]{
        R.drawable.ex_0,
        R.drawable.ex_1,
        R.drawable.ex_2,
        R.drawable.ex_3,
        R.drawable.ex_12,
        R.drawable.ex_13,
        R.drawable.ex_14,
        R.drawable.ex_15,
        R.drawable.ex_4,
        R.drawable.ex_5,
        R.drawable.ex_6,
        R.drawable.ex_7,
        R.drawable.ex_16,
        R.drawable.ex_17,
        R.drawable.ex_18,
        R.drawable.ex_19,
        R.drawable.ex_8,
        R.drawable.ex_9,
        R.drawable.ex_10,
        R.drawable.ex_11,
        R.drawable.ex_20,
        R.drawable.ex_21,
        R.drawable.ex_22,
        R.drawable.ex_23,
    };

//    private static int[] icons = new int[]{
//        R.drawable.icon_0,
//        R.drawable.icon_1,
//        R.drawable.icon_2,
//        R.drawable.icon_3,
//        R.drawable.icon_12,
//        R.drawable.icon_13,
//        R.drawable.icon_14,
//        R.drawable.icon_15,
//
//        R.drawable.icon_04,
//        R.drawable.icon_05,
//        R.drawable.icon_06,
//        R.drawable.icon_07,
//        R.drawable.icon_16,
//        R.drawable.icon_17,
//        R.drawable.icon_18,
//        R.drawable.icon_19,
//
//        R.drawable.icon_8,
//        R.drawable.icon_9,
//        R.drawable.icon_10,
//        R.drawable.icon_11,
//        R.drawable.icon_20,
//        R.drawable.icon_21,
//        R.drawable.icon_22,
//        R.drawable.icon_23,
//    };

    private static String[] names = {
        "[HELP]",
        "[HI]",
        "[NO]",
        "[OK]",

        "[别走]",
        "[加班]",
        "[卖萌]",
        "[压力山大]",

        "[流泪]",
        "[很爽]",
        "[得意]",
        "[得瑟]",

        "[泪别]",
        "[高兴]",
        "[睡觉]",
        "[爱你]",

        "[憨笑]",
        "[暴走]",
        "[可爱]",
        "[谢谢]",

        "[对不起]",
        "[泪流成河]",
        "[加油]",
        "[不]",
    };
    private static final EmojiconGroupEntity DATA = createData();

    private static EmojiconGroupEntity createData() {
        EmojiconGroupEntity emojiconGroupEntity = new EmojiconGroupEntity();
        Emojicon[] datas = new Emojicon[bigIcons.length];
        for (int i = 0; i < bigIcons.length; i++) {
            datas[i] = new Emojicon(bigIcons[i], null, Type.BIG_EXPRESSION);
            datas[i].setBigIcon(bigIcons[i]);
            datas[i].setName(names[i].replace("[", "").replace("]", ""));
            datas[i].setIdentityCode(i + ".gif");
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.gif);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }


    public static EmojiconGroupEntity getData() {
        return DATA;
    }


    private static final EmojiconGroupEntity CUSTOM_DATA = createCustomData();

    private static EmojiconGroupEntity createCustomData() {
        EmojiconGroupEntity emojiconGroupEntity = new EmojiconGroupEntity();
        Emojicon[] datas = null;

        String string = SPHelper.getString(AppConfig.EX_KEY, "");
        if (!StringUtils.isEmpty(string)) {
            try {
                JSONArray array = new JSONArray(string);
                if (array != null) {
                    int length = array.length();
                    datas = new Emojicon[length + 1];
                    datas[0] = new Emojicon(R.drawable.icon_addpic_unfocused, "",
                        Type.BIG_EXPRESSION);
                    datas[0].setBigIcon(R.drawable.icon_addpic_unfocused);
                    datas[0].setIdentityCode("add_ex");
                    datas[0].setName("");

                    for (int i = 0; i < length; i++) {
                        String emoObj = array.getString(i);
                        EmoBean bean = GsonHelper.getObject(emoObj, EmoBean.class);
                        if (bean != null) {
                            String path = bean.getContent();
                            datas[i + 1] = new Emojicon();
                            datas[i + 1].setType(Type.BIG_EXPRESSION);
                            datas[i + 1].setIconPath(path);
                            datas[i + 1].setBigIconPath(path);
                            datas[i + 1].setName("");
                            datas[i + 1].setIdentityCode(path);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            datas = new Emojicon[1];
            datas[0] = new Emojicon(R.drawable.icon_addpic_unfocused, null, Type.BIG_EXPRESSION);
            datas[0].setBigIcon(R.drawable.icon_addpic_unfocused);
            datas[0].setIdentityCode("add_ex");
            datas[0].setName("");
        }
        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.gif);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        return emojiconGroupEntity;
    }

    public static EmojiconGroupEntity getCustomData(String data) {
        EmojiconGroupEntity emojiconGroupEntity = new EmojiconGroupEntity();
        Emojicon[] datas = null;

        String string = SPHelper.getString(AppConfig.EX_KEY, "");
        if (!StringUtils.isEmpty(string)) {
            try {
                JSONArray array = new JSONArray(string);
                if (array != null) {
                    int length = array.length();
                    datas = new Emojicon[length + 1];
                    datas[0] = new Emojicon(R.drawable.icon_addpic_unfocused, "",
                        Type.BIG_EXPRESSION);
                    datas[0].setBigIcon(R.drawable.icon_addpic_unfocused);
                    datas[0].setIdentityCode("add_ex");
                    datas[0].setName("");

                    for (int i = 0; i < length; i++) {
                        String emoObj = array.getString(i);
                        EmoBean bean = GsonHelper.getObject(emoObj, EmoBean.class);
                        if (bean != null) {
                            String path = bean.getContent();
                            datas[i + 1] = new Emojicon();
                            datas[i + 1].setType(Type.BIG_EXPRESSION);
                            datas[i + 1].setIconPath(path);
                            datas[i + 1].setBigIconPath(path);
                            datas[i + 1].setName("");
                            datas[i + 1].setIdentityCode(path);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            datas = new Emojicon[1];
            datas[0] = new Emojicon(R.drawable.icon_addpic_unfocused, null, Type.BIG_EXPRESSION);
            datas[0].setBigIcon(R.drawable.icon_addpic_unfocused);
            datas[0].setIdentityCode("add_ex");
            datas[0].setName("");
        }

        emojiconGroupEntity.setEmojiconList(Arrays.asList(datas));
        emojiconGroupEntity.setIcon(R.drawable.emoji_134);
        emojiconGroupEntity.setType(Type.BIG_EXPRESSION);
        CUSTOM_DATA.setEmojiconList(Arrays.asList(datas));

        return emojiconGroupEntity;
    }


    public static EmojiconGroupEntity getCustomData() {
        return CUSTOM_DATA;
    }
}
