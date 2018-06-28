package com.lensim.fingerchat.components.adapter.multitype;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;


public class MultiTypePool implements TypePool {

    private @NonNull
    final List<Class<?>> classes;
    private @NonNull
    final List<ItemViewBinder<?, ?>> binders;


    public MultiTypePool() {
        this.classes = new ArrayList<>();
        this.binders = new ArrayList<>();
    }



    public MultiTypePool(int initialCapacity) {
        this.classes = new ArrayList<>(initialCapacity);
        this.binders = new ArrayList<>(initialCapacity);
    }



    public MultiTypePool(
            @NonNull List<Class<?>> classes, @NonNull List<ItemViewBinder<?, ?>> binders) {
        this.classes = classes;
        this.binders = binders;
    }


    @Override
    public <T> void register(
        @NonNull Class<? extends T> clazz,
        @NonNull ItemViewBinder<T, ?> binder) {
        classes.add(clazz);
        binders.add(binder);
    }


    @Override
    public boolean unregister(@NonNull Class<?> clazz) {
        boolean removed = false;
        while (true) {
            int index = classes.indexOf(clazz);
            if (index != -1) {
                classes.remove(index);
                binders.remove(index);
                removed = true;
            } else {
                break;
            }
        }
        return removed;
    }

    @Override
    public boolean checkType(@NonNull Class<?> clazz) {
        int index = classes.indexOf(clazz);
        if (index != -1) {
            return true;
        }
        return false;
    }


    @Override
    public int size() {
        return classes.size();
    }


    @Override
    public int firstIndexOf(@NonNull final Class<?> clazz) {
        int index = classes.indexOf(clazz);
        if (index != -1) {
            return index;
        }
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).isAssignableFrom(clazz)) {
                return i;
            }
        }
        return -1;
    }


    @Override
    public @NonNull
    Class<?> getClass(int index) {
        return classes.get(index);
    }


    @Override
    public @NonNull
    ItemViewBinder<?, ?> getItemViewBinder(int index) {
        return binders.get(index);
    }

}
