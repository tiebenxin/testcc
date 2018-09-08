package com.lensim.fingerchat.fingerchat.ui.me.collection;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.lens.chatmodel.helper.ImageHelper;
import com.lensim.fingerchat.commons.global.Route;
import com.lensim.fingerchat.commons.utils.StringUtils;
import com.lensim.fingerchat.commons.utils.TimeUtils;
import com.lensim.fingerchat.commons.utils.UIHelper;
import com.lensim.fingerchat.components.adapter.BaseRecyclerAdapter;
import com.lensim.fingerchat.data.me.content.FavJson;
import com.lensim.fingerchat.fingerchat.R;
import com.lensim.fingerchat.fingerchat.ui.me.collection.CollectFrameAdapter.ItemVh;
import com.lensim.fingerchat.fingerchat.ui.me.collection.fragment.AbsContentView;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.Content;
import com.lensim.fingerchat.fingerchat.ui.me.collection.type.ContentFactory;
import com.lensim.fingerchat.fingerchat.ui.me.utils.GlideCircleTransform;


/**
 * date on 2018/3/14
 * author ll147996
 * describe
 */

public class CollectFrameAdapter extends BaseRecyclerAdapter<ItemVh, FavJson> {

    private LayoutInflater mInflater;
    private String word;
    AbsContentView absFragmetnView;

    public CollectFrameAdapter(Context ctx) {
        super(ctx);
        this.mContext = ctx;
        mInflater = LayoutInflater.from(mContext);
    }

//    @Override
//    public final int getItemViewType(int position) {
//        FavJson item = items.get(position);
//        Content content = ContentFactory.createContent(item.getFavType(), item.getFavContent());
//        absFragmetnView = content.getContentView(mContext);
//        return 0;
//    }

    private ViewGroup viewGroup;

    @Override
    public ItemVh onCreateViewHolder(ViewGroup parent, int viewType) {
        viewGroup = parent;
        View view = mInflater.inflate(R.layout.item_collect_frame, parent, false);
        return new ItemVh(view);
    }

    @Override
    public void onBindViewHolder(ItemVh holder, int position) {
        FavJson item = items.get(position);
        long timeStamp = TimeUtils.getTimeStamp(item.getFavTime());
        String userid = item.getFavProvider();
        if (StringUtils.isEmpty(item.getFavCreaterAvatar())) {
            if (!TextUtils.isEmpty(userid)){
            int index = userid.indexOf("@");
            if (-1 != index) {
                userid = userid.substring(0, index);
            }
            item.setFavCreaterAvatar(String.format(Route.obtainAvater, userid));

            }
        }

        ImageHelper.loadColloctionAvatar(item.getFavCreaterAvatar(), holder.avatar,
            new GlideCircleTransform(mContext));

        holder.username.setText(item.getProviderNick());
        holder.createTime.setText(TimeUtils.secondToTime(timeStamp + ""));

        Content content = ContentFactory.createContent(item.getFavType(), item.getFavContent());
        absFragmetnView = content.getContentView(mContext);
        View containerView = absFragmetnView.getFrameLayoutView(mInflater, viewGroup);
        holder.container.removeAllViews();
        holder.container.addView(containerView);

        if (StringUtils.isEmpty(item.getFavDes())) {
            holder.llItemCollection.setVisibility(View.GONE);
            holder.txtCollectionGroup
                .setText(mContext.getResources().getString(R.string.hint_add_mark));
            holder.txtCollectionGroup
                .setTextColor(ContextCompat.getColor(mContext, R.color.primary_text));
        } else {
            holder.llItemCollection.setVisibility(View.VISIBLE);
            holder.txtCollectionGroup.setText(item.getFavDes());

            holder.txtCollectionGroup
                .setTextColor(ContextCompat.getColor(mContext, R.color.primary));
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                onItemLongClickListener.onItemLongClick(item, holder.getLayoutPosition());
            }
            return false;
        });
    }

    public void setWord(String word) {
        this.word = word;
    }


    public static class ItemVh extends BaseRecyclerAdapter.VH {

        private LinearLayout llItemCollection;
        private TextView txtCollectionGroup;
        private ImageView avatar;
        private TextView username;
        private FrameLayout container;
        private TextView createTime;

        ItemVh(View itemView) {
            super(itemView);

            avatar = (ImageView) findViewById(R.id.avatar);
            username = (TextView) findViewById(R.id.username);
            container = (FrameLayout) findViewById(R.id.container);
            createTime = (TextView) findViewById(R.id.create_time);
            llItemCollection = (LinearLayout) findViewById(R.id.ll_item_collection);
            txtCollectionGroup = (TextView) findViewById(R.id.txt_collection_group);

            UIHelper.setTextSize2(14, username, createTime, txtCollectionGroup);
        }

        private View findViewById(int resId) {
            return itemView.findViewById(resId);
        }

    }

    private OnItemLongClickListener onItemLongClickListener;

    /**
     * 设置LongClick事件
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }


    public interface OnItemLongClickListener {

        void onItemLongClick(FavJson item, int position);
    }
}
