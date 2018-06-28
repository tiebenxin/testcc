package com.lens.chatmodel.ui.message;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.lens.chatmodel.R;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.Carbon;
import com.lens.chatmodel.bean.transfor.BaseTransforEntity;
import com.lens.chatmodel.bean.transfor.MultiMessageEntity;
import com.lens.chatmodel.controller.multi.FactoryMultiCell;
import com.lens.chatmodel.databinding.ActivityAttachMsgBinding;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.multi.AdapterMultiList;
import com.lensim.fingerchat.commons.dialog.AttachDialog;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.helper.GsonHelper;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.data.help_class.IDataRequestListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import okhttp3.Response;

/**
 * Created by LL130386 on 2018/5/28.
 * 附件消息
 */

public class AttachMessageActivity extends BaseUserInfoActivity {

    private ActivityAttachMsgBinding ui;
    private MultiMessageEntity entity;
    private List<IChatRoomModel> chatModelList;

    public static Intent newIntent(Context context, ArrayList<String> msgIds) {
        Intent intent = new Intent(context, AttachMessageActivity.class);
        intent.putStringArrayListExtra("data", msgIds);
        return intent;
    }

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_attach_msg);
        ui.toolbar.setTitleText("附件消息");
        initBackButton(ui.toolbar, true);
        ui.toolbar.setConfirmBt("上传", new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AttachDialog dialog = new AttachDialog(AttachMessageActivity.this);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setOnConfrimListener(new AttachDialog.OnConfrimListener() {
                    @Override
                    public void onConfirm(String title) {
                        if (TextUtils.isEmpty(title)) {
                            T.show("主题不能为空");
                        } else {
                            dialog.dismiss();
                            uploadAttach(title);
                        }
                    }
                });
                dialog.show();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(ContextHelper.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        if (intent != null) {
            ArrayList<String> list = intent.getStringArrayListExtra("data");
            if (list != null && list.size() > 0) {
                chatModelList = MessageManager.getInstance()
                    .getMessagesByIds(this, list);

                if (chatModelList != null) {
                    entity = new MultiMessageEntity();
                    ArrayList<BaseTransforEntity> listEntity = new ArrayList<>();
                    int msgLen = chatModelList.size();
                    for (int i = 0; i < msgLen; i++) {
                        IChatRoomModel msg = chatModelList.get(i);
                        BaseTransforEntity bean = createTransforEntity(msg);
                        if (bean != null) {
                            listEntity.add(bean);
                        }
                    }
                    entity.setBody(listEntity);

                }
            }
        }
        if (entity != null) {
            AdapterMultiList mAdapter = new AdapterMultiList(this);
            mAdapter.setViewFactory(new FactoryMultiCell(this, null));
            mAdapter.setEntity(entity);
            ui.recyclerView.setAdapter(mAdapter);
        }

    }

    private void uploadAttach(String title) {
        if (chatModelList != null && chatModelList.size() > 0) {
            Carbon carbon = new Carbon();
            List<Carbon.DataBean> beanList = new ArrayList<>();
            int msgLen = chatModelList.size();
            for (int i = 0; i < msgLen; i++) {
                IChatRoomModel msg = chatModelList.get(i);
                Carbon.DataBean bean = new Carbon.DataBean();

                if (msg.isGroupChat()) {
                    carbon.setUsername(msg.getGroupName());
                    bean.setAccount(msg.isIncoming() ? msg.getTo() : msg.getFrom());
                    bean.setName(msg.getNick());
                } else {
                    carbon.setUsername(msg.getNick());
                    bean.setAccount(msg.isIncoming() ? msg.getTo() : msg.getFrom());
                    bean.setName(msg.getNick());

                }
                bean.setTs(msg.getTime());

                switch (msg.getMsgType()) {
                    case TEXT:
                        bean.setType("text");
                        bean.setContent(msg.getContent());
                        break;
                    case IMAGE:
                        bean.setType("image");
                        break;
                    case VIDEO:
                        bean.setType("video");
                        bean.setContent(msg.getContent());
                        break;
                    case VOICE:
                        bean.setType("voice");
                        bean.setContent(msg.getContent());
                        break;
                    case FACE:
                        bean.setType("gif");
                        bean.setContent(msg.getContent());
                        break;
                    default:
                        break;

                }
                beanList.add(bean);
            }
            carbon.setData(beanList);
            String data = GsonHelper.optObject(carbon);
            if (!TextUtils.isEmpty(data)) {
                HashMap<String, String> map = new HashMap<>();
                map.put("UserID", getUserId());
                map.put("FileTittle", title);
                map.put("ChatFileJson", data);

                HttpUtils.getInstance()
                    .uploadAttachTest(getUserId(), title, data, new IDataRequestListener() {
                        @Override
                        public void loadFailure(String reason) {
                            System.out.println("上传附件失败");
                        }

                        @Override
                        public void loadSuccess(Object object) {
                            if (object instanceof String) {
                                String result = (String) object;
                                if (result.contains("OK")) {
                                    System.out.println("上传附件成功");
                                    T.show("上传成功");
                                    finish();
                                } else {
                                    System.out.println("上传附件失败");
                                }
                            }
                        }
                    });
            }
        }
    }

    private BaseTransforEntity createTransforEntity(IChatRoomModel model) {
        if (model == null) {
            return null;
        }
        return BaseTransforEntity.createEntity(model);
    }


}
