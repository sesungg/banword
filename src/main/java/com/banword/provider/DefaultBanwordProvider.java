package com.banword.provider;

import com.banword.model.Banword;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnMissingBean(BanwordProvider.class)
public class DefaultBanwordProvider implements BanwordProvider {

    @Override
    public List<Banword> provideBanword() {
        return Collections.emptyList();
    }
}
