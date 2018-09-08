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
import com.lens.chatmodel.ChatEnum.ETransforType;
import com.lens.chatmodel.bean.UserBean;
import com.lens.chatmodel.db.ProviderUser;
import com.lens.chatmodel.interf.IEventClickListener;
import com.lens.chatmodel.ui.message.ChatActivity;
import com.lens.chatmodel.ui.message.TransferDialog;
import com.lens.chatmodel.ui.message.TransforMsgActivity;
import com.lens.core.componet.net.exeception.ApiException;
import com.lens.route.annotation.Path;
import com.lensim.fingerchat.commons.base.BaseActivity;
import com.lensim.fingerchat.commons.base.BaseResponse;
import com.lensim.fingerchat.commons.helper.ContextHelper;
import com.lensim.fingerchat.commons.http.FXRxSubscriberHelper;
import com.lensim.fingerchat.commons.router.ActivityPath;
import com.lensim.fingerchat.commons.utils.T;
import com.lensim.fingerchat.commons.utils.ThreadUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.components.springview.container.DefaultFooter;
import com.lensim.fingerchat.components.springview.container.DefaultHeader;
import com.lensim.fingerchat.components.springview.widget.SpringView;
import com.lensim.fingerchat.components.widget.circle_friends.CollectDialog;
import com.lensim.fingerchat.data.CollectionApi;
import com.lensim.fingerchat.data.bean.GetFavoListBody;
import com.lensim.fingerchat.data.bean.GetFavoListResponse;
import com.lensim.fingerchat.data.bean.GetFavoListResponse.DataBean;
import com.lensim.fingerchat.data.login.UserInfoRepository;
import com.lensim.fingerchat.data.me.content.CollectionManager;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.databinding.ActivityCollectionBinding;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.ContentFactory;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.NoteContent;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;


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
    private CollectionApi collectionApi;

    private CollectionApi getCollectionApi() {
        return null == collectionApi ? new CollectionApi() : collectionApi;
    }


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
        loadNetData(false);
//        loadLocalData();
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
        if (TextUtils.isEmpty(text)) {
            T.show("数据为空");
            return;
        }
        boolean isNote = content.isNote();

        if (isNote) {
            if (!isFromChat) {
                NoteActivity.openActivity(this, CollectionActivity.REQUEST_FOR_NOTE,
                    text, mAdapter.getItem(position));
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
        intent.putExtra(CollectionDetailActivity.CREATOR, item.getFavCreater());
        intent.putExtra(CollectionDetailActivity.NAME, item.getProviderNick());
        intent.putExtra(CollectionDetailActivity.PROVIDER_ID, item.getFavProvider());
        intent.putExtra(CollectionDetailActivity.MSG_ID, item.getFavMsgId());
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
//                loadNetData(true);
                ui.springviewCollect.onFinishFreshAndLoad();
                ui.recyclerViewCollect.scrollToPosition(layoutManager.getItemCount());
            }
        });
    }


    @SuppressLint("CheckResult")
    private void loadNetData(boolean isLoadMore) {
        GetFavoListBody body = new GetFavoListBody();
        body.setPageIndex(PAGE_NUM);
        body.setPageSize(PAGE_SIZE);
        getCollectionApi().getAllFavoList(UserInfoRepository.getUserName(), body,
            new FXRxSubscriberHelper<BaseResponse<GetFavoListResponse>>() {
                @Override
                public void _onNext(BaseResponse<GetFavoListResponse> baseResponse) {
                    if (baseResponse.getCode() == 10) {
                        List<DataBean> dataBeans = baseResponse.getContent().getData();
                        List<FavJson> favJsons = new ArrayList<>();
                        for (DataBean dataBean : dataBeans) {
                            FavJson favJson = new FavJson();
                            favJson.setFavType(dataBean.getMsgType() + "");
                            favJson.setFavProvider(dataBean.getProvider());
                            favJson.setProviderNick(dataBean.getFromNickname());
                            favJson.setFavDes(dataBean.getTags());
                            favJson.setFavTime(TimeUtils.timeFormat(dataBean.getCreationTime()));
                            favJson.setFavCreater(dataBean.getCreator());
                            favJson.setFavMsgId(dataBean.getMsgId());
                            favJson.setFavContent(dataBean.getMsgContent());
                            try {
                                JSONObject jsonObject = new JSONObject(dataBean.getMsgContent());
                                if (jsonObject.has("userHeadImageStr")) {
                                    favJson.setFavCreaterAvatar(
                                        jsonObject.optString("userHeadImageStr"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            favJsons.add(favJson);
                        }
                        handleFavJsons(favJsons);
                        PAGE_NUM++;
                        for (FavJson favJson :favJsons){
                            CollectionManager.getInstance().collect(favJson);
                        }
                        //CollectionManager.getInstance().collections(favJsons);
                    } else {
                        if (!isLoadMore) {
                            loadLocalData();
                        }
                    }

                }

                @Override
                public void _onError(ApiException error) {
                    super._onError(error);
                    if (!isLoadMore) {
                        loadLocalData();
                    }
                }
            });
    }

    private void loadLocalData() {
        if (PAGE_NUM == 0 && null != CollectionManager.getInstance().getCollectionsByPage()) {
            //handleFavJsons(CollectionManager.getInstance().getCollections());
            handleFavJsons(CollectionManager.getInstance().getCollectionsByPage());
        }
    }
    //本地获取数据
   /* public void loadLocalData() {
        getLocalObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(getObserver());
    }*/
    //数据赋值
    public Consumer<List<FavJson>> getObserver() {
        return new Consumer<List<FavJson>>() {
            @Override
            public void accept(List<FavJson> mStoreDataList) throws Exception {
                if (null == mStoreDataList || mStoreDataList.isEmpty()) {
                    return;
                }
                handleFavJsons(mStoreDataList);
            }
        };
    }
    //获取本地数据
    private Observable<List<FavJson>> getLocalObservable() {
        return Observable.create(new ObservableOnSubscribe<List<FavJson>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FavJson>> e) throws Exception {
                if (!e.isDisposed()) {
                    List<FavJson> list = CollectionManager.getInstance().getCollectionsByPage();
                    if (null != list) {
                       /* if (list.size() < PAGE_SIZE) {
                            loadNetData(false);
                        } else {
                            e.onNext(list);
                        }*/
                        e.onNext(list);
                    }
                }
            }
        });
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
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.setItems(items);
            }
        });

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
            getCollectionApi().deleteCollection(UserInfoRepository.getUserName(),
                favJson.getFavMsgId(), new FXRxSubscriberHelper<BaseResponse>() {
                    @Override
                    public void _onNext(BaseResponse baseResponse) {
                        if ("Ok".equals(baseResponse.getMessage())) {
                            notifyItemRemoved(posi, favJson);
                        } else {
                            T.show(baseResponse.getMessage());
                        }
                    }
                });
        }
    }

    private void notifyItemRemoved(final int posi, final FavJson favJson) {
        CollectionManager.getInstance().deleteByFavId(favJson.getFavMsgId());
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
        Observable.create(new ObservableOnSubscribe<List<FavJson>>() {
            @Override
            public void subscribe(ObservableEmitter<List<FavJson>> e) throws Exception {
                if (!e.isDisposed()) {
                    List<FavJson> list = CollectionManager.getInstance().getCollectionsByPage(0, "1");
                    if (null != list && list.size() > 0) {
                        e.onNext(list);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<List<FavJson>>() {
                @Override
                public void accept(List<FavJson> mStoreDataList) throws Exception {
                    if (null == mStoreDataList || mStoreDataList.isEmpty()) {
                        return;
                    }
                    items.add(0, mStoreDataList.get(0));
                    mAdapter.notifyItemInserted(0);
                    ui.recyclerViewCollect.scrollToPosition(0);
                }
            });

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
            openDialog(content.getText(), type, Content.getMsgType(type));
        }
    }

    private String getText(String content, String type) {
        try {
            JSONObject jsonObject = new JSONObject(content);

            if (type.equals("1")) {
                return jsonObject.optString("content");
            } else if (type.equals("2")) {
                return content;
            } else if (type.equals("4")) {
                JSONObject json = new JSONObject();
                JSONObject jsonObject1 = new JSONObject();
                json.put("ImageUrl", jsonObject.optString("ImageUrl"));
                json.put("ImageSize", jsonObject.optString("ImageSize"));
                json.put("VideoUrl", jsonObject.optString("VideoUrl"));
                //jsonObject.put("timeLength",0);
                jsonObject1.put("body", json.toString());
                jsonObject1.put("secret", 0);
                jsonObject1.put("bubbleWidth", 0);
                jsonObject1.put("bubbleHeight", 0);
                jsonObject1.put("mucNickName", jsonObject.optString("userName"));

                return content;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    private void forwardItem(int dataPosition) {
        if (null != items && items.size() > dataPosition) {
            FavJson favJson = items.get(dataPosition);
            String type = favJson.getFavType();
            String text = favJson.getFavContent();
            Content content = ContentFactory.createContent(type, text);
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            String sendContent = "";
            if (type.equals("4")) {
                try {
                    JSONObject json = new JSONObject(content.getText());
                    if (null == json) {
                        return;
                    }
                    jsonObject.put("ImageUrl", json.optString("ImageUrl"));
                    jsonObject.put("ImageSize", json.optString("ImageSize"));
                    jsonObject.put("VideoUrl", json.optString("VideoUrl"));
                    //jsonObject.put("timeLength",0);
                    jsonObject1.put("body", jsonObject.toString());
                    jsonObject1.put("secret", 0);
                    jsonObject1.put("bubbleWidth", 0);
                    jsonObject1.put("bubbleHeight", 0);
                    jsonObject1.put("mucNickName", favJson.getFavProvider());
                    sendContent = jsonObject1.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("2")) {
                try {
                    JSONObject json = new JSONObject(content.getText());
                    if (null == json) {
                        return;
                    }
                    jsonObject.put("OriginalUrl", json.optString("OriginalUrl"));
                    jsonObject.put("OriginalSzie", json.optString("OriginalSzie"));
                    jsonObject.put("ThumbnailUrl", json.optString("ThumbnailUrl"));
                    jsonObject.put("ThumbnailSize", json.optString("ThumbnailSize"));
                    jsonObject1.put("body", jsonObject.toString());
                    jsonObject1.put("secret", 0);
                    jsonObject1.put("bubbleWidth", 0);
                    jsonObject1.put("bubbleHeight", 0);
                    sendContent = jsonObject1.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (type.equals("3")) {
                T.show(getString(R.string.collect_voice_not_support));
                return;
            } else if (type.equals("1")) {
                try {
                    JSONObject json = new JSONObject(content.getText());
                    if (null == json) {
                        return;
                    }
                    sendContent = json.getString("content");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            Intent intent = TransforMsgActivity
                .newPureIntent(this, sendContent, Content.getMsgType(type), 1, "");
            startActivity(intent);
        }
    }


    public void openDialog(String content, String type, int messageType) {
        if (type.equals("3")) {
            T.show(getString(R.string.collect_voice_not_support));
            return;
        }
        UserBean user = (UserBean) ProviderUser
            .selectRosterSingle(ContextHelper.getContext(), userId);
        if (null == user) {
            return;
        }
        transforContent = getText(content, type);
        List<UserBean> rosterContactTempList = new ArrayList<UserBean>();
        rosterContactTempList.add(user);
        TransferDialog dialog = new TransferDialog(CollectionActivity.this, rosterContactTempList,
            this);
        dialog.setUser(null);
        dialog.setCarbonType(ETransforType.PURE_MSG.ordinal());
        dialog.setCarbonMode(0);
        dialog.setContent(transforContent);
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

    private List<FavJson> removeDup(List<FavJson> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).equals(list.get(i)) || list.get(j).getFavMsgId()
                    .equals(list.get(i).getFavMsgId())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }

}
