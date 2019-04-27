package com.kunminx.rxmagic.ui.widget.linkage;
/*
 * Copyright (c) 2018-2019. KunMinX
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.kunminx.rxmagic.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by KunMinX at 19/4/27
 */
public class LinkageRecyclerView extends RelativeLayout {

    private Context mContext;
    private RecyclerView mRvLevel1;
    private RecyclerView mRvLevel2;
    private LinkageLevel1Adapter mLevel1Adapter;
    private LinkageLevel2Adapter mLevel2Adapter;
    private TextView mTvLevel2Header;
    private List<String> mGroupNames;
    private List<LinkageItem> mItems;
    private List<Integer> mHeaderPositions = new ArrayList<>();
    private int mTitleHeight;
    private int mFirstPosition = 0;
    private LinearLayoutManager mLevel2LayoutManager;
    private boolean mIsGridLayout;
    private OnLinkageItemClickListener mClickListener;

    public void setClickListener(OnLinkageItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public void setGridLayout(boolean gridLayout) {
        mIsGridLayout = gridLayout;
    }

    public List<Integer> getHeaderPositions() {
        return mHeaderPositions;
    }

    public LinkageRecyclerView(Context context) {
        super(context);
    }

    public LinkageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public LinkageRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_linkage_view, this);
        mRvLevel1 = (RecyclerView) view.findViewById(R.id.rv_level_1);
        mRvLevel2 = (RecyclerView) view.findViewById(R.id.rv_level_2);
        mTvLevel2Header = (TextView) view.findViewById(R.id.tv_level_2_header);

        int layout = R.layout.adapter_linkage_level_2_linear;
        if (mIsGridLayout) {
            mLevel2LayoutManager = new GridLayoutManager(mContext, 3);
            layout = R.layout.adapter_linkage_level_2_grid;
        } else {
            mLevel2LayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        }

        mLevel2Adapter = new LinkageLevel2Adapter(layout, R.layout.adapter_linkage_level_2_title, null);
        mRvLevel2.setLayoutManager(mLevel2LayoutManager);
        mRvLevel2.setAdapter(mLevel2Adapter);

        mLevel1Adapter = new LinkageLevel1Adapter(R.layout.adapter_linkage_level_1, null);
        mRvLevel1.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
//        mRvLevel1.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mRvLevel1.setAdapter(mLevel1Adapter);
    }

    public void init(List<String> groupNames, List<LinkageItem> linkageItems) {
        this.mItems = linkageItems;
        this.mGroupNames = groupNames;
        initLinkageLevel1();
        initLinkageLevel2();
    }

    private void initLinkageLevel2() {
        mLevel2Adapter.setNewData(mItems);

        if (mItems.get(mFirstPosition).isHeader) {
            mTvLevel2Header.setText(mItems.get(mFirstPosition).header);
        }

        mLevel2Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                switch (view.getId()) {
                    case R.id.level_2_item:
                        if (mClickListener != null && !mHeaderPositions.contains(i)) {
                            mClickListener.onLinkageLevel2Click((LinkageLevel2Adapter) baseQuickAdapter, view, i);
                        }
                        break;
                    default:
                }
            }
        });

        mRvLevel2.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mTitleHeight = mTvLevel2Header.getHeight();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (mItems.get(mFirstPosition).isHeader) {
                    View view = mLevel2LayoutManager.findViewByPosition(mFirstPosition);
                    if (view != null) {
                        if (view.getTop() >= mTitleHeight) {
                            mTvLevel2Header.setY(view.getTop() - mTitleHeight);
                        } else {
                            mTvLevel2Header.setY(0);
                        }
                    }
                }

                int firstPosition = mLevel2LayoutManager.findFirstVisibleItemPosition();
                if (mFirstPosition != firstPosition && firstPosition >= 0) {
                    mFirstPosition = firstPosition;
                    mTvLevel2Header.setY(0);

                    if (mItems.get(mFirstPosition).isHeader) {
                        mTvLevel2Header.setText(mItems.get(mFirstPosition).header);
                    } else {
                        mTvLevel2Header.setText(mItems.get(mFirstPosition).t.getGroup());
                    }
                }

                for (int i = 0; i < mGroupNames.size(); i++) {
                    if (mGroupNames.get(i).equals(mTvLevel2Header.getText().toString())) {
                        mLevel1Adapter.selectItem(i);
                    }
                }

                if (mLevel2LayoutManager.findLastCompletelyVisibleItemPosition() == mItems.size() - 1) {
                    mLevel1Adapter.selectItem(mGroupNames.size() - 1);
                }
            }
        });
    }

    private void initLinkageLevel1() {
        mLevel1Adapter.setNewData(mGroupNames);

        mLevel1Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.layout_group:
                        mLevel1Adapter.selectItem(position);
                        mLevel2LayoutManager.scrollToPositionWithOffset(mHeaderPositions.get(position), 0);
                        if (mClickListener != null) {
                            mClickListener.onLinkageLevel1Click((LinkageLevel1Adapter) adapter, view, position);
                        }
                        break;
                    default:
                }
            }
        });
    }


    public interface OnLinkageItemClickListener {
        void onLinkageLevel1Click(LinkageLevel1Adapter adapter, View view, int position);

        void onLinkageLevel2Click(LinkageLevel2Adapter adapter, View view, int position);
    }


}
