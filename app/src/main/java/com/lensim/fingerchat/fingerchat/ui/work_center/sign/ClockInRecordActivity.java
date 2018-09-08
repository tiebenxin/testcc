package com.lensim.fingerchat.fingerchat.ui.work_center.sign;


import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.SSOTokenRepository;
import com.lensim.fingerchat.data.response.ret.RetObjectResponse;
import com.lensim.fingerchat.data.work_center.SignInJsonRet;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityClockInRecordBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 签到历史记录
 *
 * Created by LL117394 on 2017/8/23
 */

public class ClockInRecordActivity extends FGActivity {

    //打卡记录，是不是今天
    public static final int STATUS_TODAY = 1123;
    public static final int STATUS_NOT_TODAY = 1122;

    private TimeLineAdapter mTimeLineAdapter;
    private List<SignInJsonRet> mDataList = new ArrayList<>();
    private Date mDateToCompare, mTempDate;
    private ActivityClockInRecordBinding ui;

    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_clock_in_record);
        ui.clockInToolbar.setTitleText("外出打卡记录");
        initBackButton(ui.clockInToolbar,true);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        ui.recyclerViewClockIn.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ui.recyclerViewClockIn.setHasFixedSize(true);
        mTimeLineAdapter = new TimeLineAdapter(mDataList, Orientation.VERTICAL, false);
        ui.recyclerViewClockIn.setAdapter(mTimeLineAdapter);
        mTimeLineAdapter.setOnItemClickListener((view, position) ->
            openActivity(ClockInRecordActivity.this, mDataList.get(position)));

        getDataByPage();
    }

    public void openActivity(Activity context, SignInJsonRet item) {
        Intent intent = new Intent(context, ClockInRecordDetailActivity.class);
        intent.putExtra(ClockInRecordDetailActivity.PARAMS_CONTENT, item);
        context.startActivity(intent);

    }


    public void getDataByPage() {
        String start = TimeUtils.getDateFirstDayThisMonth();
        String userName = SSOTokenRepository.getInstance().getSSOToken().getUserId();
        Http.getSignIn(userName, start, TimeUtils.getDate(), "token")
            .compose(RxSchedulers.compose())
            .subscribe(
                new BaseObserver<RetObjectResponse<String>>() {
                    @Override
                    public void onNext(RetObjectResponse<String> response) {
                        String dateString = "";
                        Gson gson = new Gson();
                        if (1 == response.retCode) {
                            //按每一天分组显示
                            List<SignInJsonRet> items = gson.fromJson(response.retData, new TypeToken<List<SignInJsonRet>>() {}.getType());
                            if (items == null) return;
                            
                            for (int i = 0; i < items.size(); i++) {
                                SignInJsonRet item = items.get(i);

                                if (TimeUtils.getTimeStamp(item.getSignInTime()) > TimeUtils.getDateStampToday())
                                    item.setmStatus(STATUS_TODAY);
                                 else
                                     item.setmStatus(STATUS_NOT_TODAY);
                                try {
                                    dateString = item.getSignInTime().replace("T", " ");
                                    mTempDate = new SimpleDateFormat(TimeUtils.FORMATE_NO_TIME, Locale.getDefault()).parse(dateString);
                                    if (i == 0) mDateToCompare = mTempDate;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (i != 0 && TimeUtils.isSameDate(mTempDate, mDateToCompare)) {
                                    item.setFirstDay(false);
                                } else {
                                    item.setFirstDay(true);
                                    mDateToCompare = mTempDate;
                                }

                                mDataList.add(item);
                            }

                            mTimeLineAdapter.setData(mDataList);
                        } else {
                            T.show(response.retMsg);
                        }
                    }
                });
    }



    @Override
    public void backPressed() {
        onReturn();
    }

    public void onReturn() {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onReturn();
        }
        return super.onKeyDown(keyCode, event);
    }

}

