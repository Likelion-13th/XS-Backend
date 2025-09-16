package likelion13gi.demoXS.login.authorize.jwt;

import jakarta.persistence.*;
import likelion13gi.demoXS.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RefreshToken 엔티티
 * - 한 명의 사용자(User)당 Refresh Token 1개를 보관하는 테이블
 * - Shared PK(공유 PK) 대신 "별도 PK(id) + users_id UNIQUE" 방식으로 설계하여
 *   식별자 null 문제(null identifier) 및 연관관계 초기화 이슈를 피함
 */
@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ✅ 자체 PK 사용 (AUTO_INCREMENT)
    private Long id;

    /**
     * 사용자와 1:1 관계 (FK: users_id)
     * - 기본적으로 @OneToOne는 EAGER 지연로딩이 기본값이지만, 성능을 위해 LAZY로 명시
     * - users_id에는 UNIQUE 제약을 걸어 "사용자당 1행"만 허용
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", unique = true) // ✅ FK + UNIQUE 제약(사용자당 1개)
    private User user;

    /**
     * 실제 Refresh Token 문자열
     * - 보안을 위해 절대 로그에 원문 출력 금지
     * - 필요 시 길이 제한(@Column(length=...)) 및 NOT NULL 제약을 추가할 수 있음
     */
    private String refreshToken;

    /**
     * 만료 시각(예: epoch millis)
     * - 이름은 '유효기간'이지만 '남은 기간'이 아닌 '만료 시각'으로 사용 중
     * - 혼동을 줄이려면 expiresAt/expiryEpochMillis 같은 명칭을 고려
     */
    private Long ttl;

    /** 새 토큰으로 교체할 때 사용 */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /** 만료 시각 갱신 */
    public void updateTtl(Long ttl) {
        this.ttl = ttl;
    }

    // (선택) 가독성 향상을 위한 헬퍼 메서드 예시
    // public boolean isExpired() { return ttl != null && System.currentTimeMillis() >= ttl; }
}

/* 1) 왜 필요한가?
 - 리프레시 토큰 자체의 필요성 : 액세스 토큰은 기본적으로 유효기간이 짧다.(해커가 액세스 토큰을 가로채서 유저 행세를 하면 안되기 때문)
 - 하지만 우리 서비스에서 대부분 토큰을 이용한 인증 방식을 사용하기 때문에, 액세스 토큰은 계속 필요하다.
 - 그래서 유저에게 할당된 액세스 토큰을 계속 발급받을 수 있는 일종의 티켓이 필요한데, 그것이 리프레시 토큰이다.
 - 이 코드의 필요성 : 그런 리프레시 토큰을 엔티티로 선언(=DB에 저장)해주기 위해 필요하다.
 2) 없다면/틀리면?
 - 액세스 토큰을 재발급받기 위해서 유저가 반복적으로 로그인을 시도하는 불상사가 발생한다.
 - 액세스 토큰의 유효기간을 늘리면 위 문제를 완화할 수 있지만, 보안 이슈가 터졌을 때 피해가 막심하다.
 */