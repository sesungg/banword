package com.banword;

public class BanwordConfigElement {

    private final String banwordLocation;
    private final String allowwordLocation;
    private final Class<?> banwordClass;
    private final Class<?> allowwordClass;
    private final long refreshInterval;
    private final String refreshIntervalCron;

    public BanwordConfigElement(String banwordLocation, String allowwordLocation, Class<?> banwordClass, Class<?> allowwordClass, long refreshInterval, String refreshIntervalCron) {
        if (banwordLocation != null) {
            this.banwordLocation = banwordLocation;
        } else {
            this.banwordLocation = "";
        }
        if (allowwordLocation != null) {
            this.allowwordLocation = allowwordLocation;
        } else {
            this.allowwordLocation = "";
        }
        if (banwordClass != null) {
            this.banwordClass = banwordClass;
        } else {
            this.banwordClass = Banword.class;
        }
        if (allowwordClass != null) {
            this.allowwordClass = allowwordClass;
        } else {
            this.allowwordClass = AllowWord.class;
        }
        if (refreshInterval > 0) {
            this.refreshInterval = refreshInterval;
        } else {
            this.refreshInterval = -1;
        }
        if (refreshIntervalCron != null) {
            this.refreshIntervalCron = refreshIntervalCron;
        } else {
            this.refreshIntervalCron = "";
        }
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
