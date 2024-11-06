package com.banword.annotation;

import com.banword.BanwordService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BanwordEntityListener {

    private final BanwordService banwordService;

    public BanwordEntityListener(BanwordService banwordService) {
        this.banwordService = banwordService;
    }

    @TransactionalEventListener  // 트랜잭션 커밋 후에 실행되도록 설정
    public void handleEntityChange(EntityChangedEvent event) throws Exception {
        Object entity = event.getEntity();

        // 어노테이션이 존재하는 엔티티인지 확인
        if (entity.getClass().isAnnotationPresent(BanwordEntity.class)) {
            banwordService.refreshBanwordTrie();
        } else if (entity.getClass().isAnnotationPresent(AllowwordEntity.class)) {
            banwordService.refreshAllowWordTrie();
        }
    }

}
