package com.banword;

public class BanwordConfigFactory {

    private String banwordLocation;
    private String allowwordLocation;

    private Class<?> banwordClass;

    private Class<?> allowwordClass;

    private long refreshInterval;

    private String refreshIntervalCron;

    public void setBanwordLocation(String banwordLocation) {
        this.banwordLocation = banwordLocation;
    }

    public void setAllowwordLocation(String allowwordLocation) {
        this.allowwordLocation = allowwordLocation;
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

    public void setRefreshIntervalCron(String refreshIntervalCron) {
        this.refreshIntervalCron = refreshIntervalCron;
    }

    public BanwordConfigElement createBanwordConfig() {
        return new BanwordConfigElement(
                this.banwordLocation,
                this.allowwordLocation,
                this.banwordClass,
                this.allowwordClass,
                this.refreshInterval,
                this.refreshIntervalCron
        );
    }
}
