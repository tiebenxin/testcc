package com.lensim.fingerchat.fingerchat.ui.contacts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.fingerchat.api.message.RespMessage;
import com.fingerchat.api.message.RosterMessage;
import com.fingerchat.proto.message.Roster.RosterItem;
import com.lens.chatmodel.ChatEnum.EActivityNum;
import com.lens.chatmodel.ChatEnum.EFragmentNum;
import com.lens.chatmodel.ChatEnum.ERelationStatus;
import com.lens.chatmodel.ChatEnum.ESureType;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.eventbus.EventEnum;
import com.lens.chatmodel.eventbus.EventFactory;
import com.lens.chatmodel.eventbus.RefreshEntity;
import com.lens.chatmodel.eventbus.RefreshEvent;
import com.lens.chatmodel.eventbus.ResponseEvent;
import com.lens.chatmodel.eventbus.RosterEvent;
import com.lens.chatmodel.im_service.FingerIM;
import com.lens.chatmodel.interf.IContactItemClickListener;
import com.lens.chatmodel.interf.IContactListener;
import com.lens.chatmodel.interf.ISearchClickListener;
import com.lens.chatmodel.manager.RosterManager;
import com.lens.chatmodel.ui.contacts.ActivityNewFriends;
import com.lens.chatmodel.ui.contacts.GroupsActivity;
import com.lens.chatmodel.ui.group.GroupListActivity;
import com.lens.chatmodel.ui.profile.FriendDetailActivity;
import com.lens.chatmodel.ui.search.SearchActivity;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.dialog.ContactListDialog;
import com.lensim.fingerchat.commons.global.Common;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.interf.IChatUser;
import com.lensim.fingerchat.commons.interf.IContactDialogListener;
import com.lensim.fingerchat.commons.interf.IEventProduct;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.search.ControllerSearch;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by LL130386 on 2017/11/14.
 */

public class FragmentTabContacts extends BaseFragment implements IContactDialogListener {

    private static final String[] CHANNELS = new String[]{"所有好友", "分组好友", "部门好友", "所有群"};
    private final int selectColor = ContextHelper.getColor(R.color.blue);
    private final int normalColor = ContextHelper.getColor(R.color.black_33);

    private ControllerSearch viewSearch;
    private ViewPager viewPager;
    private List<String> tabNames = new ArrayList<>();
    private List<BaseFragment> fragments;
    private List<String> mDataList = Arrays.asList(CHANNELS);
    private ArrayList<UserBean> mAllRosters;
    private List<String> userIds;
    private int mCurrentPager;
    private AdapterContactsPager mAdapter;
    private ContactListDialog contactDialog;
    private IChatUser selectedUser;
    private int numNoNick;
    private int numNormal;

    public static FragmentTabContacts newInstance() {
        return new FragmentTabContacts();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, null);
    }


    @Override
    protected void initView() {
        viewSearch = new ControllerSearch(getView().findViewById(R.id.viewSearch));
        viewSearch.setOnClickListener(new ISearchClickListener() {
            @Override
            public void search(String value) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        viewPager = getView().findViewById(R.id.viewpager);
        initFragments();

        viewPager.setCurrentItem(0);
        mCurrentPager = 0;
        initMagicIndicator();

        viewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                refreshData(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initFragments() {
        fragments = new ArrayList<>();
        FragmentContactsAll all = initFragmentAll();
        fragments.add(all);
//        FragmentContactsGroupFriend group = initFragmentGroupFriend();
//        fragments.add(group);
//        FragmentContactsDepartmentFriend department = initFragmentDepartment();
//        fragments.add(department);
//        FragmentContactsGroups groups = initFragmentGroups();
//        fragments.add(groups);
//        FragmentContactsMore more = initFragmentMore();
//        fragments.add(more);
        mAdapter = new AdapterContactsPager(getChildFragmentManager(), fragments, tabNames);
        viewPager.setAdapter(mAdapter);
        viewPager.setOffscreenPageLimit(2);
    }

    private FragmentContactsAll initFragmentAll() {
        FragmentContactsAll fragment = new FragmentContactsAll();
        fragment.setParentFragment(this);
        fragment.setHeaderItemClickListener(new IContactListener() {

            @Override
            public void clickNewFriend() {
                startActivity(new Intent(getActivity(), ActivityNewFriends.class));
            }

            @Override
            public void clickGroups() {
                startActivity(new Intent(getActivity(), GroupListActivity.class));
            }

            @Override
            public void clickGroupFriend() {
                startActivity(new Intent(getActivity(), GroupsActivity.class));
            }

            @Override
            public void clickCustomerSerivce() {

            }

            @Override
            public void clickSelf() {
                startActivityUserDetail(
                    RosterManager.getInstance()
                        .createUser(((BaseUserInfoActivity) getActivity()).getUserInfo()));
            }

        });

        fragment.setItemClickListener(new IContactItemClickListener() {
            @Override
            public void onClick(Object o) {
                if (o instanceof IChatUser) {
                    startActivityUserDetail((IChatUser) o);

                }
            }

            @Override
            public void onLongClick(Object o) {
                showContactDialog((IChatUser) o);

            }

        });
        return fragment;
    }

    private FragmentContactsGroups initFragmentGroups() {
        FragmentContactsGroups fragment = new FragmentContactsGroups();
        fragment.setParentFragment(this);
        fragment.setHeaderItemClickListener(new IContactListener() {

            @Override
            public void clickNewFriend() {

            }

            @Override
            public void clickGroups() {

            }

            @Override
            public void clickGroupFriend() {

            }

            @Override
            public void clickCustomerSerivce() {

            }

            @Override
            public void clickSelf() {

            }
        });

        fragment.setItemClickListener(new IContactItemClickListener() {
            @Override
            public void onClick(Object o) {
                if (o instanceof IChatUser) {
                    startActivityUserDetail((IChatUser) o);

                }
            }

            @Override
            public void onLongClick(Object o) {
                showContactDialog((IChatUser) o);
            }

        });
        return fragment;
    }

    private FragmentContactsGroupFriend initFragmentGroupFriend() {
        FragmentContactsGroupFriend fragment = new FragmentContactsGroupFriend();
        fragment.setParentFragment(this);

        fragment.setItemClickListener(new IContactItemClickListener() {
            @Override
            public void onClick(Object o) {
                if (o instanceof IChatUser) {
                    startActivityUserDetail((IChatUser) o);

                }
            }

            @Override
            public void onLongClick(Object o) {
                IChatUser user = (IChatUser) o;
                if (user.getUserId().equals(UserInfoRepository.getUserName())) {//自己不能删除
                    return;
                }
                showContactDialog(user);
            }

        });
        return fragment;
    }

    private void showContactDialog(IChatUser user) {
        contactDialog = new ContactListDialog(getActivity(), R.style.MyDialog,
            user, this);
        contactDialog.setCanceledOnTouchOutside(true);
        contactDialog.show();
    }

    private FragmentContactsDepartmentFriend initFragmentDepartment() {
        FragmentContactsDepartmentFriend fragment = new FragmentContactsDepartmentFriend();
        fragment.setParentFragment(this);
        fragment.setItemClickListener(new IContactItemClickListener() {
            @Override
            public void onClick(Object o) {
                if (o instanceof IChatUser) {
                    startActivityUserDetail((IChatUser) o);

                }
            }

            @Override
            public void onLongClick(Object o) {
                showContactDialog((IChatUser) o);
            }

        });
        return fragment;
    }

    private FragmentContactsMore initFragmentMore() {
        FragmentContactsMore fragment = new FragmentContactsMore();
        fragment.setParentFragment(this);
        fragment.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(Object bean) {
                if (bean instanceof IChatUser) {
                    startActivityUserDetail((IChatUser) bean);
                }
            }
        });
        return fragment;
    }


    private void startActivityUserDetail(IChatUser user) {
        Intent intent = FriendDetailActivity
            .createNormalIntent(getActivity(), (UserBean) user);
        getActivity().startActivity(intent);
    }

    @Override
    protected void initData() {
        super.initData();
        loadAllUser();
        getRosters();
    }


    private void initMagicIndicator() {
        MagicIndicator magicIndicator = getView().findViewById(R.id.magic_indicator);
        magicIndicator.setVisibility(View.GONE);
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(getActivity());
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(
                    context);
                simplePagerTitleView.setNormalColor(normalColor);
                simplePagerTitleView.setSelectedColor(selectColor);
                simplePagerTitleView.setText(mDataList.get(index));
                int factor = SPHelper.getInt("font_size", 1) * 2;
                if (factor <= 2) {
                    factor = 2;
                }
                simplePagerTitleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, factor + 12);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                        refreshData(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(selectColor);
                return linePagerIndicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }


    private void getRosters() {
        FingerIM.I.getRosters();
    }

    private void loadAllUser() {
        Observable.just(0)
            .map(new Function<Integer, List<UserBean>>() {
                @Override
                public List<UserBean> apply(Integer integer) throws Exception {
                    return ProviderUser.selectRosterAll(getActivity());
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext(Observable.<List<UserBean>>empty())
            .subscribe(new Consumer<List<UserBean>>() {
                @Override
                public void accept(List<UserBean> temp) throws Exception {
                    if (temp != null) {
                        initAndSortRosters(temp);
                    }
                }
            });


    }

    private void initUserIds(List<UserBean> users) {
        if (userIds == null) {
            userIds = new ArrayList<>();
        } else {
            userIds.clear();
        }
        int len = users.size();
        for (int i = 0; i < len; i++) {
            userIds.add(users.get(i).getUserId());
        }
    }


    @Override
    public void notifyRequestResult(IEventProduct event) {
        super.notifyRequestResult(event);
        if (event instanceof RosterEvent) {
            RosterMessage message = ((RosterEvent) event).getPacket();
            int code = message.message.getCode();
            if (code == Common.QUERY_OK) {
                List<RosterItem> rosters = message.message.getItemList();
                if (rosters != null && rosters.size() > 0) {
                    List<UserBean> users = RosterManager.getInstance()
                        .createChatUserFromList(rosters,
                            ERelationStatus.FRIEND);
                    saveRosters(users);
                    loadAllUser();
                }
            } else if (code == Common.INVITE_OK) {//邀请好友成功，刷新列表
                List<RosterItem> rosters = message.message.getItemList();
                if (rosters != null && rosters.size() > 0) {
                    List<UserBean> users = RosterManager.getInstance()
                        .createNewFriendFromList(rosters,
                            ERelationStatus.FRIEND);
                    addFriend(users);
                }

            } else if (code == Common.ADD_SUCCESS) {//添加好友成功，刷新列表
                List<RosterItem> rosters = message.message.getItemList();
                if (rosters != null && rosters.size() > 0) {
                    List<UserBean> users = RosterManager.getInstance()
                        .createNewFriendFromList(rosters,
                            ERelationStatus.FRIEND);
                    addFriend(users);
                }
            } else if (code == Common.USER_NOT_IN_ROSTER || code == Common.USER_NOT_FOUND) {//好友不存在
                T.showShort(getActivity(), "用户或者好友不存在");
            } else if (code == Common.SEND_INVITE) {//收到好友邀请
                List<RosterItem> rosters = message.message.getItemList();
                List<UserBean> newRoster = RosterManager.getInstance()
                    .createChatUserFromList(rosters, ERelationStatus.RECEIVE,
                        System.currentTimeMillis(), ESureType.NO.ordinal(),
                        ESureType.YES.ordinal());
                saveRosters(newRoster);
                fragments.get(mCurrentPager).notifyResumeData();
                notifyContactCountUpdate();
            }

        } else if (event instanceof ResponseEvent) {//删除成功
            RespMessage message = ((ResponseEvent) event).getPacket();
            int code = message.response.getCode();
            if (code == Common.DELETE_SUCCESS) {
                if (selectedUser != null) {
                    deleUser(selectedUser.getUserId());
                } else {
                    getRosters();
                }
            } else if (code == Common.USER_NOT_IN_ROSTER || code == Common.USER_NOT_FOUND) {//好友不存在
                T.showShort(getActivity(), "用户或者好友不存在");
            } else if (code == Common.DELETE_FAILURE) {//删除失败
                T.showShort(getActivity(), "删除失败");
            } else if (code == Common.ALREADY_SUB) {//已是好友
                T.showShort(getActivity(), "已是好友，请勿重复添加");
            }
        }
    }

    private void initAndSortRosters(List<UserBean> list) {
        if (list != null && list.size() > 0) {
            List<UserBean> temp = new ArrayList<>();
            List<UserBean> last = new ArrayList<>();
            int len = list.size();
            for (int i = 0; i < len; i++) {
                UserBean user = list.get(i);
                if (user == null) {
                    continue;
                }
                if (TextUtils.isEmpty(user.getUserNick())) {
                    last.add(user);
                } else {
                    if (!StringUtils.matchAllLetter(user.getFirstChar())) {
                        last.add(user);
                    } else {
                        temp.add(user);
                    }
                }
            }
            if (mAllRosters == null) {
                mAllRosters = new ArrayList<>();
            } else {
                mAllRosters.clear();
            }
            mAllRosters.addAll(temp);
            mAllRosters.addAll(last);
            numNoNick = last.size();
            numNormal = temp.size();
            initUserIds(mAllRosters);
        }
        fragments.get(mCurrentPager).notifyResumeData();

    }

    public ArrayList<UserBean> getAllRosters() {
        return mAllRosters;
    }

    public int getNoNickUserSize() {
        return numNoNick;
    }

    public int getNormalUserSize() {
        return numNormal;
    }

    public void saveRosters(List<UserBean> rosters) {
        for (int i = 0; i < rosters.size(); i++) {
            UserBean roster = rosters.get(i);
            if (roster != null) {
                ProviderUser.updateRoster(ContextHelper.getContext(), roster);
            }
        }
    }

    public void refreshData(int index) {
        if (fragments != null && index < fragments.size()) {
            fragments.get(index).notifyResumeData();
            mCurrentPager = index;
        }
    }

//    public void refreshData(int index) {
//        if (index < fragments.size()) {
//            Bundle bundle = new Bundle();
//            bundle.putParcelableArrayList("data", getAllRosters());
//            fragments.get(index).setArguments(bundle);
//            mCurrentPager = index;
//            fragments.get(index).notifyResumeData();
//            System.out.println(
//                FragmentTabContacts.class.getSimpleName() + "--refreshData" + ":" + getAllRosters()
//                    .size() + "index =" + index);
//        }
//    }

    @Override
    public void notifyResumeData() {
        super.notifyResumeData();
        if (fragments != null && mAdapter != null) {
            mAdapter.setFragments(fragments);
        }
        if (viewPager != null) {
            viewPager.setCurrentItem(mCurrentPager);
        }
        refreshData(mCurrentPager);
    }

    private void notifyContactCountUpdate() {
        RefreshEntity entity = new RefreshEntity();
        entity.setActivity(EActivityNum.MAIN.value);
        entity.setFragment(EFragmentNum.TAB_CONTACTS.value);
        RefreshEvent event = (RefreshEvent) EventFactory.INSTANCE
            .create(EventEnum.MAIN_REFRESH, entity);
        EventBus.getDefault().post(event);
    }


    @Override
    public void onGroupOrTagName(IChatUser user) {
        Intent intent = FriendAliasActivity
            .newIntent(getActivity(), user.getUserNick(), user.getUserId());
        getActivity().startActivity(intent);

    }

    @Override
    public void onDeleFriend(IChatUser user) {
        selectedUser = user;
        FingerIM.I.deleFriend(user.getUserId().toLowerCase());
    }


    private void addFriend(List<UserBean> users) {
        if (users != null && users.size() > 0) {
            UserBean bean = users.get(0);
            if (mAllRosters == null) {
                mAllRosters = new ArrayList<>();
                mAllRosters.add(bean);
                initAndSortRosters(mAllRosters);
            } else {
                mAllRosters.add(bean);
                initAndSortRosters(mAllRosters);
            }
            ProviderUser.updateRoster(ContextHelper.getContext(), bean);
            fragments.get(mCurrentPager).notifyResumeData();
        }
    }

    private void deleUser(String userId) {
        if (TextUtils.isEmpty(userId) || userIds == null || mAllRosters == null) {
            return;
        }
        int position = userIds.indexOf(userId);
        if (position >= 0 && position < mAllRosters.size()) {
            mAllRosters.remove(position);
            userIds.remove(position);
            ProviderUser.updateFirendStatus(userId, ERelationStatus.RECEIVE.ordinal());
            notifyResumeData();
        }
    }


}
