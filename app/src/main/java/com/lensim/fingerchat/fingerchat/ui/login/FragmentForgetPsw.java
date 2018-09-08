package com.lensim.fingerchat.fingerchat.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.fingerchat.api.message.ExcuteResultMessage;
import com.fingerchat.proto.message.Excute.ExcuteMessage;
import com.fingerchat.proto.message.Excute.ExcuteType;
import com.lens.chatmodel.ChatEnum.ENetStatus;
import com.lens.chatmodel.eventbus.ExcuteEvent;
import com.lens.chatmodel.eventbus.NetStatusEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.manager.MessageManager;
import com.lens.chatmodel.net.network.NetworkUtils;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.interf.OnControllerClickListenter;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.login.ControllerLoginItem.OnActionListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentForgetPsw extends BaseFragment {

    public final static int MIN_TIME = 1000;
    public final static int PHOME = 1;


    private ControllerLoginItem viewIdentifyCode;
    private ControllerLoginItem viewInputAccout;
    private IFragmentForgetListener listener;
    private LinearLayout ll_forget;
    private ControllerLoginButton viewIdentifyBtn;
    private TextView tv_notify;
    private String phoneNum;
    private ExcuteMessage message;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int result = msg.what;
            switch (result) {
                case PHOME:
                    showUserPhone(phoneNum,true);
                    break;
            }
        }
    };


    private String userId;
    private String code;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_forget_psw, null);
    }

    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    @RequiresApi(api = VERSION_CODES.JELLY_BEAN)
    protected void initView() {

        ll_forget = (LinearLayout) getView().findViewById(R.id.ll_forget);
        tv_notify = (TextView) getView().findViewById(R.id.tv_notify);

        viewInputAccout = new ControllerLoginItem(getView().findViewById(R.id.viewInputAccout));
        viewInputAccout.initIconHint(R.drawable.account_number, R.string.input_accout_lose_psw);
        viewInputAccout.addRight(ControllerLoginItem.TYPE_VERIFICATION_CODE);
        viewInputAccout.initEditType(false);
        viewInputAccout.setEidtAction(true);
        viewInputAccout.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getCode();
                }
            }
        });

        viewInputAccout.setOnClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                getCode();
            }
        });

        viewIdentifyCode = new ControllerLoginItem(getView().findViewById(R.id.viewIdentifyCode));
        viewIdentifyCode.initIconHint(R.drawable.verification_code, R.string.input_identity_code);
        viewIdentifyCode.initEditType(true);
        viewIdentifyCode.setEidtAction(true);

        viewIdentifyCode.setOnEditActionListener(new OnActionListener() {
            @Override
            public void onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (listener != null) {
                        if (StringUtils.isIdentifyCode(viewIdentifyCode.getText())) {
                            listener
                                .clickIdentify(viewInputAccout.getText(),
                                    viewIdentifyCode.getText());
                        } else {
                            T.showShort(R.string.input_right_identify_code);
                        }
                    }
                }
            }
        });

        viewIdentifyBtn = new ControllerLoginButton(getView().findViewById(R.id.viewIdentifyBtn));
        viewIdentifyBtn.setText(R.string.identify_now);
        viewIdentifyBtn.setOnControllerClickListener(new OnControllerClickListenter() {
            @Override
            public void onClick() {
                if (listener != null) {
                    code = viewIdentifyCode.getText();
                    if (StringUtils.isIdentifyCode(code)) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("user", userId);
                            object.put("code", code);
                            ExcuteMessage message = MessageManager.getInstance()
                                .createExcuteBody(ExcuteType.CHECK_VALIDATECODE, object.toString());
                            FingerIM.I.excute(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        T.showShort(R.string.input_right_identify_code);
                    }
                }
            }
        });
    }

    private void getCode() {
        if (!NetworkUtils.isNetAvaliale()) {
            T.showShort(R.string.no_network_connection);
            return;
        }
        //发送请求，获取验证码
        userId = viewInputAccout.getText();
        if (!TextUtils.isEmpty(userId)) {
            queryUserPhone(userId);
            getPhone(userId);
        } else {
            T.showShort(R.string.input_accout_lose_psw);
        }

    }

    private void queryUserPhone(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            message = MessageManager.getInstance()
                .createExcuteBody(ExcuteType.QUER_USER_PHONE, userId);
            if (message != null) {
                excuteCode(getContext());
            }
        }
    }

    /**
     * 获取验证码， 校验验证码
     * */
    public void excuteCode(Context context) {
        if (!FingerIM.I.hasStarted() || !FingerIM.I.isClientState()) {
            FingerIM.I.checkInit(context).startFingerIM();
        } else if (!FingerIM.I.isConnected()) {
            FingerIM.I.manualReconnect();
        } else if (FingerIM.I.isHandOk()) {
            FingerIM.I.excute(message);
        }
    }

    private void getPhone(String userId) {
        if (!TextUtils.isEmpty(userId)) {
            FingerIM.I.applyVerCode(userId, "");
            setClickable(false, "获取验证码");

        } else {
            T.show("用户名不能为空");

        }
    }

    private void showUserPhone(String phoneNum, boolean flag) {
        if (timer != null) {
            timer.start();
        }
        if (flag) {
            ll_forget.setVisibility(View.VISIBLE);
            tv_notify.setVisibility(View.VISIBLE);
            tv_notify.setText(phoneNum);
        } else {
            tv_notify.setVisibility(View.GONE);
        }
    }


    //倒计时
    private CountDownTimer timer = new CountDownTimer(45 * MIN_TIME, MIN_TIME) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (getActivity() != null) {
                setClickable(false, "(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
            }
        }

        @Override
        public void onFinish() {
            if (getActivity() != null) {
                setClickable(true, "重新获取验证码");
            }
        }
    };


    private void setClickable(boolean isClickable, String text) {
//        viewInputAccout.setClickable(isClickable);
//        viewInputAccout.setButtonText(text, R.drawable.btn_get_identify_code);
        viewInputAccout.setForgetButtonText(isClickable, text);
    }


    public void notifyDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }



    public void setListener(IFragmentForgetListener l) {
        listener = l;
    }


    public interface IFragmentForgetListener {

        void clickIdentify(String accout, String identifyCode);

        void clickBack();

    }

    @Override
    public void notifyRequestResult(IEventProduct event) {
        if (event instanceof ExcuteEvent) {
            ExcuteResultMessage message = ((ExcuteEvent) event).getPacket();
            if (message != null) {
                int code = message.message.getCode();
                if (code == Common.LOGIN_VERYFY_PASSED) {
                    listener.clickIdentify(viewInputAccout.getText(), viewIdentifyCode.getText());
                } else if (code == Common.LOGIN_VERYFY_ERROR) {
                    T.show("验证失败");
                }else if (code == Common.USER_NOT_FOUND) {
                    setClickable(true, "获取验证码");
                    T.show("用户不存在");
                } else if (code == Common.QUERY_OK) {
                    phoneNum = message.message.getResult();
                    handler.sendEmptyMessage(PHOME);
                }
            }
        } else if (event instanceof ResponseEvent) {
            int code = ((ResponseEvent) event).getCode();
            if (code == Common.REG_SMS_OK) {
                viewIdentifyCode.setVisible(true);
                viewIdentifyBtn.setVisible(true);
            } else if (code == Common.OBTAIN_CODE_EXIST) {
                T.show("上次验证码未失效");
                viewIdentifyCode.setVisible(true);
                viewIdentifyBtn.setVisible(true);
            } else if (code == Common.RATE_LIMIT) {
                T.show("发送短信频率过快");
            }
        } else if (event instanceof NetStatusEvent) {
            NetStatusEvent e = (NetStatusEvent) event;
            if (e.getStatus() == ENetStatus.SUCCESS_ON_SERVICE && this.message != null) {
                FingerIM.I.excute(message);
            }
        }
    }
}
