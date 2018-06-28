package com.lensim.fingerchat.fingerchat.ui.login;

import static android.app.Activity.RESULT_OK;
import static com.lensim.fingerchat.commons.app.AppConfig.REGISTER_USER;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_CAMERA;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_CLIP_IMAGE;
import static com.lensim.fingerchat.commons.app.AppConfig.REQUEST_SINGLE_IMAGE;

import android.Manifest.permission;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.ui.image.MultiImageSelectorActivity;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.L;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.bean.ImageBean;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.interf.IPopItemClickListener;
import com.lensim.fingerchat.fingerchat.manager.PopManager;
import com.lensim.fingerchat.fingerchat.ui.login.ControllerLoginItem.OnActionListener;
import com.lensim.fingerchat.fingerchat.ui.photo_picture.ClipPictureActivity;
import com.lensim.fingerchat.fingerchat.view.RegisterBottomMenu;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.io.File;
import java.util.List;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentRegister extends BaseFragment {

    public final static int MIN_TIME = 1000;

    private ControllerLoginItem viewInputJobNum;
    private ControllerLoginItem viewInputPsw;
    private ControllerLoginItem viewInputNick;
    private ControllerLoginItem viewPhoneNum;
    private ControllerLoginItem viewIdentifyCode;
    private ControllerLoginButton viewLoginBtn;
    private IFragmentRegisterListener listener;
    private String jobNum;
    private String password;
    private String phoneNum;
    private String nick;
    private String photoName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, null);
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    protected void initView() {
        viewInputJobNum = new ControllerLoginItem(getView().findViewById(R.id.viewInputJobNum));
        viewInputPsw = new ControllerLoginItem(getView().findViewById(R.id.viewInputPsw));
        viewInputNick = new ControllerLoginItem(getView().findViewById(R.id.viewInputNick));
        viewPhoneNum = new ControllerLoginItem(getView().findViewById(R.id.viewPhoneNum));
        viewIdentifyCode = new ControllerLoginItem(getView().findViewById(R.id.viewIdentifyCode));
        viewLoginBtn = new ControllerLoginButton(getView().findViewById(R.id.viewLoginBtn));

        viewInputJobNum.initIconHint(R.drawable.account_number, R.string.input_job_num);
        viewInputJobNum.addRight(ControllerLoginItem.TYPE_TAKE_PHOTO);
        viewInputJobNum.initEditType(false);
        viewInputJobNum.setEidtAction(false);
        viewInputJobNum.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    viewInputPsw.requestFocus();
                }
            }
        });
        viewInputJobNum.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                showPopWindow();
            }
        });

        viewInputPsw.initIconHint(R.drawable.password, R.string.input_password);
        viewInputPsw.initEditType(true);
        viewInputPsw.addRight(ControllerLoginItem.TYPE_PSW);
        viewInputPsw.setEidtAction(false);
        viewInputPsw.setShowText(false);
        viewInputPsw.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    viewInputNick.requestFocus();
                }
            }
        });

        viewInputNick.initIconHint(R.drawable.ic_nick, R.string.input_nick);
        viewInputNick.initEditType(false);
        viewInputNick.setEidtAction(false);
        viewInputNick.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    viewPhoneNum.requestFocus();
                }
            }
        });

        viewPhoneNum.initIconHint(R.drawable.phone_number, R.string.input_phone_num);
        viewPhoneNum.initEditType(true);
        viewPhoneNum.setEidtAction(false);
        viewPhoneNum.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    viewIdentifyCode.requestFocus();
                }
            }
        });

        viewIdentifyCode.initIconHint(R.drawable.verification_code, R.string.input_identity_code);
        viewIdentifyCode.initEditType(true);
        viewIdentifyCode.setEidtAction(true);
        viewIdentifyCode.addRight(ControllerLoginItem.TYPE_VERIFICATION_CODE);

        viewIdentifyCode.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isValidGetIdentifyCode()) {
                        FingerIM.I.applyVerCode(jobNum, phoneNum);
                        timer.start();
                    }
                }
            }
        });

        viewIdentifyCode.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (isValidGetIdentifyCode()) {
                    FingerIM.I.applyVerCode(jobNum, phoneNum);
                    timer.start();
                }
            }
        });

        viewLoginBtn.setText(R.string.register);
        viewLoginBtn.setOnControllerClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (listener != null) {
                    if (!TextUtils.isEmpty(viewInputJobNum.getText()) && !TextUtils
                        .isEmpty(viewInputPsw.getText())) {
                        if (StringUtils.isIdentifyCode(viewIdentifyCode.getText())) {
                            listener
                                .clickRegister(viewInputJobNum.getText(), viewInputPsw.getText(),
                                    viewPhoneNum.getText(), viewIdentifyCode.getText());
                        } else {
                            T.showShort(R.string.input_right_identify_code);
                        }

                    } else {
                        if (TextUtils.isEmpty(viewInputJobNum.getText())) {
                            T.showShort(R.string.login);

                        }
                    }
                }
            }
        });


    }

    //倒计时
    private CountDownTimer timer = new CountDownTimer(45 * MIN_TIME, MIN_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (getActivity() != null) {
                viewIdentifyCode.setClickable(false);
                viewIdentifyCode.setButtonText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送",
                    R.drawable.btn_get_identify_code_red);
            }
        }

        @Override
        public void onFinish() {
            if (getActivity() != null) {
                viewIdentifyCode.setClickable(true);
                viewIdentifyCode.setButtonText("重新获取验证码", R.drawable.btn_get_identify_code);
            }
        }
    };

    private boolean isValidGetIdentifyCode() {
        jobNum = viewInputJobNum.getText();
        password = viewInputPsw.getText();
        nick = viewInputNick.getText();
        phoneNum = viewPhoneNum.getText();
        if (!TextUtils.isEmpty(jobNum) && !TextUtils.isEmpty(password) && !TextUtils
            .isEmpty(phoneNum)) {
            if (!StringUtils.isJobNum(jobNum)) {
                T.show("请输入正确工号");
                return false;
            } else {
                if (!StringUtils.isMobilePhone(phoneNum)) {
                    T.showShort(R.string.input_right_phone_num);
                    return false;
                } else {
                    return true;
                }
            }
        } else if (TextUtils.isEmpty(jobNum)) {
            T.showShort(R.string.job_num_empty);
            return false;
        } else if (TextUtils.isEmpty(password)) {
            T.showShort(R.string.password_empty);
            return false;
        } else if (TextUtils.isEmpty(phoneNum)) {
            T.showShort(R.string.phone_empty);
            return false;
        }
        return false;
    }

    @Override
    public void notifyActivityResult(int requestCode, int resultCode, Intent data) {
        super.notifyActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(getActivity(), ClipPictureActivity.class);
                Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/avatarCache/" + photoName);
                L.d("相机照相回调了", uri.getPath());
                intent.setData(uri);
                intent.putExtra("photoPath", REGISTER_USER);
                getActivity().startActivityForResult(intent, REQUEST_CLIP_IMAGE);
            }

        } else if (requestCode == REQUEST_SINGLE_IMAGE) {
            if (resultCode == RESULT_OK) {
                List<ImageBean> path = data
                    .getParcelableArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                if (path != null) {
                    String imagePath = path.get(0).path;
                    L.d("选择了一张图片", imagePath);
                    Intent intent = new Intent(getActivity(), ClipPictureActivity.class);
                    Uri uri = Uri.parse(imagePath);
                    intent.setData(uri);
                    intent.putExtra("photoPath", REGISTER_USER);
                    getActivity().startActivityForResult(intent, REQUEST_CLIP_IMAGE);
                }
            }
        } else if (requestCode == REQUEST_CLIP_IMAGE) {
            if (resultCode == RESULT_OK) {
                String imagePath = data.getStringExtra("bitmap");
                viewInputJobNum.addRight(BitmapFactory.decodeFile(imagePath));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void notifyDestroy() {
       if (timer != null) {
           timer.cancel();
           timer = null;
       }
    }

    private void showPopWindow() {
        RegisterBottomMenu menu = PopManager
            .createRegisterMenu(getActivity(), getView().getRootView());
        menu.setItemClick(new IPopItemClickListener() {
            @Override
            public void callCamera() {
                new RxPermissions(getActivity())
                    .request(permission.CAMERA)
                    .subscribe(bool -> callCameraIntent());

            }

            @Override
            public void callGallery() {
                callGalleryIntent();
            }

            @Override
            public void defaultAvatar() {
                T.showShort(ContextHelper.getContext(), "默认");

            }
        });
    }

    private void callCameraIntent() {
        Intent camara = new Intent();
        camara.setAction("android.media.action.IMAGE_CAPTURE");
        camara.addCategory("android.intent.category.DEFAULT");
        String folderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/avatarCache";
        File filedir = new File(folderPath);
        L.d("注册时照片的存储路径：", folderPath + "/headimage.png");
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        photoName = System.currentTimeMillis() + ".png";
        File file = new File(folderPath + "/" + photoName);
        Uri uri = Uri.fromFile(file);
        camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        getActivity().startActivityForResult(camara, REQUEST_CAMERA);
    }

    private void callGalleryIntent() {
        Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE,
            MultiImageSelectorActivity.MODE_SINGLE);
        getActivity().startActivityForResult(intent, REQUEST_SINGLE_IMAGE);
    }


    public void setListener(IFragmentRegisterListener l) {
        listener = l;
    }


    public interface IFragmentRegisterListener {

        void clickRegister(String accout, String password, String phone, String identifyCode);

        void clickBack();

    }

}
