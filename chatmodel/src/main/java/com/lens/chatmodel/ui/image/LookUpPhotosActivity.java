package com.lens.chatmodel.ui.image;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lens.chatmodel.R;
import com.lens.chatmodel.databinding.ActivityLookUpPhotosBinding;
import com.lensim.fingerchat.commons.base.FGActivity;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.helper.AnimationRect;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.router.ActivitysRouter;
import com.lensim.fingerchat.commons.utils.AnimationUtility;
import com.lensim.fingerchat.commons.utils.CyptoConvertUtils;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TDevice;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.dialog.nifty_dialog.NiftyDialogBuilder;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.CircleItem;
import com.lensim.fingerchat.data.me.CommentEntity;
import com.lensim.fingerchat.data.me.NewComment;
import com.lensim.fingerchat.data.me.ZambiaEntity;
import com.lensim.fingerchat.data.me.circle_friend.ContentEntity;
import com.lensim.fingerchat.data.me.circle_friend.FriendCircleEntity;
import java.util.ArrayList;
import java.util.List;

public class LookUpPhotosActivity extends FGActivity {

    public static final String FRIEND_CIRCLE_ENTITY_LIST = "FriendCircleEntityList";
    public static final String POSITION = "position";

    private boolean isMyself = false;
    private String urlForCollection = null;
    private final int MENU_ITEM_DELETE = 1630;
    private final int MENU_ITEM_TRANSFER = 1631;
    private final int MENU_ITEM_COLLECT = 1632;
    private final int MENU_ITEM_SAVE = 1633;

    private FriendCircleEntity entity;
    private CircleItem currentCircleItem;
    private List<CircleItem> mData = new ArrayList<>();
    private SparseArray<Fragment> fragmentMap = new SparseArray<>();
    private ArrayList<FriendCircleEntity> mDataList;
    private boolean hideTitle = false;
    private boolean zamed;
    private int positionInList;
    private int currentFragmentPosition = positionInList;

    private ActivityLookUpPhotosBinding ui;


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
        newComment.setPHO_Serno(entity.getPHO_Serno());
        newComment.setPHO_CreateUserID(entity.getPHO_CreateUserID());
        newComment.setUSR_Name(entity.getUSR_Name());
        Intent intent = ActivitysRouter.getInstance().invoke(this, ActivityPath.COMMENT_DETAIL_ACTIVITY_PATH);
        if (intent != null) {
            intent.putExtra("newComment", newComment);
            if (currentCircleItem != null) {
                intent.putExtra("circleItem", currentCircleItem);
            }
            startActivityForResult(intent, 1);
        }
    }

    private void clickZam() {
        showProgress("请稍后...", true);
            if (zamed) {
                Http.cancelLike(UserInfoRepository.getUserName().toLowerCase(), entity.getPHO_Serno(), entity.getPHO_CreateUserID())
                    .compose(RxSchedulers.io_main())
                    .subscribe(
                        string -> {
//                            if ("OK".equals(string.string())) {
                                dismissProgress();
                                zamed = false;
                                ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.photo_points_like);
                                ui.mLookUpPhotosZamText.setText("点赞");
                                removeZam();
                                //zamCount--;
                                ui.mLookUpPhotosZamCount.setText(String.valueOf(currentCircleItem.favorters.size()));
//                            }
                        },
                        throwable -> {
                            T.show("取消失败");
                            dismissProgress();
                        });
            } else {
                Http.likePhotos(UserInfoRepository.getUserName().toLowerCase(),
                    UserInfoRepository.getUsernick(), entity.getPHO_Serno(),
                    entity.getPHO_CreateUserID(), entity.getUSR_Name())
                    .compose(RxSchedulers.io_main())
                    .subscribe(responseBody -> {
                            if ("OK".equals(responseBody.string())) {
                                dismissProgress();
                                zamed = true;
                                ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.cancel_point_praise);
                                ui.mLookUpPhotosZamText.setText("取消");
                                //zamCount++;
                                ZambiaEntity zam = new ZambiaEntity();
                                zam.PHC_CommentUserid = UserInfoRepository.getUserName();
                                zam.PHC_CommentUsername = UserInfoRepository.getUsernick();
                                currentCircleItem.favorters.add(zam);
                                ui.mLookUpPhotosZamCount.setText(String.valueOf(currentCircleItem.favorters.size()));
                            } else {
                                T.show("点赞失败");
                            }
                        },
                        throwable -> {
                            T.show("点赞失败");
                            dismissProgress();
                        });
            }
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        mDataList = getIntent().getParcelableArrayListExtra(FRIEND_CIRCLE_ENTITY_LIST);
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
            FriendCircleEntity entity = mDataList.get(a);

            if (entity.getPHO_ImageName().contains(".mp4")) {
                tempCircleItem = new CircleItem();
                tempCircleItem.id = entity.getPHO_Serno();
                tempCircleItem.type = CircleItem.TYPE_VIDEO;
                tempCircleItem.parentinList = a;
                tempCircleItem.userid = entity.getPHO_CreateUserID();
                tempCircleItem.username = entity.getUSR_Name();
                tempCircleItem.content = entity.getPHO_Content();
                tempCircleItem.createTime = entity.getPHO_CreateDT();
                if (entity.getPHO_CreateUserID().equals(UserInfoRepository.getUserName())) {
                    tempCircleItem.headUrl = Route.getAvatarPath(UserInfoRepository.getUserName());
                } else {
                    tempCircleItem.headUrl = Route.getAvatarPath(entity.getPHO_CreateUserID());
                }
                tempCircleItem.videoUrl = createVideoUrl(entity.getPHO_ImageName(), entity.getPHO_ImagePath());
                mData.add(tempCircleItem);
            } else {
                List<String> imgList = createPhotos(entity.getPHO_ImageName(), entity.getPHO_ImagePath());
                if (imgList != null) {
                    for (int b = 0; b < imgList.size(); b++) {
                        tempCircleItem = new CircleItem();
                        tempCircleItem.id = entity.getPHO_Serno();
                        tempCircleItem.type = CircleItem.TYPE_IMG;
                        tempCircleItem.parentinList = a;
                        tempCircleItem.childInList = b;
                        tempCircleItem.childCount = imgList.size();
                        tempCircleItem.imgUrl = imgList.get(b);
                        tempCircleItem.userid = entity.getPHO_CreateUserID();
                        tempCircleItem.username = entity.getUSR_Name();
                        tempCircleItem.content = entity.getPHO_Content();
                        tempCircleItem.createTime = entity.getPHO_CreateDT();
                        if (entity.getPHO_CreateUserID().equals(UserInfoRepository.getUserName())) {
                            tempCircleItem.headUrl = Route.getAvatarPath(UserInfoRepository.getUserName());
                        } else {
                            tempCircleItem.headUrl = Route.getAvatarPath(entity.getPHO_CreateUserID());
                        }
                        tempCircleItem.photos = createPhotos(entity.getPHO_ImageName(), entity.getPHO_ImagePath());
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
        String[] names = namestr.split(";");
        for (int i = 0; i < names.length; i++) {
            String url = path.replace("C:\\HnlensWeb\\", Route.Host) + names[i];
            url = url.replace("\\", "/");
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
                setHideTitle();
            }

            @Override
            public void onPageScrollStateChanged(int scrollState) {
            }
        });
        ui.mLookUpPhotosPager.setCurrentItem(positionInList);
    }


    public void setCurrentCircleItem(int position) {
        currentCircleItem = mData.get(position);
        VideoFragment.isViewPagerSelected = currentCircleItem.type.equals(CircleItem.TYPE_VIDEO);
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
        Http.getPhotoItemById(entity.getPHO_Serno(), UserInfoRepository.getUserName().toLowerCase())
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                    if (stringRetObjectResponse.retCode == 1) {
                        Gson gson = new Gson();
                        List<CommentEntity> list = gson.fromJson(stringRetObjectResponse.retData, new TypeToken<List<CommentEntity>>() {}.getType());
                        List<ZambiaEntity> zams = new ArrayList<>();
                        List<ContentEntity> comments = new ArrayList<>();

                        if (list == null) return;
                        for (CommentEntity commentEntity : list) {
                            if (commentEntity.getPHC_Zambia().equals("1")) {
                                ZambiaEntity zam = new ZambiaEntity();
                                zam.PHC_CommentUserid = commentEntity.getPHC_CommentUserid();
                                zam.PHC_CommentUsername = commentEntity.getPHC_CommentUsername();
                                zams.add(zam);
                                if (!zamed) {
                                zamed = commentEntity.getPHC_CommentUserid().equals(UserInfoRepository.getUserName().toLowerCase());
                                }
                            } else {
                                // commentCount++;
                                ContentEntity comment = new ContentEntity();
                                comment.setPHC_Serno(commentEntity.getPHC_Serno());
                                comment.setPHC_Content(commentEntity.getPHC_Content());
                                comment.setPHC_CommentUserid(commentEntity.getPHC_CommentUserid());
                                comment.setPHC_CommentUsername(commentEntity.getPHC_CommentUsername());
                                comment.setPHC_SecondUserid(commentEntity.getPHC_SecondUserid());
                                comment.setPHC_SecondUsername(commentEntity.getPHC_SecondUsername());
                                comments.add(comment);
                            }
                        }
                        currentCircleItem.favorters = zams;
                        currentCircleItem.comments = comments;
                        if (zamed) {
                            ui.mLookUpPhotosZamText.setText("取消");
                            ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.cancel_point_praise);
                        } else {
                            ui.mLookUpPhotosZamText.setText("点赞");
                            ui.mLookUpPhotosCommentImage.setImageResource(R.drawable.photo_points_like);
                        }

                        ui.mLookUpPhotosCommentCount.setText(String.valueOf(comments.size()));
                        ui.mLookUpPhotosZamCount.setText(String.valueOf(zams.size()));
                    } else {
                        T.show(stringRetObjectResponse.retMsg);
                    }
                },
                throwable -> {
                    T.show("点赞失败");
                    Log.e("getPhotoItemById",throwable.getMessage());

                });
        String text = CyptoConvertUtils.decryptString(entity.getPHO_Content());
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
        getSupportActionBar().setTitle(StringUtils.parseDbTime(entity.getPHO_CreateDT()));
        String count = entity.getPHO_ImageNUM();
        if (!TextUtils.isEmpty(count)) {
            getSupportActionBar().setSubtitle(Integer.parseInt(count.trim()) > 1 ? "1/" + count : "");
        }
    }

    private void removeZam() {
        List<ZambiaEntity> favorters = currentCircleItem.favorters;
        ZambiaEntity zam = null;
        for (ZambiaEntity z : favorters) {
            if (z.PHC_CommentUserid.equals(UserInfoRepository.getUserName())) {
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
        final String pho_serno = entity.getPHO_Serno();
        Http.deleteCircle(pho_serno, UserInfoRepository.getUserName())
            .compose(RxSchedulers.io_main())
            .subscribe(
                stringRetObjectResponse -> {
                    if (1 == stringRetObjectResponse.retCode){
                        setResultToActiviy();
                    } else {
                        T.show("删除失败");
                    }
                },
                throwable -> T.show("删除失败"));
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
                currentCircleItem = data.getParcelableExtra("circleitem");
                ui.mLookUpPhotosCommentCount.setText(String.valueOf(currentCircleItem.comments.size()));
                ui.mLookUpPhotosZamCount.setText(String.valueOf(currentCircleItem.favorters.size()));
                if (data.getBooleanExtra("isDeleteCircle", false)) {
                    setResultToActiviy();
                }
            }
        }
    }

    public void setResultToActiviy() {
        Intent intent = new Intent();
        intent.putExtra("delete_circle_id", entity.getPHO_Serno());
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
                store();
                break;
            case MENU_ITEM_SAVE:
                if (fragmentMap.get(currentFragmentPosition) instanceof LookUpPhototsFragment) {
                    ((LookUpPhototsFragment) fragmentMap.get(currentFragmentPosition)).save(urlForCollection);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void transfer() {
        generateURL();
//        int mType = (currentCircleItem.getType() == CircleItem.TYPE_VIDEO) ? MessageConstants.MSG_TYPE_VIDEO : MessageConstants.MSG_TYPE_PIC;
//        Intent intent = TransforMsgActivity.newPureIntent(this, urlForCollection, mType);
//        LookUpPhotosActivity.this.startActivity(intent);
    }


    public void generateURL() {
        isMyself = entity.getPHO_CreateUserID().equals(UserInfoRepository.getUserName());
        urlForCollection = entity.getPHO_ImagePath() + entity.getPHO_ImageName().replace(";", "");
        urlForCollection = urlForCollection.replace("C:\\HnlensWeb\\", Route.Host);
        urlForCollection = urlForCollection.replace("\\", "/").trim();
    }

    public void store() {
//        generateURL();
//        int mType = (currentCircleItem.getType() == CircleItem.TYPE_VIDEO) ? MessageConstants.MSG_TYPE_VIDEO : MessageConstants.MSG_TYPE_PIC;
//        StoreManager.getInstance().storeCircleImageVideo(currentCircleItem.id, currentCircleItem.userid, urlForCollection, currentCircleItem.headUrl, mType);
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
