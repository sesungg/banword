package com.banword;

public class BanwordConfigFactory {

    private String location;

    private Class<?> banwordClass;

    private Class<?> allowwordClass;

    private long refreshInterval;

    private String refreshIntervalCron;

    public void setLocation(String location) {
        this.location = location;
    }

    public void setBanwordClass(Class<?> banwordClass) {
        this.banwordClass = banwordClass;
    }

    public void setAllowwordClass(Class<?> allowwordClass) {
        this.allowwordClass = allowwordClass;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public BanwordConfigElement createBanwordConfig() {
        String fileLocation;
        Class<?> banwordClass;

        if (this.location == null || this.location.isEmpty() || this.location == "") {

        }
    }
}
