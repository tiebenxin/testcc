package com.lens.chatmodel.bean;


import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.Emojicon.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DefaultEmojiconDatas {
    
	public static final int NUM_PAGE = 6;// 总共有多少页
	public static int NUM = 20;// 每页20个表情,还有最后一个删除button
	private static Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private static Map<String, Integer> mFaceMap1 = new LinkedHashMap<String, Integer>();

	private static void initFaceMap() {

		mFaceMap.put("[微笑]", R.drawable.emoji_4);
		mFaceMap.put("[撇嘴]", R.drawable.emoji_5);
		mFaceMap.put("[色]", R.drawable.emoji_7);
		mFaceMap.put("[发呆]", R.drawable.emoji_1);
		mFaceMap.put("[得意]", R.drawable.emoji_3);
		mFaceMap.put("[流泪]", R.drawable.emoji_6);
		mFaceMap.put("[害羞]", R.drawable.emoji_2);

		mFaceMap.put("[闭嘴]", R.drawable.emoji_13);
		mFaceMap.put("[睡觉]", R.drawable.emoji_11);
		mFaceMap.put("[大哭]", R.drawable.emoji_8);
		mFaceMap.put("[尴尬]", R.drawable.emoji_9);
		mFaceMap.put("[怒]", R.drawable.emoji_10);
		mFaceMap.put("[调皮]", R.drawable.emoji_12);
		mFaceMap.put("[龇牙]", R.drawable.emoji_14);

		mFaceMap.put("[惊讶]", R.drawable.emoji_18);
		mFaceMap.put("[难过]", R.drawable.emoji_20);
		mFaceMap.put("[冷汗]", R.drawable.emoji_16);
		mFaceMap.put("[抓狂]", R.drawable.emoji_19);
		mFaceMap.put("[吐]", R.drawable.emoji_17);
		mFaceMap.put("[偷笑]", R.drawable.emoji_15);

		mFaceMap.put("[可爱]", R.drawable.emoji_22);
		mFaceMap.put("[白眼]", R.drawable.emoji_27);
		mFaceMap.put("[傲慢]", R.drawable.emoji_21);
		mFaceMap.put("[困]", R.drawable.emoji_23);
		mFaceMap.put("[惊恐]", R.drawable.emoji_24);
		mFaceMap.put("[流汗]", R.drawable.emoji_26);
		mFaceMap.put("[憨笑]", R.drawable.emoji_25);

		mFaceMap.put("[大兵]", R.drawable.emoji_30);
		mFaceMap.put("[奋斗]", R.drawable.emoji_31);
		mFaceMap.put("[咒骂]", R.drawable.emoji_28);
		mFaceMap.put("[疑问]", R.drawable.emoji_33);
		mFaceMap.put("[嘘]", R.drawable.emoji_29);
		mFaceMap.put("[晕]", R.drawable.emoji_32);
		mFaceMap.put("[衰]", R.drawable.emoji_34);

		mFaceMap.put("[骷髅]", R.drawable.emoji_39);
		mFaceMap.put("[敲打]", R.drawable.emoji_38);
		mFaceMap.put("[再见]", R.drawable.emoji_35);
		mFaceMap.put("[擦汗]", R.drawable.emoji_37);
		mFaceMap.put("[抠鼻]", R.drawable.emoji_36);
		mFaceMap.put("[鼓掌]", R.drawable.emoji_40);


		mFaceMap.put("[坏笑]", R.drawable.emoji_42);
		mFaceMap.put("[左哼哼]", R.drawable.emoji_44);
		mFaceMap.put("[右哼哼]", R.drawable.emoji_41);
		mFaceMap.put("[打哈欠]", R.drawable.emoji_46);
		mFaceMap.put("[鄙视]", R.drawable.emoji_47);
		mFaceMap.put("[委屈]", R.drawable.emoji_43);
		mFaceMap.put("[快哭了]", R.drawable.emoji_45);

		mFaceMap.put("[阴险]", R.drawable.emoji_54);
		mFaceMap.put("[么么哒]", R.drawable.emoji_48);
		mFaceMap.put("[可怜]", R.drawable.emoji_49);
		mFaceMap.put("[菜刀]", R.drawable.emoji_52);
		mFaceMap.put("[西瓜]", R.drawable.emoji_53);
		mFaceMap.put("[啤酒]", R.drawable.emoji_51);
		mFaceMap.put("[咖啡]", R.drawable.emoji_50);

		mFaceMap.put("[猪头]", R.drawable.emoji_58);
		mFaceMap.put("[玫瑰]", R.drawable.emoji_59);
		mFaceMap.put("[凋谢]", R.drawable.emoji_55);
		mFaceMap.put("[示爱]", R.drawable.emoji_60);
		mFaceMap.put("[爱心]", R.drawable.emoji_57);
		mFaceMap.put("[心碎了]", R.drawable.emoji_56);


		mFaceMap.put("[蛋糕]", R.drawable.emoji_67);
		mFaceMap.put("[炸弹]", R.drawable.emoji_66);
		mFaceMap.put("[便便]", R.drawable.emoji_61);
		mFaceMap.put("[月亮]", R.drawable.emoji_65);
		mFaceMap.put("[太阳]", R.drawable.emoji_62);
		mFaceMap.put("[抱抱]", R.drawable.emoji_64);
		mFaceMap.put("[强]", R.drawable.emoji_63);


		mFaceMap.put("[弱]", R.drawable.emoji_70);
		mFaceMap.put("[握手]", R.drawable.emoji_73);
		mFaceMap.put("[胜利]", R.drawable.emoji_74);
		mFaceMap.put("[抱拳]", R.drawable.emoji_71);
		mFaceMap.put("[勾引]", R.drawable.emoji_69);
		mFaceMap.put("[拳头]", R.drawable.emoji_72);
		mFaceMap.put("[OK]", R.drawable.emoji_68);

		mFaceMap.put("[奶瓶]", R.drawable.emoji_76);
		mFaceMap.put("[篮球]", R.drawable.emoji_77);
		mFaceMap.put("[钞票]", R.drawable.emoji_80);
		mFaceMap.put("[药]", R.drawable.emoji_79);
		mFaceMap.put("[米饭]", R.drawable.emoji_78);
		mFaceMap.put("[口罩]", R.drawable.emoji_75);

		mFaceMap.put("[破涕为笑]", R.drawable.emoji_86);
		mFaceMap.put("[吐舌]", R.drawable.emoji_81);
		mFaceMap.put("[脸红]", R.drawable.emoji_87);
		mFaceMap.put("[恐惧]", R.drawable.emoji_84);
		mFaceMap.put("[失望]", R.drawable.emoji_83);
		mFaceMap.put("[无语]", R.drawable.emoji_85);
		mFaceMap.put("[嘿哈]", R.drawable.emoji_82);

		mFaceMap.put("[捂脸]", R.drawable.emoji_90);
		mFaceMap.put("[奸笑]", R.drawable.emoji_89);
		mFaceMap.put("[机智]", R.drawable.emoji_91);
		mFaceMap.put("[皱眉]", R.drawable.emoji_92);
		mFaceMap.put("[耶]", R.drawable.emoji_93);
		mFaceMap.put("[鬼魂]", R.drawable.emoji_94);
		mFaceMap.put("[合十]", R.drawable.emoji_88);

		mFaceMap.put("[强壮]", R.drawable.emoji_95);
		mFaceMap.put("[彩带]", R.drawable.emoji_96);
		mFaceMap.put("[礼物]", R.drawable.emoji_97);
		mFaceMap.put("[红包]", R.drawable.emoji_98);
		mFaceMap.put("[鸡]", R.drawable.emoji_99);


	}

	private static void initFaceMap1(){
		mFaceMap1.put("[一]", R.drawable.emoji_100);
		mFaceMap1.put("[二]", R.drawable.emoji_106);
		mFaceMap1.put("[三]", R.drawable.emoji_102);
		mFaceMap1.put("[四]", R.drawable.emoji_123);
		mFaceMap1.put("[五]", R.drawable.emoji_107);
		mFaceMap1.put("[六]", R.drawable.emoji_111);
		mFaceMap1.put("[七]", R.drawable.emoji_101);
		mFaceMap1.put("[八]", R.drawable.emoji_110);
		mFaceMap1.put("[九]", R.drawable.emoji_105);
		mFaceMap1.put("[零]", R.drawable.emoji_139);

		mFaceMap1.put("[加]", R.drawable.emoji_114);
		mFaceMap1.put("[减]", R.drawable.emoji_112);
		mFaceMap1.put("[问号]", R.drawable.emoji_137);
		mFaceMap1.put("[叹号]", R.drawable.emoji_118);
		mFaceMap1.put("[错]", R.drawable.emoji_136);
		mFaceMap1.put("[对]", R.drawable.emoji_124);

		mFaceMap1.put("[除]", R.drawable.emoji_138);
		mFaceMap1.put("[禁止]", R.drawable.emoji_135);
		mFaceMap1.put("[右]", R.drawable.emoji_115);
		mFaceMap1.put("[左]", R.drawable.emoji_125);
		mFaceMap1.put("[上]", R.drawable.emoji_103);
		mFaceMap1.put("[下]", R.drawable.emoji_104);
		mFaceMap1.put("[播放]", R.drawable.emoji_130);
		mFaceMap1.put("[暂停]", R.drawable.emoji_133);


		mFaceMap1.put("[向上]", R.drawable.emoji_119);
		mFaceMap1.put("[右上方]", R.drawable.emoji_116);
		mFaceMap1.put("[向右]", R.drawable.emoji_121);
		mFaceMap1.put("[右下方]", R.drawable.emoji_117);
		mFaceMap1.put("[向下]", R.drawable.emoji_120);
		mFaceMap1.put("[左下方]", R.drawable.emoji_127);
		mFaceMap1.put("[向左]", R.drawable.emoji_122);
		mFaceMap1.put("[左上方]", R.drawable.emoji_126);

		mFaceMap1.put("[星星]", R.drawable.emoji_132);
		mFaceMap1.put("[旗帜]", R.drawable.emoji_131);
		mFaceMap1.put("[刷新]", R.drawable.emoji_113);
		mFaceMap1.put("[循环]", R.drawable.emoji_129);
		mFaceMap1.put("[信号]", R.drawable.emoji_108);
		mFaceMap1.put("[爱]", R.drawable.emoji_134);
		mFaceMap1.put("[停止]", R.drawable.emoji_109);
		mFaceMap1.put("[开始]", R.drawable.emoji_128);
	}

    private static final Emojicon[] DATA = createData();
    private static final Emojicon[] DATA1 = createData1();

	private static Emojicon[] createData1() {
		if(mFaceMap1.isEmpty()){
			initFaceMap1();
		}
		Emojicon[] datas = new Emojicon[mFaceMap1.size()];
		int i = 0;
		Set<Entry<String,Integer>> entrySet = mFaceMap1.entrySet();
		for (Entry<String, Integer> entry : entrySet) {
			datas[i] = new Emojicon(entry.getValue(), entry.getKey(), Type.NORMAL);
			i++;
		}

		return datas;
	}

	private static Emojicon[] createData(){
    	if(mFaceMap.isEmpty()){
    		initFaceMap();
    	}
        Emojicon[] datas = new Emojicon[mFaceMap.size()];
        int i = 0;
        Set<Entry<String,Integer>> entrySet = mFaceMap.entrySet();
        for (Entry<String, Integer> entry : entrySet) {
        	datas[i] = new Emojicon(entry.getValue(), entry.getKey(), Type.NORMAL);
        	i++;
		}
        
        return datas;
    }
    
    public static Emojicon[] getData(){
        return DATA;
    }

    public static Emojicon[] getData1(){
        return DATA1;
    }
}
