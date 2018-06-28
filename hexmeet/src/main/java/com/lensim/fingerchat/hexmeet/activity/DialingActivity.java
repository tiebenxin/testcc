package com.lensim.fingerchat.hexmeet.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.toolbar.FGToolbar;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.RuntimeData;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.db.RestCallRow_;
import com.lensim.fingerchat.hexmeet.utils.CallRecordManager;
import com.lensim.fingerchat.hexmeet.utils.DialOutRetryHandler;
import com.lensim.fingerchat.hexmeet.utils.NetworkUtil;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.lensim.fingerchat.hexmeet.widget.CallRecordAdapter;
import com.lensim.fingerchat.hexmeet.widget.CallRecordGroup;
import com.lensim.fingerchat.hexmeet.widget.ClearEditText;
import com.lensim.fingerchat.hexmeet.widget.MenuItem;
import com.lensim.fingerchat.hexmeet.widget.PopupMenuBottom;
import com.lensim.fingerchat.hexmeet.widget.PopupMenuBottom.OnItemOnClickListener;
import com.lensim.fingerchat.hexmeet.widget.SipNumberKeyboard;
import com.lensim.fingerchat.hexmeet.widget.SipNumberKeyboard.OnHideListener;
import com.lensim.fingerchat.hexmeet.widget.expandablelist.ActionSlideExpandableListView;
import com.lensim.fingerchat.hexmeet.widget.expandablelist.ActionSlideExpandableListView.OnActionClickListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialingActivity extends FGActivity {

    public final static String PARAMS_PHONE_NUM = "PHONE_NUM";
    //会议ID
    private String sipNumber = "";
    private SimpleDateFormat sdf;
    private Context context;
    private PopupMenuBottom popuMenu;
    private final ArrayList<CallRecordGroup> adapterGroups = new ArrayList<CallRecordGroup>();
    private CallRecordAdapter callRecordAdapter;
    private String selfSipNumber;
    private SipNumberKeyboard sipNumberKeyboard;

    private RelativeLayout titleBarDivider;
    private ClearEditText confIdEditor;
    private LinearLayout dialingBlock;
    private RelativeLayout dialingDivider;
    private LinearLayout noCallRecord;
    private ActionSlideExpandableListView call_record_listview;
    private LinearLayout call_record_list;
    private LinearLayout shielder;
    private RelativeLayout rootLayout;
    private View digital_keyboard;
    private FGToolbar toolbar;


    @Override
    public void initView() {
        setContentView(R.layout.activity_dialing);
        toolbar = findViewById(R.id.toolbar_hexmeet_dial);
        toolbar.setTitleText("拨号");
        initBackButton(toolbar, true);
        context = this;

        this.rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        this.shielder = (LinearLayout) findViewById(R.id.shielder);
        this.call_record_list = (LinearLayout) findViewById(R.id.call_record_list);
        this.call_record_listview = (ActionSlideExpandableListView) findViewById(
            R.id.call_record_listview);
        this.noCallRecord = (LinearLayout) findViewById(R.id.no_call_record);
        this.dialingDivider = (RelativeLayout) findViewById(R.id.dialing_divider);
        this.dialingBlock = (LinearLayout) findViewById(R.id.dialing_block);
        this.confIdEditor = (ClearEditText) findViewById(R.id.call_number);
        this.titleBarDivider = (RelativeLayout) findViewById(R.id.title_bar_divider);
        this.digital_keyboard = (View) findViewById(R.id.include1);
    }

    @Override
    public void initData(final Bundle savedInstanceState) {
        if (getIntent() != null) {
            sipNumber = getIntent().getStringExtra(PARAMS_PHONE_NUM);
            if (!StringUtils.isEmpty(sipNumber)) {
                confIdEditor.setText(sipNumber);
            }
        }
        selfSipNumber =
            RuntimeData.getLogUser() != null ? RuntimeData.getLogUser().getCallNumber() : "";
        sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.SIMPLIFIED_CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        callRecordAdapter = new CallRecordAdapter(context, R.layout.call_record_item,
            adapterGroups);
        call_record_listview.setAdapter(callRecordAdapter);
        initRecordList();

        newSipNumberKeyboard(confIdEditor);
        confIdEditor.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showSipNumberKeyboard(new OnHideListener() {
                    @Override
                    public void onHide() {
                        shielder.setVisibility(View.GONE);
                    }
                });
                shielder.setVisibility(View.VISIBLE);
                return false;
            }
        });
        confIdEditor.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                    || keyCode == KeyEvent.KEYCODE_CALL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {

                    if (!validate()) {
                        return false;
                    }
                    if (NetworkUtil.is3GConnected(context)) {
                        warning4gConversation();
                    } else {
                        callWithSipNumber(true);
                    }
                    return true;
                }
                return false;
            }
        });

        if (!StringUtils.isEmpty(sipNumber) && confIdEditor != null) {
            confIdEditor.setText(sipNumber);
            showSipNumberKeyboard();
        }
    }


    public void newSipNumberKeyboard(EditText edittext) {
        sipNumberKeyboard = new SipNumberKeyboard(edittext, digital_keyboard);
    }

    public void showSipNumberKeyboard(OnHideListener listener) {
        sipNumberKeyboard.show(listener);
    }

    public void showSipNumberKeyboard() {
        sipNumberKeyboard.show();
    }


    private boolean validate() {
        sipNumber = confIdEditor.getText().toString();
        if (StringUtils.isEmpty(sipNumber)) {
            Utils.showToast(context, R.string.input_call_number);
            return false;
        }
        Pattern p = Pattern.compile("[0-9]+(\\*[0-9]+)?");
        Matcher m = p.matcher(sipNumber);
        if (!m.matches()) {
            Utils.showToast(context, R.string.format);
            return false;
        }
        int pos = sipNumber.indexOf('*');
        if (pos >= 11 || (pos == -1 && sipNumber.length() >= 11)) {
            Utils.showToast(context, R.string.call_id_too_long);
            return false;
        }
        if (!NetworkUtil.isSipServerReachable(context)) {
            return false;
        }
        return true;
    }

    private void warning4gConversation() {
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alertdialog_warning_4g, null);
        final AlertDialog dlg = new Builder(context).setView(view).create();
        dlg.show();
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callWithSipNumber(true);
                dlg.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    private void callWithSipNumber(boolean isVideoCall) {
        Intent intent = new Intent();
        intent.setClass(context, ConnectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isVideoCall", isVideoCall);
        bundle.putBoolean("isFromDialing", true);
        String sipNumberWithoutPassword = sipNumber;
        if (sipNumber.contains("*")) {
            sipNumberWithoutPassword = sipNumber.split("\\*")[0];
        }
        bundle.putString("sipNumber", sipNumberWithoutPassword);
        intent.putExtras(bundle);
        startActivity(intent);

        DialOutRetryHandler.getInstance().cancel();
        DialOutRetryHandler.getInstance().init();
        DialOutRetryHandler.getInstance().startDialing(sipNumber, null, isVideoCall);
    }


    private void initRecordList() {
        call_record_listview.enableExpandOnItemClick();
        call_record_listview.setItemActionListener(new OnActionClickListener() {
            @Override
            public void onClick(View listView, final View buttonview, int position) {
                final CallRecordGroup group = adapterGroups.get(position);
                sipNumber = group.getLatestCall().getPeerSipNum();
                RestContact restContact = App.getContact(sipNumber);

                if (buttonview.getId() == R.id.call_record_video) {
                    clearMissedFlagForGroup(group);

                    if (!NetworkUtil.isSipServerReachable(context)) {
                        return;
                    }

                    if (NetworkUtil.is3GConnected(context)) {
                        warning4gCall(true, restContact);
                    } else {
                        makeCall(true, restContact);
                    }
                } else if (buttonview.getId() == R.id.call_record_audio) {
                    if (!NetworkUtil.isSipServerReachable(context)) {
                        return;
                    }

                    if (NetworkUtil.is3GConnected(context)) {
                        warning4gCall(false, restContact);
                    } else {
                        makeCall(false, restContact);
                    }

                    clearMissedFlagForGroup(group);
                } else if (buttonview.getId() == R.id.delete) {
                    popuMenu = new PopupMenuBottom(context);
                    popuMenu.setHint(getString(R.string.confirm_delete) + "?");
                    popuMenu.addItem(new MenuItem(context, getString(R.string.delete_recording),
                        Color.parseColor("#F57070"), 0));
                    popuMenu.setItemOnClickListener(new OnItemOnClickListener() {
                        @Override
                        public void onItemClick(MenuItem item, int pos) {
                            if (pos == 0) {
                                CallRecordManager.delete(sipNumber);
                                clearMissedFlagForGroup(group);
                                call_record_listview.collapse();
                                refresh();
                            }
                        }
                    });
                    popuMenu.show(rootLayout);
                } else if (buttonview.getId() == R.id.call_record_info) {
                    clearMissedFlagForGroup(group);
                    call_record_listview.collapse();

                    DialingDetail.actionStart(context, sipNumber);
                }
            }

        }, R.id.call_record_video, R.id.call_record_audio, R.id.call_record_info, R.id.delete);

        refresh();
    }

    private void warning4gCall(final boolean isVideo, final RestContact restContact) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View view = factory.inflate(R.layout.alertdialog_warning_4g, null);
        final AlertDialog dlg = new Builder(context).setView(view).create();
        dlg.show();
        Button submit = (Button) view.findViewById(R.id.submit);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall(isVideo, restContact);
                dlg.dismiss();
            }
        });

        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
    }

    private void makeCall(boolean isVideo, RestContact restContact) {
        Intent intent = new Intent();
        intent.setClass(context, ConnectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Bundle bundle = new Bundle();
        bundle.putBoolean("isVideoCall", isVideo);
        String sipNumberWithoutPassword = sipNumber;
        if (sipNumber.contains("*")) {
            sipNumberWithoutPassword = sipNumberWithoutPassword.split("\\*")[0];
        }
        bundle.putString("sipNumber", sipNumber);
        bundle.putBoolean("isFromDialing", true);
        intent.putExtras(bundle);

        startActivity(intent);

        DialOutRetryHandler.getInstance().cancel();
        DialOutRetryHandler.getInstance().init();
        DialOutRetryHandler.getInstance().startDialing(sipNumber, restContact, isVideo);

        call_record_listview.collapse();
    }

    private void clearMissedFlagForGroup(CallRecordGroup group) {
        DialInNotifyActivity.showNewMissedCallFlag = false;
        if (group.getMissedCallCount() > 0) {
            group.setMissedCallCount(0);
            RestCallRow_ latest = group.getLatestCall();
            latest.setDuration(1l);
            CallRecordManager.update(latest);

            boolean show = false;
            for (CallRecordGroup g : adapterGroups) {
                if (g.getMissedCallCount() > 0) {
                    show = true;
                    break;
                }
            }
        }
    }

    public void refresh() {
        final List<RestCallRow_> latestCallRecords = CallRecordManager
            .getLatestCallRecordPerPeerSip(selfSipNumber);
        adapterGroups.clear();

        Map<String, Integer> map_peerSip_missedCount = CallRecordManager
            .getMap_peerSip_missedCount();
        for (RestCallRow_ latestRecord : latestCallRecords) {
            CallRecordGroup group = new CallRecordGroup();
            group.setLatestCall(latestRecord);
            Integer missedCount = map_peerSip_missedCount.get(latestRecord.getPeerSipNum());
            group.setMissedCallCount(missedCount == null ? 0 : missedCount);
            adapterGroups.add(group);
        }

        if (adapterGroups.size() == 0) {
            call_record_list.setVisibility(View.INVISIBLE);
            noCallRecord.setVisibility(View.VISIBLE);
        } else {
            noCallRecord.setVisibility(View.INVISIBLE);
            call_record_list.setVisibility(View.VISIBLE);
            callRecordAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void backPressed() {
        if (sipNumberKeyboard != null && sipNumberKeyboard.isKeyBoardVisible()) {
            sipNumberKeyboard.hide();
        }
        super.backPressed();
    }
}

