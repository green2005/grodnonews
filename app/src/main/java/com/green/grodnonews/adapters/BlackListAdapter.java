package com.green.grodnonews.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.green.grodnonews.R;
import com.green.grodnonews.room.BlackListItem;

import java.util.List;

public class BlackListAdapter extends BaseAdapter {
    public interface OnSelectedItemsChangeListener {
        void onChange();
    }

    private class ViewHolder {
        TextView tvUser;
        CheckBox chDel;
    }

    private List<BlackListItem> mItems = null;
    private List<String> mSelectedItems = null;
    private LayoutInflater mInflater;
    private OnSelectedItemsChangeListener mOnChangeListener;
    private CompoundButton.OnCheckedChangeListener mOnCheckChangeListener;


    public BlackListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mOnCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = (Integer) buttonView.getTag();
                if (isChecked) {
                    if (!mSelectedItems.contains(mItems.get(position))) {
                        mSelectedItems.add(mItems.get(position).userName);
                    }
                } else {
                    mSelectedItems.remove(mItems.get(position).userName);
                }
                mOnChangeListener.onChange();
            }
        };
    }

    public void setSelectedList(List<String> items, OnSelectedItemsChangeListener changeListener) {
        mSelectedItems = items;
        mOnChangeListener = changeListener;
    }

    public void setItems(List<BlackListItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mItems == null)
            return 0;
        else
            return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if (mItems == null) {
            return null;
        } else
            return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_black_list, null);
            holder = new ViewHolder();
            holder.tvUser = convertView.findViewById(R.id.tvPerson);
            holder.chDel = convertView.findViewById(R.id.chDel);
            holder.chDel.setOnCheckedChangeListener(mOnCheckChangeListener);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.chDel.setTag(position);
        BlackListItem item = mItems.get(position);
        holder.tvUser.setText(item.userName);
        holder.chDel.setOnCheckedChangeListener(null);
        holder.chDel.setChecked(mSelectedItems.contains(item.userName));
        holder.chDel.setOnCheckedChangeListener(mOnCheckChangeListener);
        return convertView;
    }
}
