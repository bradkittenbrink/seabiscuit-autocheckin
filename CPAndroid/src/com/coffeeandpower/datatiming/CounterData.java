package com.coffeeandpower.datatiming;

import com.coffeeandpower.cont.DataHolder;

public class CounterData {
    public static String triggertype = "trigger";

    public static String counttype = "count";

    private DataHolder data;

    public String type = "unknown";

    public CounterData(DataHolder myData) {
        this.type = CounterData.triggertype;
        this.data = myData;
    }

    public DataHolder getData() {
        return data;
    }
}
