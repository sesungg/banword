package com.banword.config;

public class BanwordConfigFactory {

    private String banwordLocation;
    private String allowwordLocation;

    private Class<?> banwordClass;

    private Class<?> allowwordClass;

    private long refreshInterval;

    private String refreshIntervalCron;

    public BanwordConfigFactory setBanwordLocation(String banwordLocation) {
        this.banwordLocation = banwordLocation;
        return this;
    }

    public BanwordConfigFactory setAllowwordLocation(String allowwordLocation) {
        this.allowwordLocation = allowwordLocation;
        return this;
    }

    public BanwordConfigFactory setBanwordClass(Class<?> banwordClass) {
        this.banwordClass = banwordClass;
        return this;
    }

    public BanwordConfigFactory setAllowwordClass(Class<?> allowwordClass) {
        this.allowwordClass = allowwordClass;
        return this;
    }

    public void setRefreshInterval(long refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public void setRefreshIntervalCron(String refreshIntervalCron) {
        this.refreshIntervalCron = refreshIntervalCron;
    }

    public BanwordConfigElement createBanwordConfig() {
        validateRequiredFields();
        return new BanwordConfigElement(
                this.banwordLocation,
                this.allowwordLocation,
                this.banwordClass,
                this.allowwordClass,
                this.refreshInterval,
                this.refreshIntervalCron
        );
    }

    private void validateRequiredFields() {
        if (banwordLocation == null || allowwordLocation == null ||
                banwordLocation.equals("") || allowwordLocation.equals("") ||
                banwordClass == null || allowwordClass == null) {
            throw new IllegalArgumentException("bawordLocation, allowwordLocation, banwordClass, allowwordClass are required fields.");
        }
    }
}
