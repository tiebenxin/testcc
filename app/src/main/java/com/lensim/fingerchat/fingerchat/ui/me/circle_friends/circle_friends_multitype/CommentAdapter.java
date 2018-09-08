package com.lensim.fingerchat.fingerchat.ui.me.circle_friends.circle_friends_multitype;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.utils.UrlUtils;
import com.lens.chatmodel.view.friendcircle.CommentListView;
import com.lens.chatmodel.view.spannable.CircleMovementMethod;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.model.bean.CommentBean;

/**
 * date on 2018/2/9
 * author ll147996
 * describe
 */

public class CommentAdapter extends CommentListView.Adapter<CommentBean> {

    public final static String FONT_SIZE = "font_size";

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position) {
        View convertView = View.inflate(mContext, R.layout.im_social_item_comment, null);
        TextView commentTv = convertView.findViewById(R.id.commentTv);
        UIHelper.setTextSize(14, commentTv);

        final CircleMovementMethod circleMovementMethod = new CircleMovementMethod(R.color.name_selector_color,
            R.color.name_selector_color);

        final CommentBean bean = mDatas.get(position);
        String name =CyptoConvertUtils.decryptString(bean.getCommentUsername());
        String id = bean.getCommentUserid();
        String toReplyName = "";
        if (bean.getCreatorUserid() != null) {
            toReplyName = CyptoConvertUtils.decryptString(bean.getCommentUsername2());
        }

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(setClickableSpan(name, bean.getCommentUserid()));

        if (!StringUtils.isEmpty(toReplyName)) {
            builder.append(" 回复 ");
            builder.append(setClickableSpan(toReplyName, bean.getCommentUserid()));
        }
        builder.append(": ");
        //转换表情字符
        String contentBodyStr = CyptoConvertUtils.decryptString(bean.getCommentContent());
        int textSize = SPSaveHelper.getIntValue(FONT_SIZE, 1) * 4 + 12;
        Spannable smiledText = SmileUtils.getSmiledText(ContextHelper.getApplication(),
            UrlUtils.formatUrlString(contentBodyStr), TDevice.sp2px(textSize+10));
        builder.append(smiledText);
        commentTv.setText(builder);

        commentTv.setMovementMethod(circleMovementMethod);
        commentTv.setOnClickListener(v -> {
            if (circleMovementMethod.isPassToTv() && null != getListview().getOnItemClickListener()) {
                getListview().getOnItemClickListener().onItemClick(position);
            }
        });
        commentTv.setOnLongClickListener(v -> {
            if (circleMovementMethod.isPassToTv() && null != getListview().getOnItemLongClickListener()) {
                getListview().getOnItemLongClickListener().onItemLongClick(position);
                return true;
            }
            return false;
        });

        return convertView;
    }
}
