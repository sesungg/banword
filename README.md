# Banword - 아호코라식 기반 통합 금칙어 시스템

[우아한형제들 기술블로그](https://techblog.woowahan.com/15764/)의 아호코라식 알고리즘 구현을 참고하여 개발된 Spring Boot 기반 통합 금칙어 필터링 시스템입니다.

## 📋 목차

- [개요](#개요)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [설치 및 사용법](#설치-및-사용법)
- [API 문서](#api-문서)
- [예제](#예제)
- [설정](#설정)
- [기여하기](#기여하기)

## 🎯 개요

Banword는 아호코라식(Aho-Corasick) 알고리즘을 활용하여 효율적인 다중 패턴 매칭을 수행하는 금칙어 필터링 시스템입니다. 우회 문자(숫자, 공백, 외국어 등)를 자동으로 필터링하고, 허용 단어를 통해 예외 처리를 지원합니다.

### 주요 특징

- **고성능 다중 패턴 매칭**: 아호코라식 알고리즘으로 O(n+m) 시간 복잡도
- **우회 문자 자동 필터링**: 숫자, 공백, 외국어 등 우회 문자 자동 제거
- **허용 단어 지원**: 금칙어와 허용 단어의 우선순위 처리
- **Spring Boot 자동 설정**: `@BanwordField` 어노테이션으로 간편한 사용
- **위치 기반 검증**: 탐지된 금칙어의 정확한 위치 정보 제공

## 🚀 주요 기능

### 1. 우회 문자 필터링
- **숫자 필터링**: `NUMBERS` 정책으로 숫자 제거
- **공백 필터링**: `WHITESPACES` 정책으로 공백 제거  
- **외국어 필터링**: `FOREIGNLANGUAGES` 정책으로 한글/영어 외 문자 제거

### 2. 허용 단어 처리
- 금칙어가 허용 단어에 포함된 경우 자동 제외
- 허용 단어 우선순위 적용

### 3. 위치 매핑
- 원본 문장과 필터링된 문장 간의 위치 매핑
- 탐지된 금칙어의 정확한 원본 위치 제공

## 🛠 기술 스택

- **Java 11**
- **Spring Boot 2.7.18**
- **Spring Validation**
- **AhoCorasick 0.6.3**
- **Gradle**

## 🏗 아키텍처

```
src/main/java/com/banword/
├── annotation/
│   └── BanwordField.java          # 금칙어 검증 어노테이션
├── config/
│   └── BanwordAutoConfiguration.java  # Spring Boot 자동 설정
├── core/
│   ├── BanwordDetection.java      # 금칙어 탐지 결과
│   ├── BypassCharacterFilter.java # 우회 문자 필터링
│   └── TrieBuilder.java           # 아호코라식 트라이 빌더
├── enums/
│   └── BanwordFilterPolicy.java   # 필터링 정책 열거형
├── model/
│   ├── Allowword.java             # 허용 단어 모델
│   ├── Banword.java               # 금칙어 모델
│   ├── BanwordValidationResult.java # 검증 결과
│   ├── FilteredCharacter.java     # 필터링된 문자
│   └── FilteredResult.java        # 필터링 결과
├── provider/
│   ├── AllowwordProvider.java     # 허용 단어 제공자 인터페이스
│   ├── BanwordProvider.java       # 금칙어 제공자 인터페이스
│   ├── DefaultAllowwordProvider.java # 기본 허용 단어 제공자
│   └── DefaultBanwordProvider.java   # 기본 금칙어 제공자
└── service/
    ├── BanwordFieldValidator.java # 어노테이션 검증기
    └── BanwordValidator.java      # 핵심 검증 서비스
```

## 📦 설치 및 사용법

### 1. 의존성 추가

```gradle
dependencies {
    implementation 'com.banword:banword:0.0.1-SNAPSHOT'
}
```

### 2. 기본 사용법

#### 어노테이션 기반 사용

```java
public class UserRequest {
    @BanwordField(policies = {BanwordFilterPolicy.NUMBERS, BanwordFilterPolicy.WHITESPACES})
    private String content;
    
    // getter, setter
}
```

#### 서비스 직접 사용

```java
@Service
public class ContentService {
    
    @Autowired
    private BanwordValidator banwordValidator;
    
    public void validateContent(String content) {
        Set<BanwordFilterPolicy> policies = Set.of(
            BanwordFilterPolicy.NUMBERS,
            BanwordFilterPolicy.WHITESPACES
        );
        
        BanwordValidationResult result = banwordValidator.validate(content, policies);
        
        if (result.isFoundBanword()) {
            // 금칙어 발견 처리
            List<BanwordDetection> detections = result.getDetectedBanwords();
            // ...
        }
    }
}
```

### 3. 커스텀 제공자 구현

#### 금칙어 제공자

```java
@Component
public class CustomBanwordProvider implements BanwordProvider {
    
    @Override
    public List<Banword> provideBanword() {
        return Arrays.asList(
            new Banword("금칙어1"),
            new Banword("금칙어2")
        );
    }
}
```

#### 허용 단어 제공자

```java
@Component
public class CustomAllowwordProvider implements AllowwordProvider {
    
    @Override
    public List<Allowword> provideAllowwords() {
        return Arrays.asList(
            new Allowword("허용단어1"),
            new Allowword("허용단어2")
        );
    }
}
```

## 📚 API 문서

### BanwordValidator

#### validate(String originSentence, Set<BanwordFilterPolicy> policies)
- **설명**: 문장에서 금칙어를 검증합니다.
- **매개변수**:
  - `originSentence`: 검증할 원본 문장
  - `policies`: 적용할 필터링 정책들
- **반환값**: `BanwordValidationResult` - 검증 결과

### BanwordValidationResult

#### isFoundBanword()
- **설명**: 금칙어 발견 여부를 반환합니다.
- **반환값**: `boolean`

#### getDetectedBanwords()
- **설명**: 탐지된 금칙어 목록을 반환합니다.
- **반환값**: `List<BanwordDetection>`

### BanwordDetection

#### getBanword()
- **설명**: 탐지된 금칙어를 반환합니다.
- **반환값**: `String`

#### getStartPosition()
- **설명**: 금칙어 시작 위치를 반환합니다.
- **반환값**: `int`

#### getEndPosition()
- **설명**: 금칙어 종료 위치를 반환합니다.
- **반환값**: `int`

## 💡 예제

### 기본 검증 예제

```java
@RestController
public class ContentController {
    
    @Autowired
    private BanwordValidator banwordValidator;
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateContent(@RequestBody String content) {
        Set<BanwordFilterPolicy> policies = Set.of(
            BanwordFilterPolicy.NUMBERS,
            BanwordFilterPolicy.WHITESPACES
        );
        
        BanwordValidationResult result = banwordValidator.validate(content, policies);
        
        if (result.isFoundBanword()) {
            return ResponseEntity.badRequest()
                .body("금칙어가 발견되었습니다: " + 
                      result.getDetectedBanwords().stream()
                          .map(BanwordDetection::getBanword)
                          .collect(Collectors.joining(", ")));
        }
        
        return ResponseEntity.ok("검증 통과");
    }
}
```

### 우회 문자 필터링 예제

```java
// 입력: "금칙어123"
// 필터링 정책: NUMBERS
// 결과: "금칙어" (숫자 제거됨)

// 입력: "금칙어 공백"
// 필터링 정책: WHITESPACES  
// 결과: "금칙어공백" (공백 제거됨)

// 입력: "금칙어@#$"
// 필터링 정책: FOREIGNLANGUAGES
// 결과: "금칙어" (특수문자 제거됨)
```

## ⚙ 설정

### 필터링 정책

| 정책 | 설명 | 정규식 |
|------|------|--------|
| `NUMBERS` | 숫자 제거 | `[\\p{N}]` |
| `WHITESPACES` | 공백 제거 | `[\\s]` |
| `FOREIGNLANGUAGES` | 한글/영어 외 문자 제거 | `[\\p{L}&&[^ㄱ-ㅎ가-힣ㅏ-ㅣa-zA-Z]]` |

### 어노테이션 설정

```java
@BanwordField(
    policies = {BanwordFilterPolicy.NUMBERS, BanwordFilterPolicy.WHITESPACES},
    message = "금칙어가 포함되어 있습니다: %s"
)
private String content;
```

## 🙏 참고 자료

- [우아한형제들 기술블로그 - 아호코라식 알고리즘](https://techblog.woowahan.com/15764/)
- [AhoCorasick 라이브러리](https://github.com/robert-bor/aho-corasick) 