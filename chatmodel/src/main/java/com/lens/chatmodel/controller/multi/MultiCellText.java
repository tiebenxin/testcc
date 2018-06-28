package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.util.regex.Matcher;

/**
 * Created by LL130386 on 2018/2/1.
 */

public class MultiCellText extends MultiCellBase {

    private TextView tv_msg;
    private Spannable span;
    private int textSize;


    protected MultiCellText(Context context, EMultiCellLayout cellLayout,
        IChatEventListener listener) {
        super(context, cellLayout, listener);
        loadControls();

    }

    private void loadControls() {
        tv_msg = getView().findViewById(R.id.tv_msg);
    }

    @Override
    public void showData() {
        super.showData();
        if (mEntity != null) {
            textSize = SPHelper.getInt("font_size", 1) * 2 + 12;

            initText(tv_msg, mEntity.getBody());
        }
    }

    private void initText(TextView tv_msg, String content) {
        if (!TextUtils.isEmpty(content)) {
            Matcher matcher = StringUtils.URL1.matcher(content);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (matcher.find()) {
                String substring = content.substring(matcher.start(), matcher.end());
                builder.append(content.substring(0, matcher.start()));
//            builder.append(setClickableSpan(substring));
                builder.append(content.substring(matcher.end()));
                span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(mContext, builder, (int) TDevice.dpToPixel(textSize + 10)));
            } else {
                span = SmileUtils
                    .getSmiledText(mContext, content, (int) TDevice.dpToPixel(textSize + 10));
            }
            tv_msg.setText(span);
        }
    }
}
