package com.lensim.fingerchat.hexmeet.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.ui.group.Constant;
import com.lens.chatmodel.ui.group.GroupSelectListActivity;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.commons.base.BaseObserver;
import com.lensim.fingerchat.commons.global.BaseURL;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.data.hexmeet.RoomInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.repository.SPSaveHelper;
import com.lensim.fingerchat.hexmeet.App;
import com.lensim.fingerchat.hexmeet.R;
import com.lensim.fingerchat.hexmeet.activity.MeetingDetailActivity;
import com.lensim.fingerchat.hexmeet.activity.StringPropertyEditor;
import com.lensim.fingerchat.hexmeet.adapter.DateTimeWheelAdapter;
import com.lensim.fingerchat.hexmeet.api.model.RestContact;
import com.lensim.fingerchat.hexmeet.api.model.RestEndpoint;
import com.lensim.fingerchat.hexmeet.api.model.RestMeeting;
import com.lensim.fingerchat.hexmeet.api.model.RestUser;
import com.lensim.fingerchat.hexmeet.conf.ConferenceStatus;
import com.lensim.fingerchat.hexmeet.conf.DefaultMeeting;
import com.lensim.fingerchat.hexmeet.contact.ContactDetail;
import com.lensim.fingerchat.hexmeet.utils.AvatarLoader;
import com.lensim.fingerchat.hexmeet.utils.Convertor;
import com.lensim.fingerchat.hexmeet.utils.Utils;
import com.lensim.fingerchat.hexmeet.widget.ChangeFocusLayout;
import com.lensim.fingerchat.hexmeet.widget.PopupKeyBoard;
import com.lensim.fingerchat.hexmeet.widget.wheel.OnWheelChangedListener;
import com.lensim.fingerchat.hexmeet.widget.wheel.WheelView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HexMeetContentFrag extends Fragment {

    private Activity context;
    private TextView headTitle;

    private LinearLayout starttimeRow;
    private LinearLayout endtimeRow;
    private LinearLayout conf_numRow;
    private LinearLayout endpointsRow;

    private TextView starttime;
    private TextView endtime;
    private TextView conf_num;
    private EditText title;
    private TextView remark;
    private EditText password;
    private TextView endpoints;
    private TextView contacts_hint;

    private GridView contactGridView;
    private volatile boolean isDeletingContact = false;
    private ContactAdapter contactAdapter = null;

    private RestMeeting meeting;
    private boolean isEditable = true;

    private View contentView;
    private RelativeLayout grid;

    private PopupKeyBoard keyBoard;

    private ArrayList<UserBean> resultChoosePeople;
    private List<String> alreadyChoosenGroup;
    private Map<String, String> selectedContacts;
    private Map<String, String> ContactsJIDMap;
    private List<RestContact> mAllHexUsers;

    OnGlobalLayoutListener listener = null;

    private enum RequestCode {
        edit_remark, select_contact;

        public static RequestCode fromOrdinal(int ordinal) {
            if (ordinal < 0 || ordinal >= RequestCode.values().length) return null;
            return RequestCode.values()[ordinal];
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (RequestCode.fromOrdinal(requestCode)) {
                case edit_remark:
                    try {
                        String val = data.getStringExtra("propertyValue");
                        String remarkVal = val == null ? "" : val;
                        remark.setText(remarkVal);
                        meeting.setRemarks(remarkVal);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case select_contact:
                    try {
                        resultChoosePeople = data.getParcelableArrayListExtra("invite_list");
                        if (null != resultChoosePeople && resultChoosePeople.size() > 0) {
                            UserBean temp;
                            boolean isGroup = false;
                            //如果是群，根据群id查所有群成员
                            for (int i = 0; i < resultChoosePeople.size(); i++) {
                                temp = resultChoosePeople.get(i);
                                isGroup = temp.getType() != 0;
                                if (isGroup) {//如果是群
                                    choosenGroup(temp);
                                } else {
                                    setRestContact(temp);
                                }
                            }
                            if (!isGroup) refreshMeeting();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void choosenGroup(UserBean temp) {
        String groupID = temp.getMucId();
        if (!StringUtils.isEmpty(groupID)) {
            getGroupMembers(groupID);
        }
        alreadyChoosenGroup.add(groupID);
    }

    private void setRestContact(UserBean temp) {
        if (!StringUtils.isEmpty(temp.getUserNick())) {
            RestContact tempContact = new RestContact();
            tempContact.setName(temp.getUserNick());
            tempContact.setImageURL(temp.getAvatarUrl());
            selectedContacts.put(temp.getUserId(), temp.getUserNick());
            ContactsJIDMap.put(temp.getAvatarUrl(), temp.getUserId());
        }
    }

    /**
     * 获取聊天室所有群成员
     */
    private void getGroupMembers(String room) {
        Http.getMucMember("teamdetail", UserInfoRepository.getUserName(), room)
            .compose(RxSchedulers.<List<RoomInfo>>compose())
            .subscribe(new BaseObserver<List<RoomInfo>>() {
                @Override
                public void onNext(List<RoomInfo> roomInfos) {
                    String name = "";
                    for (RoomInfo item : roomInfos) {
                        name = item.getUSR_Name();
                        name = StringUtils.isEmpty(name) ? item.getUSR_ID() : name;
                        if (StringUtils.isEmpty(name)) {
                            name = "";
                        }
                        selectedContacts.put(item.getJid(), name);
                        ContactsJIDMap.put(item.getUSR_UserImage(), item.getJid());
                    }
                    refreshMeeting();
                }
            });

    }

    public Map<String, String> getContactsJIDMap() {
        return ContactsJIDMap;
    }


    private void refreshMeeting() {
    String myJID = UserInfoRepository.getUserName() +  "@" + BaseURL.DEFAULT_SERVER_NAME;
    if (!selectedContacts.containsKey(UserInfoRepository.getUserName()) && !selectedContacts.containsKey(myJID)) {
      selectedContacts.put(myJID, UserInfoRepository.getUsernick());

      ContactsJIDMap.put(String.format(Route.obtainAvater, UserInfoRepository.getUserName()), myJID);
    }
    List<RestContact> contactArrayList = new ArrayList<RestContact>();
    List<String> fgUserID = new ArrayList<String>();
    for (Map.Entry<String, String> entry : selectedContacts.entrySet()) {
      String tempJID = entry.getKey();
      if (!StringUtils.isEmpty(tempJID) && !myJID.equals(tempJID)) {
        fgUserID.add(tempJID);
      }

      RestContact item = new RestContact();
      item.setFgJID(tempJID);
      item.setName(entry.getValue());
      tempJID = tempJID.substring(0, tempJID.indexOf("@"));
      item.setImageURL(String.format(Route.obtainAvater, tempJID));
      contactArrayList.add(item);
    }
    meeting.getContacts().clear();
    meeting.getContacts().addAll(contactArrayList);
    Collections.sort(meeting.getContacts(), new Comparator<RestContact>() {
      @Override
      public int compare(RestContact lhs, RestContact rhs) {
        return lhs.getName().compareTo(rhs.getName());
      }
    });

    Gson gson = new Gson();
    saveFGUsers(gson.toJson(fgUserID));
    if (contactAdapter == null) {
      refreshContacts();
    }
    contactAdapter.notifyDataSetChanged();
    adjustBottomButtons();
    if (!isEditable) {
      if (getActivity() instanceof MeetingDetailActivity) {
        ((MeetingDetailActivity) getActivity()).JoinToExistMeeting();
      }
    }
    }

    private void adjustBottomButtons() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.conference_content, container, false);
        context = getActivity();
        if (mAllHexUsers == null) {
            mAllHexUsers = new ArrayList<RestContact>();
        }
        alreadyChoosenGroup = new ArrayList<String>();
        selectedContacts = new HashMap<>();
        ContactsJIDMap = new HashMap<>();

        meeting = (RestMeeting) getActivity().getIntent().getSerializableExtra("meeting");
        if (meeting == null) {
            if (savedInstanceState != null
                && savedInstanceState.getSerializable("meeting") != null) {
                meeting = (RestMeeting) savedInstanceState.getSerializable("meeting");
            } else {
                meeting = ((DefaultMeeting) getActivity()).getDefaultMeeting();
            }
        }
        List<RestContact> contacts = meeting.getContacts();
        for (RestContact item : contacts) {
            String nick = StringUtils.isEmpty(item.getName()) ? item.getUserName() : item.getName();
            selectedContacts.put(item.getFgJID(), nick);
        }

        headTitle = (TextView) contentView.findViewById(R.id.head_title);

        starttimeRow = (LinearLayout) contentView.findViewById(R.id.starttime);
        starttime = (TextView) contentView.findViewById(R.id.starttime_view);
        starttimeRow.setOnClickListener(new DateTimeEditor(starttime, meeting, true));
        starttime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(meeting.getStartTime()));

        endtimeRow = (LinearLayout) contentView.findViewById(R.id.endtime);
        endtime = (TextView) contentView.findViewById(R.id.endtime_view);
        endtimeRow.setOnClickListener(new DateTimeEditor(endtime, meeting, false));
        endtime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm")
            .format(meeting.getStartTime() + meeting.getDuration()));

        conf_numRow = (LinearLayout) contentView.findViewById(R.id.conf_num);
        conf_num = (TextView) contentView.findViewById(R.id.conf_num_view);
        conf_num.setText(meeting.getNumericId() + "");
        TextView conf_num_label = (TextView) contentView.findViewById(R.id.conf_num_label);
        conf_num_label.setTextSize(App.isEnVersion() ? 14 : 15);

        final ChangeFocusLayout layout = (ChangeFocusLayout) contentView
            .findViewById(R.id.for_change_focus);
        title = (EditText) contentView.findViewById(R.id.title_view);
        title.setText(meeting.getName());
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                layout.setToLoseFocus(title);
                layout.setToGetFocus(contentView.findViewById(R.id.left_button));
                return false;
            }
        });
        title.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                    InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    title.clearFocus();
                    String strInput = title.getText().toString();
                    if (strInput.trim().equals("")) {
                        title.setText(meeting.getName());
                    } else {
                        title.setText(strInput);
                        meeting.setName(strInput);
                    }
                    return true;
                }
                return false;
            }
        });
        title.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) context
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    String strInput = title.getText().toString();
                    if (strInput.trim().equals("")) {
                        title.setText(meeting.getName());
                    } else {
                        title.setText(strInput);
                        meeting.setName(strInput);
                    }
                }
            }
        });

        contentView.findViewById(R.id.remark).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                StringPropertyEditor
                    .actionStart(HexMeetContentFrag.this, getString(R.string.conf_remark),
                        remark.getText().toString(),
                        RequestCode.edit_remark.ordinal(), false);
            }
        });
        remark = (TextView) contentView.findViewById(R.id.remark_view);

        ConferenceStatus.Status status = ConferenceStatus.getStatus(meeting);
        if (StringUtils.isEmpty(meeting.getRemarks())) {
            if (status.equals(ConferenceStatus.Status.NEW)) {
                remark.setHint(getString(R.string.conf_remark_tips));
            } else {
                remark.setHint("");
            }
        }
        remark.setText(meeting.getRemarks());

        password = (EditText) contentView.findViewById(R.id.password_view);
        keyBoard = new PopupKeyBoard(context, password, true);
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                password.setHint("");
                keyBoard.show(password);
                return false;
            }
        });
        password.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                    grid.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                    //password.clearFocus();
                    String strInput = password.getText().toString();
                    if (strInput.trim().equals("")) {
                        password.setHint(getString(R.string.input_password_tips));
                        password.setText("");
                        meeting.setConfPassword("");
                    } else {
                        password.setText(strInput);
                        meeting.setConfPassword(strInput);
                    }
                    return true;
                }
                return false;
            }
        });
        password.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String strInput = password.getText().toString();
                    if (strInput.trim().equals("")) {
                        password.setHint(getString(R.string.input_password_tips));
                        password.setText("");
                        meeting.setConfPassword("");
                    } else {
                        password.setText(strInput);
                        meeting.setConfPassword(strInput);
                    }
                }
            }
        });

        if (StringUtils.isEmpty(meeting.getConfPassword())) {
            if (status.equals(ConferenceStatus.Status.NEW)) {
                password.setHint(getString(R.string.input_password_tips));
            } else {
                password.setHint("");
            }
        }
        password.setText(meeting.getConfPassword());

        endpointsRow = (LinearLayout) contentView.findViewById(R.id.endpoints);
        endpoints = (TextView) contentView.findViewById(R.id.endpoint_view);
        List<RestEndpoint> eps = meeting.getEndpoints();
        if (null != eps && eps.size() > 0) {
            endpointsRow.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (RestEndpoint ep : eps) {
                sb.append(ep.getName()).append("、");
            }
            sb.deleteCharAt(sb.length() - 1);
            endpoints.setText(sb.toString());
        }

        contacts_hint = (TextView) contentView.findViewById(R.id.contacts_hint);

        contentView.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });

        contactGridView = (GridView) contentView.findViewById(R.id.contacts);
        grid = (RelativeLayout) contentView.findViewById(R.id.contact_grid);

        adjustBottomButtons();

        return contentView;
    }

    public String getTitle() {
        return title.getText().toString();
    }

    public RestMeeting getMeeting() {
        return meeting;
    }

    public View getContentView() {
        return contentView;
    }

    public TextView getHeadTitle() {
        return headTitle;
    }

    public TextView getContacts_hint() {
        return contacts_hint;
    }

    public GridView getContactGridView() {
        return contactGridView;
    }

    public LinearLayout getConfNumRow() {
        return conf_numRow;
    }

    public EditText getPassword() {
        return password;
    }

    public TextView getRemark() {
        return remark;
    }

    public void cancelEditable() {
        isEditable = false;
        title.setEnabled(false);
        contentView.findViewById(R.id.starttime).setClickable(false);
        contentView.findViewById(R.id.endtime).setClickable(false);
        contentView.findViewById(R.id.conf_num).setClickable(false);
        contentView.findViewById(R.id.remark).setClickable(false);
        password.setEnabled(false);
        contentView.findViewById(R.id.starttime_arrow).setVisibility(View.INVISIBLE);
        contentView.findViewById(R.id.endtime_arrow).setVisibility(View.INVISIBLE);
    }

    public void enableEditable() {
        isEditable = true;
        title.setEnabled(true);
        contentView.findViewById(R.id.starttime).setClickable(true);
        contentView.findViewById(R.id.endtime).setClickable(true);
        contentView.findViewById(R.id.conf_num).setClickable(false);
        contentView.findViewById(R.id.remark).setClickable(true);
        password.setEnabled(true);
        contentView.findViewById(R.id.starttime_arrow).setVisibility(View.VISIBLE);
        contentView.findViewById(R.id.endtime_arrow).setVisibility(View.VISIBLE);
        refreshContacts();
    }

    private class DateTimeEditor implements OnClickListener {

        private TextView textView;
        private RestMeeting meet;
        private boolean isStartTime;

        public DateTimeEditor(TextView textView, RestMeeting meet, boolean isStart) {
            super();
            this.textView = textView;
            this.meet = meet;
            this.isStartTime = isStart;
        }

        @Override
        public void onClick(View v) {
            final Calendar tmpCalendar = Calendar.getInstance();
            tmpCalendar.setTimeInMillis(
                isStartTime ? meet.getStartTime() : meet.getStartTime() + meet.getDuration());
            int confYear = tmpCalendar.get(Calendar.YEAR);
            int confMonth = tmpCalendar.get(Calendar.MONTH);
            int confDay = tmpCalendar.get(Calendar.DAY_OF_MONTH);
            int confHour = tmpCalendar.get(Calendar.HOUR_OF_DAY);
            int confMinute = tmpCalendar.get(Calendar.MINUTE);

            final View view = context.getLayoutInflater()
                .inflate(R.layout.conf_starttime_editor, null);

            final TextView year = (TextView) view.findViewById(R.id.year);
            year.setText(confYear + "");

            final WheelView day = (WheelView) view.findViewById(R.id.day);
            view.findViewById(R.id.left_arrow).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    year.setText((Integer.valueOf(year.getText().toString()) - 1) + "");
                    tmpCalendar.set(Calendar.YEAR, Integer.valueOf(year.getText().toString()));
                    int maxDays = tmpCalendar.getActualMaximum(Calendar.DATE);
                    DateTimeWheelAdapter dayAdapter = new DateTimeWheelAdapter(context, maxDays - 1,
                        2,
                        getString(R.string.day));
                    day.setViewAdapter(dayAdapter);
                    day.invalidateWheel(true);
                }
            });
            view.findViewById(R.id.right_arrow).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    year.setText((Integer.valueOf(year.getText().toString()) + 1) + "");
                    tmpCalendar.set(Calendar.YEAR, Integer.valueOf(year.getText().toString()));
                    int maxDays = tmpCalendar.getActualMaximum(Calendar.DATE);
                    DateTimeWheelAdapter dayAdapter = new DateTimeWheelAdapter(context, maxDays - 1,
                        2,
                        getString(R.string.day));
                    day.setViewAdapter(dayAdapter);
                    day.invalidateWheel(true);
                }
            });

            final WheelView month = (WheelView) view.findViewById(R.id.month);
            month.setViewAdapter(
                new DateTimeWheelAdapter(context, 11, 1, getString(R.string.month)));
            month.setCurrentItem(confMonth);
            month.setCyclic(true);

            month.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue, int newValue) {
                    tmpCalendar.set(Calendar.MONTH, newValue);
                    int maxDays = tmpCalendar.getActualMaximum(Calendar.DATE);
                    DateTimeWheelAdapter dayAdapter = new DateTimeWheelAdapter(context, maxDays - 1,
                        2,
                        getString(R.string.day));
                    day.setViewAdapter(dayAdapter);
                    day.invalidateWheel(true);
                }
            });

            int maxDays = tmpCalendar.getActualMaximum(Calendar.DATE);
            DateTimeWheelAdapter dayAdapter = new DateTimeWheelAdapter(context, maxDays - 1, 2,
                getString(R.string.day));
            day.setViewAdapter(dayAdapter);
            day.setCurrentItem(confDay - 1);
            day.setCyclic(true);

            final WheelView hour = (WheelView) view.findViewById(R.id.hour);
            hour.setViewAdapter(new DateTimeWheelAdapter(context, 23, 3, getString(R.string.hour)));
            hour.setCurrentItem(confHour);
            hour.setCyclic(true);

            final WheelView minute = (WheelView) view.findViewById(R.id.minute);
            minute.setViewAdapter(
                new DateTimeWheelAdapter(context, 59, 4, getString(R.string.minute)));
            minute.setCurrentItem(confMinute);
            minute.setCyclic(true);

            final PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
            pop.setOutsideTouchable(false);
            pop.setFocusable(true);
            view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    pop.dismiss();
                }
            });

            view.findViewById(R.id.ok).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar now = Calendar.getInstance();
                    Calendar tmp = Calendar.getInstance();
                    tmp.set(Calendar.YEAR, Integer.valueOf(year.getText().toString()));
                    tmp.set(Calendar.MONTH, month.getCurrentItem());
                    tmp.set(Calendar.DAY_OF_MONTH, day.getCurrentItem() + 1);
                    tmp.set(Calendar.HOUR_OF_DAY, hour.getCurrentItem());
                    tmp.set(Calendar.MINUTE, minute.getCurrentItem());
                    tmp.set(Calendar.SECOND, 0);

                    if (tmp.before(now)) {
                        Utils.showToast(context, getString(R.string.later_than_now));
                        return;
                    }

                    StringBuffer sb = new StringBuffer();
                    sb.append(
                        String.format("%d-%02d-%02d %02d:%02d",
                            Integer.valueOf(year.getText().toString()),
                            month.getCurrentItem() + 1, day.getCurrentItem() + 1,
                            hour.getCurrentItem(),
                            minute.getCurrentItem()));

                    textView.setText(sb);
                    try {
                        long newtime = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(sb.toString())
                            .getTime();
                        if (isStartTime) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            long duration = 2 * 60 * 60 * 1000;
                            String endTime = format.format(newtime + duration);
                            endtime.setText(endTime);

                            meeting.setStartTime(newtime);
                            meeting.setDuration(duration);
                        } else {
                            meeting.setDuration(newtime - meeting.getStartTime());
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (meeting.getDuration() <= 0) {
                        Utils.showToast(context, getString(R.string.starttime_later_than_endtime));
                        return;
                    }

                    pop.dismiss();
                }
            });

            pop.showAtLocation(textView, Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshContacts();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("meeting", meeting);
        super.onSaveInstanceState(outState);
    }

    private void refreshContacts() {
        if (meeting.getContacts().size() > 0 || meeting.getUsers().size() > 0) {
            contacts_hint.setVisibility(View.GONE);
        } else {
            return;
        }

        if (meeting.getContacts().size() > 0) {
            Collections.sort(meeting.getContacts(), new Comparator<RestContact>() {
                @Override
                public int compare(RestContact lhs, RestContact rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }

        if (meeting.getUsers().size() > 0) {
            Collections.sort(meeting.getUsers(), new Comparator<RestUser>() {
                @Override
                public int compare(RestUser lhs, RestUser rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }

        contactAdapter = new ContactAdapter(context, meeting);
        contactGridView.setAdapter(contactAdapter);
    }

    private class ContactAdapter extends BaseAdapter {

        private RestMeeting meeting;
        private LayoutInflater inflater = null;

        public ContactAdapter(Context context, RestMeeting meeting) {
            super();
            this.meeting = meeting;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            int count = meeting.getContacts().size() + meeting.getUsers().size();
            count = count + 2;
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.simple_grid_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.gridview_img);
                holder.titleName = (TextView) convertView.findViewById(R.id.gridview_text);
                holder.little_delete_icon = (ImageView) convertView.findViewById(R.id.btn_delete);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == meeting.getContacts().size() + meeting.getUsers().size()) {
                holder.titleName.setText("");
                holder.image.setImageResource(R.drawable.btn_invite);
                holder.image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDeletingContact = false;
                        addPeople();
                    }
                });
            } else if (position == meeting.getContacts().size() + meeting.getUsers().size() + 1) {
                holder.image.setImageResource(R.drawable.btn_del);
                holder.titleName.setText("");
                holder.image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isDeletingContact = !isDeletingContact;
                        ContactAdapter.this.notifyDataSetChanged();
                    }
                });
            } else {
                if (meeting.getContacts().size() > 0) {
                    final RestContact restContact = meeting.getContacts().get(position);
                    if (restContact != null) {
                        holder.titleName.setText(restContact.getName());
                        String imageUrl = restContact.getImageURL();
                        AvatarLoader.load(imageUrl, holder.image);

                        holder.image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isDeletingContact) {
                                    meeting.getContacts().remove(restContact);
                                    refreshContacts();

                                    if (meeting.getContacts().size() == 0) {
                                        isDeletingContact = !isDeletingContact;
                                    }

                                    contactAdapter.notifyDataSetChanged();
                                    adjustBottomButtons();
                                } else {
                                    boolean hideAction = false;
                                    if (meeting.getStatus() == null || ConferenceStatus
                                        .isOngoing(meeting.getStatus())
                                        || meeting.getStatus().equalsIgnoreCase("FINISHED")
                                        || meeting.getStatus().equalsIgnoreCase("APPROVED")
                                        || meeting.getStatus().equalsIgnoreCase("REJECTED")) {
                                        hideAction = true;
                                    }
                                    ContactDetail.actionStart(context, hideAction, restContact);
                                }
                            }
                        });

                        if (isDeletingContact) {
                            holder.little_delete_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.little_delete_icon.setVisibility(View.INVISIBLE);
                        }
                    }
                } else if (meeting.getUsers().size() > 0) {
                    final RestUser restUser = meeting.getUsers().get(position);
                    if (restUser != null) {
                        holder.titleName.setText(restUser.getName());
                        String imageUrl = Convertor.getAvatarUrl(restUser);
                        AvatarLoader.load(imageUrl, holder.image);

                        holder.image.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isDeletingContact) {
                                    meeting.getUsers().remove(restUser);
                                    refreshContacts();

                                    if (meeting.getUsers().size() == 0) {
                                        isDeletingContact = !isDeletingContact;
                                    }

                                    contactAdapter.notifyDataSetChanged();
                                    adjustBottomButtons();
                                } else {
                                    boolean hideAction = false;
                                    if (meeting.getStatus() == null || ConferenceStatus
                                        .isOngoing(meeting.getStatus())
                                        || meeting.getStatus().equalsIgnoreCase("FINISHED")
                                        || meeting.getStatus().equalsIgnoreCase("APPROVED")
                                        || meeting.getStatus().equalsIgnoreCase("REJECTED")) {
                                        hideAction = true;
                                    }
                                }
                            }
                        });

                        if (isDeletingContact) {
                            holder.little_delete_icon.setVisibility(View.VISIBLE);
                        } else {
                            holder.little_delete_icon.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }

            return convertView;
        }


        private void addPeople() {
            Intent intent = new Intent(getActivity(), GroupSelectListActivity.class);
            intent.putExtra(Constant.KEY_OPERATION, Constant.GROUP_SELECT_MODE_CARD);
            intent.putExtra(Constant.KEY_SELECT_USER, resultChoosePeople);
            startActivityForResult(intent, RequestCode.select_contact.ordinal());
        }

        private class ViewHolder {

            ImageView image = null;
            TextView titleName = null;
            ImageView little_delete_icon = null;
        }
    }


    private void saveFGUsers(String users) {
    SPSaveHelper.setValue(UserInfoRepository.getUserName() + "HexMeet", "fgUser", users);
    }
}


