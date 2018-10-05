package com.qingyun.zhiyunelu.ds.data;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ApiResult<TData> {
    public Calendar timestamp;
    public TData data;
    public ErrorInfo error;
    public TokenInfo token;
    public Map<String, List<SimpleItem>> pocket;
}
