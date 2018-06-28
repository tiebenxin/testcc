package com.lens.chatmodel.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import com.lens.chatmodel.R;
import com.lens.chatmodel.adapter.AbstractRecyclerAdapter.OnItemClickListener;
import com.lens.chatmodel.adapter.MessageAdapter;
import com.lens.chatmodel.interf.IChatRoomModel;
import com.lensim.fingerchat.components.pulltorefresh.XCPullToLoadMoreListView;


/**
 * 消息列表
 *
 * @author 周哥
 */
public class ChatMessageList extends RelativeLayout {

    protected static final String TAG = "EaseChatMessageList";
    protected XCPullToLoadMoreListView listView;
    protected Context context;
    protected boolean showUserNick;
    protected boolean showAvatar;
    protected Drawable myBubbleBg;
    protected Drawable otherBuddleBg;
    private MessageAdapter messageAdapter;
    private int visiblePosition;
    private int visibleOffset;

    public ChatMessageList(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public ChatMessageList(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseStyle(context, attrs);
        init(context);
    }

    public ChatMessageList(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.lens_chat_message_list, this);
        listView = findViewById(R.id.list_chat_message);
        listView.getListView().setOnScrollListener(mOnScrollListener);

    }

    private AbsListView.OnScrollListener mOnScrollListener = new AbsListView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //Log.e(TAG, "onScrollStateChanged()---scrollState -->"+scrollState);

            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                visiblePosition = listView.getFirstVisiblePosition();
                View topView = listView.getChildAt(listView.getFirstVisiblePosition());
                if ((topView != null)) {
                    Log.e(TAG, "onScrollStateChanged()---topView 显示");
                    visibleOffset = topView.getTop();
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        }
    };

    public void setAdapter(BaseAdapter adapter) {
        listView.setAdapter(adapter);
        messageAdapter = (MessageAdapter) adapter;

    }


    protected void parseStyle(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ChatMessageList);
        showAvatar = ta.getBoolean(R.styleable.ChatMessageList_msgListShowUserAvatar, true);
        myBubbleBg = ta.getDrawable(R.styleable.ChatMessageList_msgListMyBubbleBackground);
        otherBuddleBg = ta.getDrawable(R.styleable.ChatMessageList_msgListMyBubbleBackground);
        showUserNick = ta.getBoolean(R.styleable.ChatMessageList_msgListShowUserNick, false);
        ta.recycle();
    }

    public void scrollUp() {
        listView.getListView().smoothScrollToPosition(0);
    }

    public void scrollDown() {
        if (listView != null && messageAdapter != null) {
            listView.getListView().setSelection(messageAdapter.getCount() - 1);
        }
    }

    public void scrollToPostion(int postion) {
        listView.getListView().setSelection(postion);
    }

    /**
     * 获取listview
     */
    public XCPullToLoadMoreListView getListView() {
        return listView;
    }

    /**
     * 获取SwipeRefreshLayout
     */
    public IChatRoomModel getItem(int position) {
        return messageAdapter.getItem(position);
    }


    /**
     * 设置list item里控件的点击事件
     */
    public void setItemClickListener(OnItemClickListener listener) {
        if (messageAdapter != null) {
            messageAdapter.setItemClickListener(listener);
        }
    }

}
