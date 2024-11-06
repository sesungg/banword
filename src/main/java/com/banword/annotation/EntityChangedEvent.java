package com.banword.annotation;

import com.banword.AllowWord;
import com.banword.Banword;
import org.springframework.context.ApplicationEvent;

public class EntityChangedEvent extends ApplicationEvent {
    private final Object entity;

    public EntityChangedEvent(Object entity) {
        super(entity);
        this.entity = entity;
    }

    public Object getEntity() {
        return entity;
    }

    public boolean isBanwordChanged() {
        return entity instanceof Banword;
    }

    public boolean isAllowWordChanged() {
        return entity instanceof AllowWord;
    }
}
