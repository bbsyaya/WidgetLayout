package com.rexy.widgetlayout.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rexy.common.BaseFragment;
import com.rexy.widgets.tools.ViewUtils;
import com.rexy.widgets.view.FadeTextButton;
import com.rexy.widgetlayout.R;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO:功能说明
 *
 * @author: rexy
 * @date: 2017-06-05 15:03
 */
public class FragmentNestFloatLayout extends BaseFragment{
    RecyclerView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_nestfloatlayout, container, false);
        mListView = ViewUtils.view(root, R.id.listView);
        initTestListView(mListView,50);
        return root;
    }


    private void initTestListView(RecyclerView listView, int count) {
        List<String> list=new ArrayList(32);
        for(int i=0;i<count;i++){
            list.add(String.valueOf(i));
        }
        listView.setAdapter(new RecycleTestAdapter(getActivity(),list));
        listView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL));
    }


    static class RecycleTestAdapter extends RecyclerView.Adapter{
        List<String> mListData;
        Context mContext;

        public RecycleTestAdapter(Context context,List<String> list){
            mContext=context;
            mListData=list;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FadeTextButton textView =new FadeTextButton(mContext);
            textView.setPadding(30,30,30,30);
            textView.setTextSize(18);
            textView.setClickable(true);
            return new TestAdapter(textView);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if(viewHolder.itemView instanceof TextView){
                ((TextView)viewHolder.itemView).setText(mListData.get(i));
            }
        }

        @Override
        public int getItemCount() {
            return mListData==null?0:mListData.size();
        }


        class TestAdapter extends RecyclerView.ViewHolder{
            public TestAdapter(View itemView) {
                super(itemView);
            }
        }
    }
}
