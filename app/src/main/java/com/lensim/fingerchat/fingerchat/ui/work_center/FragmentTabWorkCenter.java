package com.lensim.fingerchat.fingerchat.ui.work_center;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import com.example.webview.BrowserActivity;
import com.lensim.fingerchat.commons.base.BaseMVPFragment;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.mvp.factory.CreatePresenter;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.DensityUtil;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.components.adapter.multitype.MultiTypeAdapter;
import com.lensim.fingerchat.components.dialog.InputPasswordDialog;
import com.lensim.fingerchat.components.dialog.nifty_dialog.Effectstype;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.data.login.PasswordRespository;
import com.lensim.fingerchat.data.work_center.WorkItem;
import com.lensim.fingerchat.data.work_center.identify.UserIdentify;
import com.lensim.fingerchat.data.work_center.identify.UserIdentifyResponse;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.controller.ControllerBadNet;
import com.lensim.fingerchat.fingerchat.databinding.FragmentWorkCenterBinding;
import com.lensim.fingerchat.fingerchat.ui.guide.ImageViewPagerAdapter;
import com.lensim.fingerchat.fingerchat.ui.settings.IdentifyActivity;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by LL130386 on 2017/11/14.
 * 工作中心
 */

@CreatePresenter(WorkCenterPresenter.class)
public class FragmentTabWorkCenter extends BaseMVPFragment<WorkCenterView, WorkCenterPresenter>
    implements WorkCenterView {

    //每行显示4个子item
    private static final int SPAN_COUNT = 4;
    private static final int MSG_START_SCROLL = 100;
    private static final int VIEWPAGER_SWITCH_DURING = 5000;//轮播时间

    private FragmentWorkCenterBinding ui;
    private MultiTypeAdapter adapter;
    private List<Object> items = new ArrayList<>();

    private String mEmpoyNo;

    private InputPasswordDialog dialog;
    AbItemMoveCallBack itemMoveCallBack;
    private int[] drawableIds = new int[]{R.drawable.ic_work_bg_1, R.drawable.ic_work_bg_2};
    private int pagerNum;
    private ImageView iv_dot;
    private int distance;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_START_SCROLL) {
                handler.removeMessages(MSG_START_SCROLL);
                int num = ui.viewpager.getCurrentItem();
                if (num % 2 == 0) {
                    ui.viewpager.setCurrentItem(1);
                } else {
                    ui.viewpager.setCurrentItem(0);
                }
                handler.sendEmptyMessageDelayed(MSG_START_SCROLL, VIEWPAGER_SWITCH_DURING);
            }
        }
    };

    public static FragmentTabWorkCenter newInstance() {
        return new FragmentTabWorkCenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        ui = DataBindingUtil.inflate(inflater, R.layout.fragment_work_center, container, false);
        return ui.getRoot();
    }

    @Override
    protected void initView() {
        ControllerBadNet viewBadNet = new ControllerBadNet(
            getView().findViewById(R.id.view_badnet));
        viewBadNet.setOnClickListener(() -> T.showShort(ContextHelper.getContext(), "reload"));
        initPagerAdapter();
        iniSelectPoint();
        initItemAdapter();
        dragSort();
        initItems();
    }

    private void iniSelectPoint() {
        ui.ivPoint.setImageResource(R.drawable.shape_dot_light);
        ui.ivPoint.setMinimumWidth(DensityUtil.dip2px(getContext(), 7));
        ui.ivPoint.setMinimumHeight(DensityUtil.dip2px(getContext(), 7));
        ui.ivPoint.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                distance =
                    ui.llPoints.getChildAt(1).getLeft() - ui.llPoints.getChildAt(0).getLeft();
                ui.ivPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void initPagerAdapter() {
        ImageViewPagerAdapter pagerAdapter = new ImageViewPagerAdapter(getImageViewList());
        ui.viewpager.setAdapter(pagerAdapter);
        startAutoScroll();
        ui.viewpager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {
                float leftMargin = distance * (position + positionOffset);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ui.ivPoint
                    .getLayoutParams();
                params.leftMargin = (int) leftMargin;
                ui.ivPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                float leftMargin = distance * position;
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ui.ivPoint
                    .getLayoutParams();
                params.leftMargin = (int) leftMargin;
                ui.ivPoint.setLayoutParams(params);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initItemAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (items.get(position) instanceof WorkItem) ? 1 : SPAN_COUNT;
            }
        });
        ui.workCenterRv.setLayoutManager(layoutManager);

        WorkCenterClassAdapter classAdapter = new WorkCenterClassAdapter(getActivity());
        WorkCenterItemAdapter itemAdapter = new WorkCenterItemAdapter(getActivity());

        adapter = new MultiTypeAdapter();
        adapter.register(String.class, classAdapter);
        adapter.register(WorkItem.class, itemAdapter);

        ui.workCenterRv.setNestedScrollingEnabled(false);
        ui.workCenterRv.setAdapter(adapter);

        itemAdapter.setListener((view, item) -> toActivity(view, item));
    }

    private List<View> getImageViewList() {
        List<View> list = new ArrayList<>();
        ImageView imageView;
        pagerNum = drawableIds.length;
        for (int i = 0; i < pagerNum; i++) {
            imageView = new ImageView(getContext());
            imageView.setImageResource(drawableIds[i]);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            list.add(imageView);
            addDot();
        }
        return list;
    }

    private void addDot() {
        iv_dot = new ImageView(getContext());
        iv_dot.setMinimumWidth(DensityUtil.dip2px(getContext(), 7));
        iv_dot.setMinimumHeight(DensityUtil.dip2px(getContext(), 7));
        iv_dot.setImageResource(R.drawable.shape_dot_dark);
        LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 40, 0);
        iv_dot.setLayoutParams(params);
        ui.llPoints.addView(iv_dot);
    }


    /**
     * 拖拽排序
     */
    private void dragSort() {
        itemMoveCallBack = new AbItemMoveCallBack() {

            @Override
            int setFirstPosition(RecyclerView.ViewHolder viewHolder) {
                if (items.get(viewHolder.getAdapterPosition()) instanceof String) {
                    return viewHolder.getAdapterPosition();
                } else {
                    for (int i = viewHolder.getAdapterPosition(); i >= 0; i--) {
                        if (items.get(i) instanceof String) {
                            return i;
                        }
                    }
                }
                return 0;
            }

            @Override
            int setLastPosition(RecyclerView.ViewHolder viewHolder) {
                if (items.get(viewHolder.getAdapterPosition()) instanceof String) {
                    return viewHolder.getAdapterPosition();
                } else {
                    for (int i = viewHolder.getAdapterPosition(); i < items.size(); i++) {
                        if (items.get(i) instanceof String) {
                            return i;
                        }
                    }
                }
                return items.size();
            }

            @Override
            boolean isDragEnabled(RecyclerView.ViewHolder viewHolder) {
                if (items.get(viewHolder.getAdapterPosition()) instanceof String) {
                    return false;
                } else {
                    return true;
                }

            }

            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder,
                ViewHolder target) {
                if (target.getAdapterPosition() <= setFirstPosition(viewHolder)
                    || target.getAdapterPosition() >= setLastPosition(viewHolder)) {
                    return true;
                }
                Collections
                    .swap(items, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                adapter
                    .notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }
        };

        ItemTouchHelper helper = new ItemTouchHelper(itemMoveCallBack);
        helper.attachToRecyclerView(ui.workCenterRv);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (UserIdentifyResponse.getInstance().getUserIdentify() != null) {
            UserIdentify userIdentify = UserIdentifyResponse.getInstance().getUserIdentify();
            mEmpoyNo = userIdentify.EmployeeNO;
            mEmpoyNo = StringUtils.isEmpty(mEmpoyNo) ? "" : mEmpoyNo;
        }
    }


    private void initItems() {
        getMvpPresenter().initItems();
    }

    @Override
    public void setItems(List<Object> items) {
        this.items = items;
        sortItems();
        adapter.setItems(items);
    }

    //排序过滤items
    public void sortItems() {
        if (items != null && items.size() > 0) {

        }
    }


    private void toActivity(View view, WorkItem item) {

        final String title = item.getFuncName();
        final String url = item.getFuncAddress();
        int funcId = item.getFuncId();
        int funcType = item.getFuncType();

        if (37 == funcId) {//签到
//            Intent intent = new Intent(getActivity(), ClockInActivity.class);
//            getActivity().startActivity(intent);
        } else if (7 == funcId) {//电话会议
            T.show("电话会议");
//            HexMeetLogin login = new HexMeetLogin(getActivity());
//            login.toHexMeetLogin();
        } else if (7 == funcType) {//OAToken ——news
            getMvpPresenter().toOAActivity(url, title);
        } else if ("我的薪资".equals(title)) {
            if (StringUtils.isEmpty(mEmpoyNo)) {
                T.showShort(getActivity(), "没有获取到工号信息,请先进行认证");
                return;
            }
            dialog = new InputPasswordDialog(getActivity(), R.style.PasswordDialog);
            dialog.setOnPwdConfrimListener((content) -> comfirmClick(content, url, title));
            TDevice.showSoftKeyboard(dialog);
            dialog.show();
        } else {
            getMvpPresenter().toOtherActivity(url, title);
        }
    }

    private void comfirmClick(String content, String url, String title) {
        if (TextUtils.isEmpty(content)) {
            T.showShort(getActivity(), "密码不能为空");
            return;
        }

        if (content.equals(PasswordRespository.getPassword())) {
            dialog.dismiss();
            //(time_stamp="年-月-日 时：分：秒"&user_name="用户名"，注意参数单元之间没有空格
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            String param = "time_stamp=" + format.format(new Date()) + "&user_name=" + mEmpoyNo;
            String encodeParam = null;
            try {
                String key = CyptoUtils.getMD5("king@7cc").substring(0, 8).toUpperCase();
                encodeParam = CyptoUtils.EncryptAsDoNet(param, key);
                encodeParam = URLEncoder.encode(encodeParam, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String mUrl;
            if (url.contains("?")) {
                mUrl = url + "username=" + mEmpoyNo + "&parm=" + encodeParam;
            } else {
                mUrl = url + "?username=" + mEmpoyNo + "&parm=" + encodeParam;
            }
            startBrowserActivity(mUrl, title);
        } else {
            T.showShort(getActivity(), "密码错误");
        }
    }


    @Override
    public void startBrowserActivity(String url, String title) {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        intent.putExtra(BrowserActivity.TITLE, title);
        startActivity(intent);
    }


    @Override
    public void showIdentifyDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(getActivity());
        builder.withTitle("认证提示")
            .withMessage("请先至\"我的\"->点击头像进入个人信息->右上角进行认证")
            .withEffect(Effectstype.Newspager)
            .withButton1Text("取消")
            .withButton2Text("前往认证")
            .setButton1Click(v -> builder.dismiss())
            .setButton2Click(v -> {
                Intent intent = new Intent(getActivity(), IdentifyActivity.class);
                startActivity(intent);
                builder.dismiss();
            })
            .show();

    }

    public void startAutoScroll() {
        if (handler != null) {
            handler.removeMessages(MSG_START_SCROLL);
            handler.sendEmptyMessageDelayed(MSG_START_SCROLL, VIEWPAGER_SWITCH_DURING);
        }
    }

    public void stopScroll() {
        handler.removeMessages(MSG_START_SCROLL);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clear();
    }

    public void clear() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
