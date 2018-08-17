package com.qingyun.zhiyunelu.ds.data;

import com.google.gson.JsonElement;

public class SimpleItemModel {
    public String value;
    public String name;
    public String label;
    public JsonElement extra;
    public SimpleItemModel[] children;
    public String parentValue;
}
