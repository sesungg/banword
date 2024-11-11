package com.banword;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BanwordFilterProperties {
    @Value("${banword.file.path}")
    private String banwordFilePath;

    @Value("${allowword.file.path}")
    private String allowwordFilePath;

    public String getBanwordFilePath() {
        return banwordFilePath;
    }
    public String getAllowwordFilePath() {
        return allowwordFilePath;
    }
}
