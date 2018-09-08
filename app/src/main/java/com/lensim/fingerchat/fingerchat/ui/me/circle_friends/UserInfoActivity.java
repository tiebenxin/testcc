package com.lensim.fingerchat.fingerchat.ui.me.circle_friends;


import static com.lensim.fingerchat.commons.app.AppConfig.REGISTER_USER;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_CLIP_IMAGE;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_IMAGE;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import com.fingerchat.api.message.RespMessage;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.bean.body.ImageUploadEntity;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.net.HttpUtils;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lens.route.annotation.Path;
import com.lensim.fingerchat.commons.app.AppConfig;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.global.CommonEnum.EUploadFileType;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.components.dialog.DialogUtil;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.data.help_class.IUploadListener;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.ControllerInfoAvatar;
import com.lensim.fingerchat.fingerchat.ui.me.ControllerInfoImage;
import com.lensim.fingerchat.fingerchat.ui.me.ControllerInfoText;
import com.lensim.fingerchat.fingerchat.ui.me.IViewAvatarClickListener;
import com.lensim.fingerchat.fingerchat.ui.me.InputInfoActivity;
import com.lensim.fingerchat.fingerchat.ui.photo_picture.ClipPictureActivity;
import java.util.List;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@Path(ActivityPath.USER_INFO_ACTIVITY_PATH)
public class UserInfoActivity extends BaseUserInfoActivity {

    private ControllerInfoAvatar viewAvatar;
    private ControllerInfoText viewNick;
    private ControllerInfoText viewAccount;
    private ControllerInfoImage view2Code;
    private LinearLayout ll_identy_info;
    private ControllerInfoText viewFactory;
    private ControllerInfoText viewSex;
    private ControllerInfoText viewWorkNum;
    private ControllerInfoText viewUserName;
    private ControllerInfoText viewDepartment;
    private ControllerInfoText viewDuty;
    private ControllerInfoText viewPosition;
    private FGToolbar toolbar;
    private boolean isSelf;
    private UserBean userBean;
    private String sNick;
    private String avatarUrl;


    @Override
    public void initView() {
        setContentView(R.layout.activity_user_info);
        Intent intent = getIntent();
        String userId = intent.getStringExtra(ActivityPath.USER_ID);
        if (!TextUtils.isEmpty(userId) && userId.equals(getUserId())) {
            isSelf = true;
        } else {
            isSelf = false;
            userBean = intent.getParcelableExtra(ActivityPath.USER);

        }
        toolbar = findViewById(R.id.viewTitleBar);
        initBackButton(toolbar, true);
        toolbar.setTitleText(R.string.private_info);
        viewAvatar = new ControllerInfoAvatar(findViewById(R.id.viewAvatar));
        viewNick = new ControllerInfoText(findViewById(R.id.viewNick));
        viewAccount = new ControllerInfoText(findViewById(R.id.viewAccout));
        view2Code = new ControllerInfoImage(findViewById(R.id.view2Code));
        ll_identy_info = findViewById(R.id.ll_identify_info);
        viewFactory = new ControllerInfoText(findViewById(R.id.viewFactoryName));
        viewSex = new ControllerInfoText(findViewById(R.id.viewSex));
        viewWorkNum = new ControllerInfoText(findViewById(R.id.viewWorkNum));
        viewUserName = new ControllerInfoText(findViewById(R.id.viewUserName));
        viewDepartment = new ControllerInfoText(findViewById(R.id.viewDepartment));
        viewDuty = new ControllerInfoText(findViewById(R.id.viewDuty));
        viewPosition = new ControllerInfoText(findViewById(R.id.viewPosition));

        setData();
        initListener();
    }


    public void setData() {
        if (isSelf) {
            if (getUserInfo() == null) {
                return;
            }
            viewAvatar
                .setTitleAndContent(ContextHelper.getString(R.string.avatar), getUserAvatar());
            viewNick.setTitleAndContent(ContextHelper.getString(R.string.nick), getUserNick());
            viewAccount.setTitleAndContent(ContextHelper.getString(R.string.accout), getUserId());
            view2Code.setTitleAndContent(ContextHelper.getString(R.string.user_qrcode),
                R.drawable.qr_code_icon);

            if (getUserInfo().getIsvalid() == ESureType.YES.ordinal()) {//认证
                ll_identy_info.setVisibility(View.VISIBLE);
                viewFactory.setTitleAndContent(ContextHelper.getString(R.string.factroy_address),
                    getUserInfo().getWorkAddress());
                viewSex.setTitleAndContent(ContextHelper.getString(R.string.sex),
                    getUserInfo().getSex());
                viewWorkNum.setTitleAndContent(ContextHelper.getString(R.string.employee_id),
                    getUserInfo().getEmpNo());
                viewUserName.setTitleAndContent(ContextHelper.getString(R.string.real_name),
                    getUserInfo().getEmpName());
                viewDepartment.setTitleAndContent(ContextHelper.getString(R.string.department),
                    getUserInfo().getDptName());
                viewDuty
                    .setTitleAndContent(ContextHelper.getString(R.string.employee_duty), "");//职责
                viewPosition.setTitleAndContent(ContextHelper.getString(R.string.employee_job),
                    getUserInfo().getJobname());
            } else {
                ll_identy_info.setVisibility(View.GONE);
            }
        } else {
            if (userBean == null) {
                return;
            }
            viewAvatar
                .setTitleAndContent(ContextHelper.getString(R.string.avatar),
                    userBean.getAvatarUrl());
            viewNick
                .setTitleAndContent(ContextHelper.getString(R.string.nick),
                    userBean.getRemarkName());
            viewAccount
                .setTitleAndContent(ContextHelper.getString(R.string.accout), userBean.getUserId());
            view2Code.setTitleAndContent(ContextHelper.getString(R.string.user_qrcode),
                R.drawable.qr_code_icon);

            if (userBean.isValid()) {//认证
                ll_identy_info.setVisibility(View.VISIBLE);
                viewFactory.setTitleAndContent(ContextHelper.getString(R.string.factroy_address),
                    userBean.getWorkAddress());
                viewSex.setTitleAndContent(ContextHelper.getString(R.string.sex),
                    userBean.getSex());
                viewWorkNum.setTitleAndContent(ContextHelper.getString(R.string.employee_id),
                    userBean.getEmpNo());
                viewUserName.setTitleAndContent(ContextHelper.getString(R.string.real_name),
                    userBean.getEmpName());
                viewDepartment.setTitleAndContent(ContextHelper.getString(R.string.department),
                    userBean.getDptName());
                viewDuty
                    .setTitleAndContent(ContextHelper.getString(R.string.employee_duty), "");//职责
                viewPosition.setTitleAndContent(ContextHelper.getString(R.string.employee_job),
                    userBean.getJobName());
            } else {
                ll_identy_info.setVisibility(View.GONE);
            }
        }
    }

    private void initListener() {
        viewAvatar.setOnClickListener(new IViewAvatarClickListener() {
            @Override
            public void clickItem() {
                if (isSelf) {
                    Intent intent = new Intent(UserInfoActivity.this,
                        MultiImageSelectorActivity.class);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
                        MultiImageSelectorActivity.MODE_SINGLE);
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            }

            @Override
            public void clickAvatar() {
                String avatar = "";
                if (isSelf) {
                    avatar = getUserAvatar();
                } else {
                    if (userBean != null) {
                        avatar = userBean.getAvatarUrl();
                    }
                }
                if (!TextUtils.isEmpty(avatar)) {
                    Intent intent = PreviewSingleImageActivity
                        .nenIntent(UserInfoActivity.this, avatar);
                    startActivity(intent);
                } else {
                    T.show("头像不能为空");
                }
            }
        });

        viewNick.setOnClickListener(() -> {
            if (!isSelf) {//不能修改好友昵称,只能修改备注名
                return;
            }
            Intent nick = new Intent(this, InputInfoActivity.class);
            nick.putExtra("content", viewNick.getContent());
            nick.putExtra(InputInfoActivity.REQUEST_CODE, InputInfoActivity.REQUEST_NICK);
            startActivityForResult(nick, InputInfoActivity.REQUEST_NICK);
        });
        viewAccount.setOnClickListener(() -> {

        });
        view2Code.setOnClickListener(() -> {
            if (isSelf) {
                DialogUtil
                    .getUserInfoDialog(this, R.style.MyDialog, getUserId(), getUserAvatar(),
                        getUserNick())
                    .show();
            } else {
                if (userBean != null) {
                    DialogUtil
                        .getUserInfoDialog(this, R.style.MyDialog, userBean.getUserId(),
                            userBean.getAvatarUrl(), userBean.getUserNick())
                        .show();
                } else {
                    T.show("无用户信息");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                final List<ImageBean> path = data
                    .getParcelableArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                // 批量上传，全部上传完成
                if (path != null && path.size() > 0) {
                    if (!getUserAvatar().equals(path.get(0))) {
                        L.d("选择的图片路径", path.get(0));
                        String imagePath = path.get(0).path;
                        L.d("选择了一张图片", imagePath);
                        Intent intent = new Intent(this, ClipPictureActivity.class);
                        Uri uri = Uri.parse(imagePath);
                        intent.setData(uri);
                        intent.putExtra("photoPath", REGISTER_USER);
                        startActivityForResult(intent, REQUEST_CLIP_IMAGE);
                    }
                }
            }
        } else if (requestCode == InputInfoActivity.REQUEST_NICK) {
            if (resultCode == RESULT_OK) {
                sNick = data.getStringExtra(InputInfoActivity.INPUT_RESULT);
                if (isSelf) {
                    if (!StringUtils.isEmpty(sNick)) {
                        RosterManager.getInstance()
                            .updateUser(getUserId(), sNick, RosterManager.NICK);
                    }
                } else {
                    RosterManager.getInstance()
                        .updateRoster(userBean.getUserId(), sNick, RosterManager.NICK);
                }


            }
        } else if (requestCode == REQUEST_CLIP_IMAGE) {
            if (resultCode == RESULT_OK) {
                showProgress("正在上传..", true);
                final String imagePath = data.getStringExtra("bitmap");
                uploadImage(imagePath);
            }
        }

    }

    private void uploadImage(String path) {
        HttpUtils.getInstance()
            .uploadImageSave(path, EUploadFileType.JPG,
                new IUploadListener() {
                    @Override
                    public void onSuccess(Object result) {
                        dismissProgress();
                        if (result != null && result instanceof ImageUploadEntity) {
                            ImageUploadEntity entity = (ImageUploadEntity) result;
                            if (entity != null && !TextUtils.isEmpty(entity.getOriginalUrl())) {
                                avatarUrl = entity.getOriginalUrl();
                                SPSaveHelper.setValue(
                                    UserInfoRepository.getUserName() + AppConfig.HEAD_IMAGE,
                                    entity.getOriginalUrl());
                                RosterManager.getInstance()
                                    .updateUser(getUserId(), entity.getOriginalUrl(),
                                        RosterManager.AVATAR);
                            }
                        }
                    }

                    @Override
                    public void onFailed() {
                        dismissProgress();
                        T.showShort(R.string.upload_failed);
                    }

                    @Override
                    public void onProgress(int progress) {
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventMain(IEventProduct event) {
        if (event != null) {
            dealWithEvent(event);
        }
    }

    private void dealWithEvent(IEventProduct event) {
        if (event instanceof ResponseEvent) {
            RespMessage message = ((ResponseEvent) event).getPacket();
            if (message != null && message.response != null) {
                int code = message.response.getCode();
                if (code == Common.UPDATE_ROSTER_SUCCESS) {//更新成功
                    System.out.println("修改成功");
                    ProviderUser
                        .updateRosterRemarkName(ContextHelper.getContext(), userBean.getUserId(),
                            sNick);
                    viewNick.setContent(sNick);
                } else if (code == Common.UPDATE_ROSTER_FAILURE) {//修改失败
                    T.show("修改失败");
                } else if (code == Common.UPDATE_SUCCESS) {
                    if (!TextUtils.isEmpty(avatarUrl)) {
                        UserInfoRepository.getInstance().getUserInfo()
                            .setImage(avatarUrl);

                        viewAvatar.setAvatar(avatarUrl);
                    } else if (!TextUtils.isEmpty(sNick)) {
                        UserInfoRepository.getInstance().getUserInfo()
                            .setUsernick(sNick);
                        viewNick.setContent(sNick);
                    }
                } else if (code == Common.UPDATE_FAILURE) {
                    T.show("修改失败");
                }
            }
        }
    }
}
