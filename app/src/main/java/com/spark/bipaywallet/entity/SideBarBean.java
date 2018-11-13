package com.spark.bipaywallet.entity;

public class SideBarBean {
    public static final int _First = 1;//一级
    public static final int _Second = 2;//二级
    public static final int _Third = 3;//三级
    private String itemName;
    private SideBarBean parentItem;
    private int level = _First;
    private float order;
    private boolean isExpand = false;
    private boolean hasChild = false;
    private boolean isCheck = false;
    private int tag;

    public SideBarBean() {
        super();
    }

    public SideBarBean(String itemName, float order, int tag) {
        super();
        this.itemName = itemName;
        this.order = order;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean isExpand) {
        this.isExpand = isExpand;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public SideBarBean getParentItem() {
        return parentItem;
    }

    public void setParentItem(SideBarBean parentItem) {
        this.parentItem = parentItem;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getOrder() {
        return order;
    }

    public void setOrder(float order) {
        this.order = order;
    }

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    @Override
    public String toString() {
        return "SideBarItem [isExpand=" + isExpand + ", itemName=" + itemName + ", parentItem=" + parentItem
                + ", level=" + level + ", order=" + order + "]";
    }


}
