package com.order.utils;

import java.util.List;

public abstract class LoadBalance {
    public volatile static List<String> SERVICE_LIST;

    public abstract String choseServiceHost();
}
