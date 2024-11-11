package com.banword;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BanwordServiceTest {

    @Autowired
    private BanwordValidator banwordValidator;

    @Mock
    private BanwordLoader banwordLoader;

    @Test
    void validate_whenContainsBanword_shouldReturnTrue() throws Exception {
        when(banwordLoader.loadBanword(anyString())).thenReturn(List.of("banword"));

        BanwordValidationResult result = banwordValidator.validate("This is a banword test.");

        System.out.println("result.getDetectedBanwords() = " + result.getDetectedBanwords());
    }

}