package com.lensim.fingerchat.components.adapter;

import android.content.Context;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ll147996 on 2017/12/8.
 */
public abstract class FilterListAdapter<T extends FilterListAdapter.IFilterListAdapter>
        extends BaseListAdapter<T> implements Filterable {

    /**
     * 过滤实体接口
     * 如果需要过滤的实体必须继承该接口
     */
    public interface IFilterListAdapter {

        /**
         * 返回过滤字符
         * 该方法的返回值用来作为过滤判断依据
         *
         * @return
         */
        String getFilterValue();
    }


    /**
     * Lock used to modify the content of {@link #items}. Any write operation
     * performed on the array should be synchronized on this lock. This lock is also
     * used by the filter (see {@link #getFilter()} to make a synchronized copy of
     * the original array of data.
     */
    private final Object mLock = new Object();

    // Filter
    private Filter mFilter;

    // 原始数据
    private List<T> mOriginalValues = null;

    public FilterListAdapter(Context ctx) {
        super(ctx);
    }

    /**
     *
     * @return
     */
    @Override
    public Filter getFilter() {
        if (null == mFilter) {
            mFilter = new BaseListFilter();
        }
        return mFilter;
    }

    private class BaseListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(items);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<T> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<T> values;
                synchronized (mLock) {
                    values = new ArrayList<>(mOriginalValues);
                }

                final int count = values.size();
                final ArrayList<T> newValues = new ArrayList<>();

                for (int i = 0; i < count; i++) {
                    final T value = values.get(i);
                    final String valueText = value.getFilterValue().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            items = (List<T>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
