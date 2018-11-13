package com.spark.bipaywallet.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.spark.bipaywallet.R;
import com.spark.bipaywallet.entity.SideBarBean;
import com.spark.bipaywallet.utils.DpPxUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * 侧边栏的适配器
 */


public class SideBarAdapter extends BaseQuickAdapter<SideBarBean, BaseViewHolder> {
    private List<SideBarBean> list;
    private Context context;
    private int dp20;
    private int dp30;
    private List<SideBarBean> tempList = new LinkedList<>();

    public SideBarAdapter(Context context, int layoutResId, @Nullable List<SideBarBean> data) {
        super(layoutResId, data);
        this.context = context;
        this.list = data;
        dp20 = DpPxUtils.dip2px(context, 20);
        dp30 = DpPxUtils.dip2px(context, 35);
        sortItem(list);
        removeUnExpandItem(list);
    }

    /**
     * 对侧边栏的数据进行排序
     *
     * @param list
     */
    private void sortItem(List<SideBarBean> list) {
        Collections.sort(list, new Comparator<SideBarBean>() {

            @Override
            public int compare(SideBarBean lhs, SideBarBean rhs) {
                if (lhs.getOrder() - rhs.getOrder() > 0) {
                    return 1;
                } else if (lhs.getOrder() - rhs.getOrder() < 0) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        //设置侧边栏的层级和是否选中
        for (SideBarBean sideBarItem : list) {
            sideBarItem.setLevel(initItemLevel(sideBarItem));
            //因为默认只有一个选中，所以一开始进来，全部的没选中，设为false
            sideBarItem.setCheck(false);
        }

    }

    /**
     * 移除没有展开的item。注意：父亲没有展开，儿子也不给他显示
     *
     * @param list
     */
    private void removeUnExpandItem(List<SideBarBean> list) {
        tempList.clear();
        for (SideBarBean sideBarItem : list) {
            if (sideBarItem.getParentItem() != null) {
                if (!sideBarItem.getParentItem().isExpand()) {
                    sideBarItem.setExpand(false);
                    tempList.add(sideBarItem);
                }
            }
        }
        list.removeAll(tempList);
    }

    // 递归设置level
    private int initItemLevel(SideBarBean item) {
        int level = 1;
        if (item.getParentItem() != null) {
            item.getParentItem().setHasChild(true);
            return (level + initItemLevel(item.getParentItem()));
        }
        return level;
    }


    @Override
    protected void convert(BaseViewHolder helper, SideBarBean item) {
        View convertView = helper.getConvertView();
        int position = helper.getAdapterPosition();

        LinearLayout llName = helper.getView(R.id.llName);
        if (item.getLevel() == SideBarBean._First) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llName.getLayoutParams();
            lp.leftMargin = dp20;
            llName.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llName.getLayoutParams();
            lp.leftMargin = dp30;
            llName.setLayoutParams(lp);
        }
        //如果有孩子，就有右边的小箭头，没有的话隐藏的
        ImageView ivArrow = helper.getView(R.id.ivArrow);
        if (item.isHasChild()) {
            ivArrow.setVisibility(View.VISIBLE);
            if (item.isExpand()) {
                ivArrow.setBackgroundResource(R.mipmap.icon_up_close);
//                convertView.setBackgroundColor(context.getResources().getColor(R.color.bg_create_man));
            } else {
                ivArrow.setBackgroundResource(R.mipmap.icon_down_open);
//                convertView.setBackgroundColor(context.getResources().getColor(R.color.main_bg));
            }
        } else {
            ivArrow.setVisibility(View.INVISIBLE);
        }
        helper.setText(R.id.tvName, item.getItemName());
        convertView.setTag(R.id.sidebar, item);
        convertView.setTag(R.id.curPosition, position);
        convertView.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            list.addAll(tempList);
            sortItem(list);
            SideBarBean item = (SideBarBean) v.getTag(R.id.sidebar);
            int curPosition = (int) v.getTag(R.id.curPosition);
            item.setCheck(!item.isCheck());
            item.setExpand(!item.isExpand());
            removeUnExpandItem(list);
            //设置回调
            if (listener != null) {
                listener.onSideBarItemClick(item, curPosition);
            }
            notifyDataSetChanged();
        }
    };

    private SideBarListener listener;

    public void setSideBarListener(SideBarListener listener) {
        this.listener = listener;
    }

    public interface SideBarListener {
        void onSideBarItemClick(SideBarBean item, int position);
    }

    /**
     * 设置当前选中的是那个item
     *
     * @param index
     */
    public void setCurCheckItem(int index) {
        list.addAll(tempList);
        sortItem(list);
        SideBarBean item = getItem(index);
        item.setCheck(true);
        item.setExpand(true);
        removeUnExpandItem(list);
        notifyDataSetChanged();
    }

}
