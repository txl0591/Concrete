package com.concrete.ctrl;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.concrete.app.R;
import com.concrete.common.nlog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tangxl on 2017/12/17.
 */

public class HistoryAdapter extends BaseAdapter {

    private ArrayList<HistoryItem> mList = null;
    private Context mContext = null;
    private LayoutInflater inflater = null;

    public HistoryAdapter(ArrayList<HistoryItem> list, Context context){
        mList = list;
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(mList.size() == 0){
            return view;
        }

        HistoryItem mHistoryItem = mList.get(i);

        View mView = null;

        if (view == null) {
            view = inflater.inflate(R.layout.history_item, null);
            mView = view;
        } else {
            mView = view;
        }

        ImageView mImageView = (ImageView) mView.findViewById(R.id.icon_right);
        TextView mTextView = (TextView) mView.findViewById(R.id.text_left);
        if(mHistoryItem.ItemImage > 0){
            mImageView.setImageResource(mHistoryItem.ItemImage);
            mImageView.setVisibility(View.VISIBLE);
        }else{
            mImageView.setVisibility(View.INVISIBLE);
        }
        mTextView.setText(mHistoryItem.ItemText);

        return mView;
    }

    public void notifyDataSetChanged(ListView listView, int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = listView.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = listView.getChildAt(position - firstVisiblePosition);
            getView(position, view, listView);
        }

    }

    public void notifyDataSetChanged(ListView listView) {
        /**第一个可见的位置**/
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = listView.getLastVisiblePosition();
        for(int i = firstVisiblePosition; i < lastVisiblePosition;i ++){
            View view = listView.getChildAt(i - firstVisiblePosition);
            getView(i, view, listView);
        }
    }

    public void UpdateListData(ListView listView,ArrayList<HistoryItem> item, boolean force){
        if(!mList.isEmpty()){
            mList.clear();
        }

        mList = item;

        if(force){
            notifyDataSetChanged();
        }else{
            if(mList.size() > 0){
                notifyDataSetChanged(listView);
            }else{
                notifyDataSetChanged();
            }
        }
    }
}
