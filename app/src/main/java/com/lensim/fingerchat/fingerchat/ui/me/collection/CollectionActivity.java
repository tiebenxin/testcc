package com.lensim.fingerchat.fingerchat.ui.me.collection;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.annotation.Path;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lens.chatmodel.ChatEnum.ETransforType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.interf.IEventClickListener;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.message.TransferDialog;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.components.springview.container.DefaultFooter;
import com.lensim.fingerchat.components.springview.container.DefaultHeader;
import com.lensim.fingerchat.components.springview.widget.SpringView;
import com.lensim.fingerchat.components.widget.circle_friends.CollectDialog;
import com.lensim.fingerchat.data.Http;
import com.lensim.fingerchat.data.RxSchedulers;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCollectionBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.ContentFactory;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.NoteContent;

import java.util.ArrayList;
import java.util.List;


/**
 * 一进来网络加戴 ——再缓存本地
 */
@Path(ActivityPath.COLLECTION_ACTIVITY_PATH)
public class CollectionActivity extends BaseActivity implements
    CollectDialog.OnItemClickListener,
    IEventClickListener {

    public final static String ACTIVITY = "activity";
    public final static String USERI_D = "USERID";

    public final static int REQUEST_FOR_NOTE = 645;
    private final int PAGE_SIZE = 10;
    private int PAGE_NUM = 0;
    private LinearLayoutManager layoutManager;
    private CollectFrameAdapter mAdapter;
    private List<FavJson> items;
    private String activity, userId;
    ActivityCollectionBinding ui;
    private boolean isFromChat;
    private String transforContent;
    private FavJson transforFavJson;


    @Override
    public void initView() {
        ui = DataBindingUtil.setContentView(this, R.layout.activity_collection);
        ui.collectionToolbar
            .setTitleText("我的收藏 ")
            .setBtSearchDrawable(0, v -> {
                Intent intent = new Intent(this, SearchNoteActivity.class);
                startActivityForResult(intent, SearchNoteActivity.REQUEST_FOR_SEARCH);
            }).setConfirmBt("笔记", v -> {
            Intent intent = new Intent(this, NoteActivity.class);
            startActivityForResult(intent, CollectionActivity.REQUEST_FOR_NOTE);
        });
        initBackButton(ui.collectionToolbar, true);

        Intent intent = getIntent();
        activity = intent.getStringExtra(ACTIVITY);
        userId = intent.getStringExtra(USERI_D);
        isFromChat =
            !TextUtils.isEmpty(activity) && activity.equals(ChatActivity.class.getSimpleName());

        initAdapter();
        initSpringView();
        loadNetData();
    }

    private void initAdapter() {
        mAdapter = new CollectFrameAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ui.recyclerViewCollect.setLayoutManager(layoutManager);
        ui.recyclerViewCollect.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener((v, position) -> onItemClick(position));

        mAdapter.setOnItemLongClickListener((item, position) -> {
            Content content = ContentFactory.createContent(item.getFavType(), item.getFavContent());
            CollectDialog dialog = new CollectDialog(this, false, true, "删除");
            dialog.setCanceledOnTouchOutside(true);
            dialog.show();
            dialog.setDeleteIndex(position);
            dialog.setCollectItemClickListener(this);
            if (content instanceof NoteContent) {
                dialog.hideTrans();
            }
        });
    }


    private void onItemClick(int position) {
        String type = mAdapter.getItem(position).getFavType();
        String des = mAdapter.getItem(position).getFavDes();
        Content content = ContentFactory
            .createContent(type, mAdapter.getItem(position).getFavContent());
        String text = content.getText();
        boolean isNote = content.isNote();

        if (isNote) {
            if (!isFromChat) {
                NoteActivity.openActivity(this, CollectionActivity.REQUEST_FOR_NOTE,
                    text, mAdapter.getItem(position).getFavId());
            } else {
                T.showShort(ContextHelper.getContext(), "收藏笔记不能转发");
            }
        } else {
            if (!isFromChat) {
                openActivity(position, Integer.parseInt(type), text, mAdapter.getItem(position),
                    des);
            } else {
                forwardItemInChat(position);
            }
        }
    }


    private void openActivity(int position, int type, String data, FavJson item, String des) {
        Intent intent = new Intent(this, CollectionDetailActivity.class);
        intent.putExtra(CollectionDetailActivity.ITEM_POSITION, position);
        intent.putExtra(CollectionDetailActivity.ITEM_TYPE, type);
        intent.putExtra(CollectionDetailActivity.ITEM_DATA, data);
        intent.putExtra(CollectionDetailActivity.UNIQUE_ID, item.getFavId());
        intent.putExtra(CollectionDetailActivity.AVATAR_URL, item.getFavCreaterAvatar());
        intent.putExtra(CollectionDetailActivity.NAME, item.getProviderNick());
        intent.putExtra(CollectionDetailActivity.CREATE_TIME,
            TimeUtils.getTimeStamp(item.getFavTime()));
        intent.putExtra(CollectionDetailActivity.DES, des);
        startActivityForResult(intent, CollectionDetailActivity.REQUEST_FOR_DETAIL);
    }


    public void initSpringView() {
        ui.springviewCollect.setHeader(new DefaultHeader(this));
        ui.springviewCollect.setFooter(new DefaultFooter(this));
        ui.springviewCollect.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                ui.springviewCollect.onFinishFreshAndLoad();
            }

            @Override
            public void onLoadMore() {
                loadNetData();
                ui.springviewCollect.onFinishFreshAndLoad();
                ui.recyclerViewCollect.scrollToPosition(layoutManager.getItemCount());
            }
        });
    }


    @SuppressLint("CheckResult")
    private void loadNetData() {
        Http.getFavList(UserInfoRepository.getUserName(), PAGE_NUM + "", PAGE_SIZE + "")
            .compose(RxSchedulers.io_main())
            .subscribe(stringRetObjectResponse -> {
                    int ret = stringRetObjectResponse.retCode;
                    if (ret == 1) {
                        String json = stringRetObjectResponse.retData;
                        Gson gson = new Gson();
                        List<FavJson> favJsons = gson
                            .fromJson(json, new TypeToken<List<FavJson>>() {
                            }.getType());
                        handleFavJsons(favJsons);
                        PAGE_NUM++;
                        CollectionManager.getInstance().collections(favJsons);
                    } else {
                        loadLocalData();
                    }
                },
                throwable -> loadLocalData()
            );
    }

    private void loadLocalData() {
        if (PAGE_NUM == 0 && null != CollectionManager.getInstance().getCollections()) {
            handleFavJsons(CollectionManager.getInstance().getCollections());
        }
    }


    private void handleFavJsons(List<FavJson> mStoreDataList) {
        if (null == mStoreDataList || mStoreDataList.isEmpty()) {
            return;
        }
        if (items == null) {
            items = mStoreDataList;
        } else {
            items.addAll(mStoreDataList);
        }
        mAdapter.setItems(items);
    }


    /****
     * 笔记，只删除本地
     * 其它，删除网络
     * 同一条朋友圈，不同图片，共享一个msgID，删除成功后，接口返回却不成功。 暂时不管返回什么，直接删除本地
     * */
    @SuppressLint("CheckResult")
    public void removeItem(final int posi) {
        if (null != mAdapter.getItem(posi)) {
            FavJson favJson = mAdapter.getItem(posi);
            Http.removeFavItem(favJson.getFavMsgId(), UserInfoRepository.getUserName())
                .compose(RxSchedulers.io_main())
                .subscribe(retResponse -> {
                    if (1 == retResponse.retCode) {
                        notifyItemRemoved(posi, favJson);
                    } else {
                        T.show(retResponse.retMsg);
                    }
                });
        }
    }

    private void notifyItemRemoved(final int posi, final FavJson favJson) {
        CollectionManager.getInstance().deleteByFavId(favJson.getFavId());
        items.remove(posi);
        mAdapter.notifyItemRemoved(posi);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //详情
        if (requestCode == CollectionDetailActivity.REQUEST_FOR_DETAIL) {
            String user_labels = data.getStringExtra("Item_callback");
            int position = data.getIntExtra("position", 0);

            View view = ui.recyclerViewCollect.getLayoutManager().findViewByPosition(position);
            LinearLayout layout = view.findViewById(R.id.ll_item_collection);
            TextView mark = layout.findViewById(R.id.txt_collection_group);

            if (null != user_labels && user_labels.length() > 0) {
                layout.setVisibility(View.VISIBLE);
                mark.setText(user_labels);
                mark.setTextColor(ContextCompat.getColor(this, R.color.primary));
            } else {
                layout.setVisibility(View.GONE);
            }
        }

        //搜索
        if (requestCode == SearchNoteActivity.REQUEST_FOR_SEARCH
            && data.getBooleanExtra("dataChanged", false)) {
            loadLocalData();
        }
        //更新笔记后的回调
        if (requestCode == CollectionActivity.REQUEST_FOR_NOTE
            && data.getBooleanExtra("dataChanged", false)) {
            reLoadNote();
        }
    }

    /**
     * 更新笔记后的回调
     */
    private void reLoadNote() {
        if (CollectionManager.getInstance().getLastFavJson() != null) {
            items.add(0, CollectionManager.getInstance().getLastFavJson());
            mAdapter.notifyItemInserted(0);
            ui.recyclerViewCollect.scrollToPosition(0);
        }
    }

    @Override
    public void onItemClick(int position, int dataPosition) {
        switch (position) {
            case 1:
                forwardItem(dataPosition);
                break;
            case 2:
                removeItem(dataPosition);
                break;
            default:
                break;
        }
    }


    public void forwardItemInChat(int dataPosition) {
        if (null != items && items.size() > dataPosition) {
            transforFavJson = items.get(dataPosition);
            String type = transforFavJson.getFavType();
            String text = mAdapter.getItem(dataPosition).getFavContent();
            Content content = ContentFactory.createContent(type, text);
            openDialog(content.getText(), Content.getMsgType(type));
        }
    }


    private void forwardItem(int dataPosition) {
        if (null != items && items.size() > dataPosition) {
            FavJson favJson = items.get(dataPosition);
            String type = favJson.getFavType();
            String text = favJson.getFavContent();
            Content content = ContentFactory.createContent(type, text);
            Intent intent = TransforMsgActivity
                .newPureIntent(this, content.getText(), Content.getMsgType(type), 0, "");
            startActivity(intent);
        }
    }


    public void openDialog(String content, int messageType) {
        UserBean user = (UserBean) ProviderUser
            .selectRosterSingle(ContextHelper.getContext(), userId);
        if (null == user) {
            return;
        }
        transforContent = content;
        List<UserBean> rosterContactTempList = new ArrayList<UserBean>();
        rosterContactTempList.add(user);
        TransferDialog dialog = new TransferDialog(CollectionActivity.this, rosterContactTempList,
            this);
        dialog.setUser(null);
        dialog.setCarbonType(ETransforType.PURE_MSG.ordinal());
        dialog.setCarbonMode(0);
        dialog.setContent(content);
        dialog.setMessageType(messageType);
        dialog.show();
    }

    @Override
    public void onEvent(int event, Object o) {
        if (event == ETransforType.PURE_MSG.ordinal()) {
            if (o != null && transforFavJson != null && !TextUtils.isEmpty(transforContent)) {
                Intent intent = new Intent();
                intent.putExtra("content", transforContent);
                intent.putExtra("type", transforFavJson.getFavType());
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }
}
