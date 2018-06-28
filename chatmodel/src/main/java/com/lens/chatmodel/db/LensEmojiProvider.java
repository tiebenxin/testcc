package com.lens.chatmodel.db;


import com.lens.chatmodel.bean.Emojicon;
import com.lens.chatmodel.bean.EmojiconGroupEntity;
import com.lens.chatmodel.interf.EmojiconInfoProvider;
import com.lensim.fingerchat.commons.utils.StringUtils;
import java.util.List;
import java.util.Map;

public class LensEmojiProvider implements EmojiconInfoProvider {
	
	private EmojiconGroupEntity entity;
	
	public LensEmojiProvider(EmojiconGroupEntity entity){
		this.entity = entity;
	}
	
	@Override
	public Emojicon getEmojiconInfo(String emojiconIdentityCode) {
		Emojicon emojicon = null;
		if(entity!=null && !StringUtils.isEmpty(emojiconIdentityCode)){
			List<Emojicon> emojiconList = entity.getEmojiconList();
			for (Emojicon emoji : emojiconList) {
				if(emoji.getIdentityCode().equals(emojiconIdentityCode)){
					emojicon = emoji;
					break;
				}
			}
		}
		
		return emojicon;
	}

	
	
	@Override
	public Map<String, Object> getTextEmojiconMapping() {
		// TODO Auto-generated method stub
		return null;
	}

}
