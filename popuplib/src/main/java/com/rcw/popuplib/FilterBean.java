package com.rcw.popuplib;

import java.util.List;

/**
 * Created by ruancw on 2018/5/31.
 * 用于筛选的数据类
 */

public class FilterBean {
    private String typeName;//标题名字
    private TableMode tab;//用于记录上次点击的位置
    private List<TableMode> tabs; //标签集合

    public FilterBean(String typeName, TableMode tab, List<TableMode> tabs) {
        this.typeName = typeName;
        this.tab = tab;
        this.tabs = tabs;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public TableMode getTab() {
        return tab;
    }

    public void setTab(TableMode tab) {
        this.tab = tab;
    }

    public List<TableMode> getTabs() {
        return tabs;
    }

    public void setTabs(List<TableMode> tabs) {
        this.tabs = tabs;
    }

    public static class TableMode{
        String name;

        public TableMode(String name) {
            this.name = name;
        }
    }
}
