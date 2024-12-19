package com.banword.provider;

import com.banword.model.Allowword;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@ConditionalOnMissingBean(AllowwordProvider.class)
public class DefaultAllowwordProvider implements AllowwordProvider {
    @Override
    public List<Allowword> provideAllowwords() {
        return Collections.emptyList();
    }
}
