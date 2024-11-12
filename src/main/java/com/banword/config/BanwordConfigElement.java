package com.banword.config;

import com.banword.model.AllowWord;
import com.banword.model.Banword;

public class BanwordConfigElement {
    private final String banwordLocation;
    private final String allowwordLocation;
    private final Class<?> banwordClass;
    private final Class<?> allowwordClass;
    private final long refreshInterval;
    private final String refreshIntervalCron;


    public BanwordConfigElement(String banwordLocation,
                                String allowwordLocation,
                                Class<?> banwordClass,
                                Class<?> allowwordClass,
                                long refreshInterval,
                                String refreshIntervalCron) {
        this.banwordLocation = banwordLocation != null ? banwordLocation : "";
        this.allowwordLocation = allowwordLocation != null ? allowwordLocation : "";
        this.banwordClass = banwordClass != null ? banwordClass : Banword.class;
        this.allowwordClass = allowwordClass != null ? allowwordClass : AllowWord.class;
        this.refreshInterval = refreshInterval > 0 ? refreshInterval : -1;
        this.refreshIntervalCron = refreshIntervalCron != null ? refreshIntervalCron : "";
    }

    public String getBanwordLocation() {
        return banwordLocation;
    }

    public String getAllowwordLocation() {
        return allowwordLocation;
    }

    public Class<?> getBanwordClass() {
        return banwordClass;
    }

    public Class<?> getAllowwordClass() {
        return allowwordClass;
    }

    public long getRefreshInterval() {
        return refreshInterval;
    }

    public String getRefreshIntervalCron() {
        return refreshIntervalCron;
    }
}
