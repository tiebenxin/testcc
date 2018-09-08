package com.lens.chatmodel.ui.message;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Browser;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import com.fingerchat.proto.message.Muc.MucMemberItem;
import com.lens.chatmodel.R;
import com.lens.chatmodel.bean.message.MessageBean;
import com.lens.chatmodel.databinding.ActivityMsgDetailBinding;
import com.lens.chatmodel.db.MucInfo;
import com.lens.chatmodel.utils.SmileUtils;
import com.lens.chatmodel.view.spannable.SpannableClickable;
import com.lens.chatmodel.view.spannable.SpannableUtil;
import com.lensim.fingerchat.commons.base.BaseMvpActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TDevice;
import java.sql.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by LL130386 on 2018/9/5.
 */
@CreatePresenter(MessageDetailPresenter.class)
public class MessageDetailActivity extends
    BaseMvpActivity<MessageDetailView, MessageDetailPresenter> implements MessageDetailView {

    private ActivityMsgDetailBinding ui;
    private MessageBean bean;


    public static void startActivity(Context context, MessageBean bean) {
        Intent intent = new Intent(context, MessageDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", bean);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_msg_detail);
        initBackButton(ui.toolbar, true);
        ui.toolbar.setTitleText("消息详情");

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        bean = getIntent().getParcelableExtra("data");
        if (bean != null) {
            ui.tvName.setText(bean.getNick());
            initText(ui.tvContent, bean.getContent());
            ui.tvTime.setText(StringUtils.friendly_time3(new Date(bean.getTime())));
            initTab();
        }
    }

    private void initText(TextView tv_msg, String content) {
        if (!TextUtils.isEmpty(content)) {
            Matcher matcher = StringUtils.URL1.matcher(content);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            Spannable span;
            if (matcher.find()) {
                String substring = content.substring(matcher.start(), matcher.end());
                builder.append(content.substring(0, matcher.start()));
                builder.append(setClickableSpan(substring));
                builder.append(content.substring(matcher.end()));
                span = SpannableUtil.getAtText(SmileUtils
                    .getSmiledText(ContextHelper.getContext(), builder,
                        (int) TDevice.dpToPixel(4 + 10)));
            } else {
                span = SmileUtils
                    .getSmiledText(ContextHelper.getContext(), content,
                        (int) TDevice.dpToPixel(4 + 10));
            }
            tv_msg.setText(span);

        }
    }

    public SpannableString setClickableSpan(String textStr) {
        SpannableString subjectSpanText = new SpannableString(textStr);
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

    private void initTab() {
        int totalMemberCount = MucInfo.getMucMemberCount(bean.getTo());
        List<String> readUsers = bean.getReadedUserList();
        int readCount = 0;
        if (readUsers != null && readUsers.size() > 0) {
            readCount = readUsers.size();
        }
        for (int i = 0; i < 2; i++) {
            View view = getLayoutInflater().inflate(R.layout.layout_slider, null);

        }
    }

    @Override
    public void setReadedMembers(List<MucMemberItem> readedMembers) {

    }

    @Override
    public void setUnreadedMembers(List<MucMemberItem> unreadedMembers) {

    }


}
