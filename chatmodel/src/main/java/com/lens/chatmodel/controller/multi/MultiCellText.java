package com.lens.chatmodel.controller.multi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Browser;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.lens.chatmodel.ChatEnum.EChatCellLayout;
import com.lens.chatmodel.ChatEnum.EMultiCellLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.interf.IChatEventListener;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableClickable;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.utils.L;
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
    private SpannableString subjectSpanText;
    private int textColor;


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

            textColor = SPHelper.getInt("font_receive_color", Color.BLACK);

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
                builder.append(setClickableSpan(substring));
                builder.append(content.substring(matcher.end()));
                span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(ContextHelper.getContext(), builder,
                        (int) TDevice.dpToPixel(textSize + 10)));
            } else {
                span = SmileUtils
                    .getSmiledText(ContextHelper.getContext(), content,
                        (int) TDevice.dpToPixel(textSize + 10));
            }
            tv_msg.setText(span);
            tv_msg.setTextColor(textColor);
            tv_msg.setTextSize(textSize);
        }
    }


    public SpannableString setClickableSpan(String textStr) {
        subjectSpanText = new SpannableString(textStr);
        subjectSpanText.setSpan(new SpannableClickable() {
                                    @Override
                                    public void onClick(View widget) {
                                        // TODO: 2016/8/14 此处应该是跳转至相册的操作
                                        L.i("ChatRowText:" + "setClickableSpan--onclick");
                                        Uri uri = Uri.parse(textStr);
                                        Context context = widget.getContext();
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                        context.startActivity(intent);
                                    }
                                }, 0, subjectSpanText.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return subjectSpanText;
    }
}
