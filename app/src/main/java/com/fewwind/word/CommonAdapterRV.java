package com.fewwind.word;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 *
 */

public abstract class CommonAdapterRV<T> extends RecyclerView.Adapter<ViewHolderRV> {
    protected Context mContext;
    protected int mLayoutId;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;

    public CommonAdapterRV(Context context, List<T> datas, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public ViewHolderRV onCreateViewHolder(final ViewGroup parent, int viewType) {
        ViewHolderRV viewHolder = null;
        if (viewType != 0) {
            viewHolder = ViewHolderRV.createViewHolder(mContext, parent, getItemIdType(viewType));
        } else {
            viewHolder = ViewHolderRV.createViewHolder(mContext, parent, mLayoutId);
        }
        onViewHolderCreated(viewHolder, viewHolder.getConvertView(), viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolderRV holder, int position) {
        convert(holder, mDatas.get(position));
    }

    public void onViewHolderCreated(ViewHolderRV holder, View itemView, int viewType) {

    }

    public int getItemIdType(int viewType) {
        return viewType;
    }

    public abstract void convert(ViewHolderRV holder, T t);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }
}