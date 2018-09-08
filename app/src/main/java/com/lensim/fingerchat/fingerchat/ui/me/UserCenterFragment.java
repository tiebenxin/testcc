package com.lensim.fingerchat.fingerchat.ui.me;

import static com.lensim.fingerchat.commons.utils.UIHelper.MIN_TEXT_SIZE;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.base.BaseUserInfoActivity;
import com.lensim.fingerchat.commons.base.BaseFragment;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.utils.ImageLoader;
import com.lensim.fingerchat.commons.utils.SPHelper;
import com.lensim.fingerchat.components.dialog.DialogUtil;
import com.lensim.fingerchat.data.login.UserInfo;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.api.CirclesFriendsApi;
import com.lensim.fingerchat.fingerchat.component.databind.ClickHandle;
import com.lensim.fingerchat.fingerchat.databinding.FragmentUserCenterBinding;
import com.lensim.fingerchat.fingerchat.interf.IMeTitleListener;
import com.lensim.fingerchat.fingerchat.model.bean.PhotoBean;
import com.lensim.fingerchat.fingerchat.model.result.GetPhotoResult;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.CircleFriendsActivity;
import com.lensim.fingerchat.fingerchat.ui.me.circle_friends.UserInfoActivity;
import com.lensim.fingerchat.fingerchat.ui.me.collection.CollectionActivity;
import com.lensim.fingerchat.fingerchat.ui.me.photo.PhotosActivity;

/**
 * 个人中心
 * Created by zm on 2018/5/24.
 */
public class UserCenterFragment extends BaseFragment implements ClickHandle {

    private FragmentUserCenterBinding userCenterBinding;
    private ControllerMeTitle viewMeTitle;

    private UserInfo userInfo; // 用户信息
    private CirclesFriendsApi photoApi;

    /**
     * 静态工厂方法提供实例
     */
    public static UserCenterFragment newInstance() {
        return new UserCenterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View rooView = inflater.inflate(R.layout.fragment_user_center, null);
        userCenterBinding = DataBindingUtil.bind(rooView);
        userCenterBinding.setClick(this);
        return rooView;
    }

    @Override
    protected void initView() {
        userCenterBinding.menuNetdisk.setVisibility(View.GONE);
        userInfo = UserInfoRepository.getInstance().getUserInfo();
        if (userInfo != null) {
            viewMeTitle = new ControllerMeTitle(userCenterBinding.viewMeTitle);
            viewMeTitle.initUserInfo(userInfo);
            viewMeTitle.setOnClickListener(new IMeTitleListener() {
                @Override
                public void clickAvatar() {

                    // 跳转至个人信息页面
                    Bundle bundle = new Bundle();
                    bundle.putString(ActivityPath.USER_ID,
                        ((BaseUserInfoActivity) getActivity()).getUserId());
                    toActivityForResult(UserInfoActivity.class, bundle, 1);
                }

                @Override
                public void clickCode() {
                    DialogUtil
                        .getUserInfoDialog(getActivity(), R.style.MyDialog,
                            ((BaseUserInfoActivity) getActivity()).getUserId(),
                            ((BaseUserInfoActivity) getActivity()).getUserAvatar(),
                            ((BaseUserInfoActivity) getActivity()).getUserNick())
                        .show();
                }
            });
        }

        int factor = SPHelper.getInt("font_size", 1) * 2;
        if (factor <= 2) {
            factor = 2;
        } else if (factor > 6) {
            factor = 6;
        }

        userCenterBinding.menuMoments.setTextSize(factor + MIN_TEXT_SIZE);
        userCenterBinding.menuCollection.setTextSize(factor + MIN_TEXT_SIZE);
        userCenterBinding.menuGallery.setTextSize(factor + MIN_TEXT_SIZE);


    }

    @Override
    protected void initData() {

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUnreadPhoto();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu_moments:
                // 朋友圈
                toActivity(CircleFriendsActivity.class);
                break;
            case R.id.menu_collection:
                //收藏
                //showToast("此功能未开放");
                toActivity(CollectionActivity.class);
                break;
            case R.id.menu_gallery:
                // 相册
                Bundle bundle = new Bundle();
                bundle.putString(ActivityPath.USER_ID, UserInfoRepository.getUserName());
                toActivity(PhotosActivity.class, bundle);
                break;
            case R.id.menu_netdisk:
                // 个人网盘
                break;
        }
    }

    public CirclesFriendsApi getPhotoApi() {
        return photoApi == null ? new CirclesFriendsApi() : photoApi;
    }

    @Override
    public void notifyResumeData() {
        userInfo = UserInfoRepository.getInstance().getUserInfo();
        if (viewMeTitle != null && userInfo != null) {
            viewMeTitle.initUserInfo(userInfo);
        }
    }


    /**
     * 更新未读朋友圈
     */
    public void updateUnreadPhoto() {
        getPhotoApi().getUnreadPhoto(userInfo.getUserid(), new FXRxSubscriberHelper<GetPhotoResult>() {
            @Override
            public void _onNext(GetPhotoResult result) {
                if (result.getContent() != null
                    && result.getContent().getFxNewPhotos() != null
                    && !result.getContent().getFxNewPhotos().isEmpty()) {
                    PhotoBean photoBean = result.getContent().getFxNewPhotos().get(0); // 取第一条未读数据
                    userCenterBinding.menuMoments.setPointVisibility(true);
                    userCenterBinding.menuMoments.getRightImageView().setVisibility(View.VISIBLE);
                    ImageLoader.loadImage( photoBean.getUserImage(),userCenterBinding.menuMoments.getRightImageView());
                } else {
                    userCenterBinding.menuMoments.setPointVisibility(false);
                    userCenterBinding.menuMoments.getRightImageView().setVisibility(View.INVISIBLE);
                }
            }

        });
    }
}
