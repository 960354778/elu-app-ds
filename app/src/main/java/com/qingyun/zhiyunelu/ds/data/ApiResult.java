package com.qingyun.zhiyunelu.ds.data;

import java.util.Calendar;

public class ApiResult<TData> {
    public Calendar timestamp;
    public TData data;
    public ErrorInfo error;
    public TokenInfo token;
}
