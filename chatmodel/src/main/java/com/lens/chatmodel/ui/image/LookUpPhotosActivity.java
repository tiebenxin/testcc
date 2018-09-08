package com.lens.chatmodel.ui.image;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.lens.chatmodel.R;
import com.lens.chatmodel.api.AlbumApi;
import com.lens.chatmodel.bean.body.ThumbsUpRequestBody;
import com.lens.chatmodel.databinding.ActivityLookUpPhotosBinding;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.utils.AnimationUtility;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.CyptoUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.circle_friend.CommentBean;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleInfo;
import com.lensim.fingerchat.data.me.circle_friend.FxPhotosBean;
import com.lensim.fingerchat.data.me.circle_friend.ThumbsBean;
import com.lensim.fingerchat.data.me.content.StoreManager;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class LookUpPhotosActivity extends FGActivity {

    public static final String FRIEND_CIRCLE_ENTITY_LIST = "FriendCircleEntityList";
    public static final String POSITION = "position";

    private boolean isMyself = false;
    private String urlForCollection = null;
    private final int MENU_ITEM_DELETE = 1630;
    private final int MENU_ITEM_TRANSFER = 1631;
    private final int MENU_ITEM_COLLECT = 1632;
    private final int MENU_ITEM_SAVE = 1633;

    private FxPhotosBean entity;
    private FxPhotosBean currentCircleItem;
    private List<CircleItem> mData = new ArrayList<>();
    private SparseArray<Fragment> fragmentMap = new SparseArray<>();
    private ArrayList<FxPhotosBean> mDataList;
    private boolean hideTitle = false;
    private boolean zamed;
    private int positionInList;
    private int currentFragmentPosition = positionInList;

    private ActivityLookUpPhotosBinding ui;
    private AlbumApi albumApi;
    private CircleItem circleItem;

    private AlbumApi getAlbumApi(){
        return null == albumApi ? new AlbumApi():albumApi;
    }


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_look_up_photos);
        UIHelper
            .setTextSize2(14, ui.mLookUpPhotosContent, ui.mLookUpPhotosZamText, ui.mLookUpPhotosComenText,
                ui.mLookUpPhotosZamCount, ui.mLookUpPhotosCommentCount);

        ui.mLookUpPhotosZam.setOnClickListener(v -> clickZam());
        ui.mLookUpPhotosComment.setOnClickListener(v -> clickCount());
        ui.mLookUpPhotosCount.setOnClickListener(v -> clickCount());

        setSupportActionBar(ui.mLookUpPhotosToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void clickCount() {
        NewComment newComment = new NewComment();
        newComment.setPHO_Serno(entity.getPhotoSerno());
        newComment.setPHO_CreateUserID(entity.getPhotoCreator());
        newComment.setUSR_Name(entity.getPhotoCreator());

        Intent intent = ActivitysRouter.getInstance().invoke(this, ActivityPath.COMMENT_DETAIL_ACTIVITY_PATH);
        if (intent != null) {
            intent.putExtra("newComment", newComment);
            intent.putExtra("photoSerno",entity.getPhotoSerno());
            if (currentCircleItem != null) {
                intent.putExtra("circleItem", currentCircleItem);
            }
            startActivityForResult(intent, 1);
        }
    }

    private void clickZam() {
        showProgress("请稍后...", true);
            if (zamed) {
                getAlbumApi().cancelThumbsUp(entity.getPhotoCreator(), entity.getPhotoSerno(),
                    UserInfoRepository.getUserId(), new FXRxSubscriberHelper<BaseResponse>() {
                        @Override
                        public void _onNext(BaseResponse baseResponse) {
                            dismissProgress();
                            zamed = false;
                            ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.photo_points_like);
                            ui.mLookUpPhotosZamText.setText("点赞");
                            removeZam();
                            //zamCount--;
                            ui.mLookUpPhotosZamCount.setText(String.valueOf(currentCircleItem.getThumbsUps().size()));
                        }
                    });
            } else {
                ThumbsUpRequestBody requestBody = new ThumbsUpRequestBody.Builder()
                    .photoSerno(entity.getPhotoSerno())
                    .photoUserId(entity.getPhotoCreator())
                    .photoUserName(entity.getPhotoCreator())
                    .thumbsUserId(UserInfoRepository.getUserId())
                    .thumbsUserName(CyptoUtils.encrypt(UserInfoRepository.getUsernick())).build();
                getAlbumApi().thumbsUp(requestBody, new FXRxSubscriberHelper<BaseResponse>() {
                    @Override
                    public void _onNext(BaseResponse baseResponse) {
                        dismissProgress();
                        if ("Ok".equals(baseResponse.getMessage())) {
                            zamed = true;
                            ui.mLookUpPhotosCommentImage
                                .setImageResource(R.drawable.cancel_point_praise);
                            ui.mLookUpPhotosZamText.setText("取消");
                            ThumbsBean zam = new ThumbsBean();
                            zam.setThumbsUserId(UserInfoRepository.getUserName());
                            zam.setThumbsUserName(UserInfoRepository.getUsernick());
                            currentCircleItem.getThumbsUps().add(zam);
                            ui.mLookUpPhotosZamCount
                                .setText(String.valueOf(currentCircleItem.getThumbsUps().size()));
                        }else {
                            T.show("点赞失败");
                        }
                    }
                });
            }


    }


    @Override
    public void initData(Bundle savedInstanceState) {
        mDataList = (ArrayList<FxPhotosBean>) getIntent().getSerializableExtra(FRIEND_CIRCLE_ENTITY_LIST);
        positionInList = getIntent().getIntExtra(POSITION, 0);
        entity = mDataList.get(positionInList);
        prepareDataList();
        initPager();
        refresh(positionInList);
    }


    public void prepareDataList() {
        if (null == mDataList || mDataList.isEmpty()) {
            return;
        }
        CircleItem tempCircleItem;
        for (int a = 0; a < mDataList.size(); a++) {
            FxPhotosBean entity = mDataList.get(a);

            if (entity.getPhotoFilenames().contains(".mp4")) {
                tempCircleItem = new CircleItem();
                tempCircleItem.id = entity.getPhotoSerno();
                tempCircleItem.type = CircleItem.TYPE_VIDEO;
                tempCircleItem.parentinList = a;
                tempCircleItem.userid = entity.getPhotoCreator();
                tempCircleItem.username = entity.getPhotoCreator();
                tempCircleItem.content = entity.getPhotoContent();
                tempCircleItem.createTime = TimeUtils.timeFormat(entity.getCreateDatetime());
                if (entity.getPhotoCreator().equals(UserInfoRepository.getUserName())) {
                    tempCircleItem.headUrl = Route.getAvatarPath(UserInfoRepository.getUserName());
                } else {
                    tempCircleItem.headUrl = Route.getAvatarPath(entity.getPhotoCreator());
                }
                //tempCircleItem.videoUrl = createVideoUrl(entity.getPhotoFilenames(), entity.getPhotoUrl());
                tempCircleItem.videoUrl = entity.getPhotoUrl().split(",")[0];
                mData.add(tempCircleItem);
            } else {
                List<String> imgList = createPhotos(entity.getPhotoFilenames(), entity.getPhotoUrl());
                if (imgList != null) {
                    for (int b = 0; b < imgList.size(); b++) {
                        tempCircleItem = new CircleItem();
                        tempCircleItem.id = entity.getPhotoSerno();
                        tempCircleItem.type = CircleItem.TYPE_IMG;
                        tempCircleItem.parentinList = a;
                        tempCircleItem.childInList = b;
                        tempCircleItem.childCount = imgList.size();
                        tempCircleItem.imgUrl = imgList.get(b);
                        tempCircleItem.userid = entity.getPhotoCreator();
                        tempCircleItem.username = entity.getPhotoCreator();
                        tempCircleItem.content = entity.getPhotoContent();
                        tempCircleItem.createTime = TimeUtils.timeFormat(entity.getCreateDatetime());
                        if (entity.getPhotoCreator().equals(UserInfoRepository.getUserName())) {
                            tempCircleItem.headUrl = Route.getAvatarPath(UserInfoRepository.getUserName());
                        } else {
                            tempCircleItem.headUrl = Route.getAvatarPath(entity.getPhotoCreator());
                        }
                        tempCircleItem.photos = createPhotos(entity.getPhotoFilenames(), entity.getPhotoUrl());
                        mData.add(tempCircleItem);
                    }
                }
            }
        }
        for (int i = 0, len = mData.size(); i < len; i++) {
            if (positionInList == mData.get(i).parentinList) {
                positionInList = i;
                setCurrentCircleItem(positionInList);
                break;
            }
        }
    }


    private List<String> createPhotos(String namestr, String path) {
        if (StringUtils.isEmpty(namestr)) {
            return null;
        }
        List<String> photos = new ArrayList<>();
        String[] paths = path.split(",");
        for (int i = 0; i < paths.length; i++) {
            /*String url = path.replace("C:\\HnlensWeb\\", Route.Host) *//*+ names[i]*//*;
            url = url.replace("\\", "/");*/
            String url = paths[i];
                photos.add(url);
        }
        return photos;
    }

    private String createVideoUrl(String namestr, String path) {
        if (StringUtils.isEmpty(namestr)) {
            return null;
        }
        String s = "";
        String[] names = namestr.split(";");
        s = path.replace("C:\\HnlensWeb\\", Route.Host) + names[0];
        return s.replace("\\", "/");
    }

    private void initPager() {
        ui.mLookUpPhotosPager.setAdapter(new ImagePagerAdapter(getSupportFragmentManager()));
        ui.mLookUpPhotosPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                refresh(position);
                circleItem = mData.get(position);
                setHideTitle();
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {
            }
        });
        ui.mLookUpPhotosPager.setCurrentItem(positionInList);
    }


    public void setCurrentCircleItem(int position) {
        currentCircleItem = mDataList.get(position);
        VideoFragment.isViewPagerSelected = currentCircleItem.getPhotoFilenames().contains(".mp4");
    }



    public void refresh(int position) {
        setCurrentCircleItem(position);
        entity = mDataList.get(mData.get(position).parentinList);
        if (mData.get(position).childCount > 1) {
            onChildPageChange(position);
        } else {
            onParentPageChange();
        }
        zamed = false;
        getAlbumApi().getSubject(entity.getPhotoSerno(),
            new FXRxSubscriberHelper<BaseResponse<FriendCircleInfo>>() {
                @Override
                public void _onNext(BaseResponse<FriendCircleInfo> friendCircleInfoBaseResponse) {
                    if ("Ok".equals(friendCircleInfoBaseResponse.getMessage())){
                        FxPhotosBean photosBeanList = friendCircleInfoBaseResponse.getContent().getFxNewPhotos();
                        List<ThumbsBean> thumbsBeans = photosBeanList.getThumbsUps();
                        List<CommentBean> commentBeans = photosBeanList.getComments();
                        if (!zamed){
                            for (ThumbsBean thumbsBean :thumbsBeans){
                                zamed = thumbsBean.getThumbsUserId().equals(UserInfoRepository.getUserName().toLowerCase());
                            }
                        }
                        if (zamed) {
                            ui.mLookUpPhotosZamText.setText("取消");
                            ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.cancel_point_praise);
                        } else {
                            ui.mLookUpPhotosZamText.setText("点赞");
                            ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.photo_points_like);
                        }
                        ui.mLookUpPhotosCommentCount.setText(String.valueOf(commentBeans.size()));
                        ui.mLookUpPhotosZamCount.setText(String.valueOf(thumbsBeans.size()));

                    }else {
                        T.show(friendCircleInfoBaseResponse.getMessage());
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                }
            });
        String text = CyptoConvertUtils.decryptString(entity.getPhotoContent());
        if (TextUtils.isEmpty(text)) {
            ui.mLookUpPhotosContent.setVisibility(View.GONE);
        } else {
            ui.mLookUpPhotosContent.setVisibility(View.VISIBLE);
            ui.mLookUpPhotosContent.setText(text);
        }
    }


    public void onChildPageChange(int position) {
        getSupportActionBar().setSubtitle(
            mData.get(position).childInList + 1 + "/" + mData.get(position).childCount);
    }

    public void onParentPageChange() {
        getSupportActionBar().setTitle(StringUtils.parseDbTime(TimeUtils.timeFormat(entity.getCreateDatetime())));
        String count = entity.getPhotoFileNum()+"";
        if (!TextUtils.isEmpty(count)) {
            getSupportActionBar().setSubtitle(Integer.parseInt(count.trim()) > 1 ? "1/" + count : "");
        }
    }

    private void removeZam() {
        List<ThumbsBean> favorters = currentCircleItem.getThumbsUps();
        ThumbsBean zam = null;
        for (ThumbsBean z : favorters) {
            if (z.getThumbsUserId().equals(UserInfoRepository.getUserName())) {
                zam = z;
                break;
            }
        }
        if (zam != null) {
            favorters.remove(zam);
        }
    }

    public void setHideTitle() {
        if (!hideTitle) {
            onPhotoTap();
        }
    }

    public void onPhotoTap() {
        hideTitle = !hideTitle;
        if (hideTitle) {
            TDevice.setWindowStatusBarColor(this, R.color.black);
            AnimationUtility.hideTileAndBottm(ui.mLookUpPhotosToolbar, ui.mLookUpPhotosBottom);
        } else {
            AnimationUtility.showTileAndBottm(ui.mLookUpPhotosToolbar, ui.mLookUpPhotosBottom);
            TDevice.setWindowStatusBarColor(this, R.color.black_33);
        }
    }


    public void deleteRequest() {
        final String pho_serno = entity.getPhotoSerno();

        getAlbumApi().deletePhoto(pho_serno, new FXRxSubscriberHelper<BaseResponse>() {
            @Override
            public void _onNext(BaseResponse baseResponse) {
                T.show("删除成功");
                setResultToActiviy();
            }
        });

    }

    public void deleteDialog() {
        final NiftyDialogBuilder builder = NiftyDialogBuilder.getInstance(this);
        builder.withTitle("提示")
            .withMessage("确定删除吗")
            .withButton1Text("取消")
            .withButton2Text("删除")
            .setButton1Click(v -> builder.dismiss())
            .setButton2Click(v -> {
                builder.dismiss();
                deleteRequest();
            }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strData = data.getStringExtra("circleitem");
                ui.mLookUpPhotosCommentCount.setText(strData.split(",")[0]);
                ui.mLookUpPhotosZamCount.setText(strData.split(",")[1]);
                if (data.getBooleanExtra("isDeleteCircle", false)) {
                    setResultToActiviy();
                }
            }
        }
    }

    public void setResultToActiviy() {
        Intent intent = new Intent();
        intent.putExtra("delete_circle_id", entity.getPhotoSerno());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_photos, menu);
        generateURL();
        if (isMyself) {
            menu.add(Menu.NONE, MENU_ITEM_DELETE, Menu.NONE, getString(R.string.pop_delete_collect));
        }
        menu.add(Menu.NONE, MENU_ITEM_TRANSFER, Menu.NONE,
            getString(R.string.dialog_menu_send_to_friend));
        menu.add(Menu.NONE, MENU_ITEM_COLLECT, Menu.NONE, getString(R.string.pop_menu_collect));
        menu.add(Menu.NONE, MENU_ITEM_SAVE, Menu.NONE, getString(R.string.pop_menu_copy_to_local));
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_DELETE:
                deleteDialog();
                break;
            case MENU_ITEM_TRANSFER:
                transfer();
                break;
            case MENU_ITEM_COLLECT:
                try {
                    store();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case MENU_ITEM_SAVE:

                if (fragmentMap.get(currentFragmentPosition-1) instanceof LookUpPhototsFragment) {
                    ((LookUpPhototsFragment) fragmentMap.get(currentFragmentPosition-1)).save(urlForCollection);
                }else{
                    T.showShort(LookUpPhotosActivity.this, "视频不可复制");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void transfer() {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        int mType = (currentCircleItem.getPhotoFilenames().contains(".mp4")) ? 3 : 1;
        if (mType == 3 ){
            String imageUrl = currentCircleItem.getPhotoUrl().split(",")[1];
            try {
                jsonObject.put("ImageUrl",imageUrl);
                jsonObject.put("ImageSize","");
                jsonObject.put("VideoUrl",mData.get(positionInList).videoUrl);
                jsonObject.put("timeLength",0);
                jsonObject1.put("body",jsonObject.toString());
                jsonObject1.put("secret",0);
                jsonObject1.put("bubbleWidth",0);
                jsonObject1.put("bubbleHeight",0);
                jsonObject1.put("timeLength",0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            try {
                jsonObject.put("OriginalUrl",circleItem.imgUrl);
                jsonObject.put("OriginalSzie","");
                jsonObject.put("ThumbnailUrl",circleItem.imgUrl);
                jsonObject.put("ThumbnailSize","");
                jsonObject1.put("body",jsonObject.toString());
                jsonObject1.put("secret",0);
                jsonObject1.put("bubbleWidth",0);
                jsonObject1.put("bubbleHeight",0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //int mType = (currentCircleItem.getType() == CircleItem.TYPE_VIDEO) ? MessageConstants.MSG_TYPE_VIDEO : MessageConstants.MSG_TYPE_PIC;
        Intent intent = TransforMsgActivity.newPureIntent(this, jsonObject1.toString(), mType,1,"");
        LookUpPhotosActivity.this.startActivity(intent);
    }


    public void generateURL() {
        isMyself = entity.getPhotoCreator().equals(UserInfoRepository.getUserName());
        urlForCollection = entity.getPhotoUrl() + entity.getPhotoFilenames().replace(";", "");
        urlForCollection = urlForCollection.replace("C:\\HnlensWeb\\", Route.Host);
        urlForCollection = urlForCollection.replace("\\", "/").trim();
    }

    public void store() throws JSONException{
       // showToast("此功能未开放");
        int mType = 0;
        JSONObject jsonObject = new JSONObject();
        if (!currentCircleItem.getPhotoFilenames().contains(".mp4")) {
            mType = 2;
            //JSONObject json1 = new JSONObject(json.getString("body"));
            jsonObject.put("OriginalSzie", "");
            jsonObject.put("userHeadImageStr", currentCircleItem.getUserImage());
            jsonObject.put("OriginalUrl", circleItem.imgUrl);
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "2");
            jsonObject.put("type",  "0");
            jsonObject.put("ThumbnailUrl", "");
            jsonObject.put("ThumbnailSize", "");
            jsonObject.put("recordTime", TimeUtils.getDate());
        } else {
            mType = 4;
            String imageUrl = currentCircleItem.getPhotoUrl().split(",")[1];
            jsonObject.put("ImageSize", "");
            jsonObject.put("userHeadImageStr", currentCircleItem.getUserImage());
            jsonObject.put("signContent", "");
            jsonObject.put("messageType", "4");
            jsonObject.put("ImageUrl", imageUrl);
            jsonObject.put("type", "0");
            jsonObject.put("userName", currentCircleItem.getPhotoCreator());
            jsonObject.put("VideoUrl", mData.get(positionInList).videoUrl);
            jsonObject.put("recordTime", TimeUtils.getDate());
        }

//        int mType = (currentCircleItem.getType() == CircleItem.TYPE_VIDEO) ? MessageConstants.MSG_TYPE_VIDEO : MessageConstants.MSG_TYPE_PIC;
        StoreManager.getInstance().storeCircleImageVideo(currentCircleItem.getPhotoSerno(), currentCircleItem.getPhotoCreator(), currentCircleItem.getPhotoCreator(), jsonObject.toString(), currentCircleItem.getUserImage(), mType);
    }


    private class ImagePagerAdapter extends FragmentPagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            CircleItem item = mData.get(position);
            fragment = fragmentMap.get(position);
            currentFragmentPosition = position;
            if (fragment == null) {
                if (item.type.equals(CircleItem.TYPE_VIDEO)) {
                    //Log.e("ttt--传的视频-",item.videoUrl);
                    fragment = VideoFragment.newInstance(item.videoUrl);
                } else {
                    fragment = LookUpPhototsFragment.newInstance(item.imgUrl);
                }
                fragmentMap.put(position, fragment);
            }
            return fragment;
        }

        private AnimationRect getrec(String s, ArrayList<AnimationRect> rectList) {
            if (rectList == null) {
                return null;
            }
            for (AnimationRect rect : rectList) {
                if (s.equals(rect.getUri())) {
                    return rect;
                }
            }
            return null;
        }

        //when activity is recycled, ViewPager will reuse fragment by theirs name, so
        //getItem wont be called, but we need fragmentMap to animate close operation
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof Fragment) {
                fragmentMap.put(position, (Fragment) object);
            }
        }

        @Override
        public int getCount() {
            return mData.size();
        }
    }

}
