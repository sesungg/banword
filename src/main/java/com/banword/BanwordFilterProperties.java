package com.banword;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "banword-filter")
public class BanwordFilterProperties {
    private String banwordFilePath;
    private String allowwordFilePath;

    public String getBanwordFilePath() {
        return banwordFilePath;
    }

    public void setBanwordFilePath(String banwordFilePath) {
        this.banwordFilePath = banwordFilePath;
    }

    public String getAllowwordFilePath() {
        return allowwordFilePath;
    }

    public void setAllowwordFilePath(String allowwordFilePath) {
        this.allowwordFilePath = allowwordFilePath;
    }
}
