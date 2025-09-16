package likelion13gi.demoXS.login.authorize.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class JwtDto {
    private String accessToken;
    private String refreshToken;

    public JwtDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

/* 1) 왜 필요한가?
 - 서버에서 유저를 인증하기 위해서는 액세스 토큰과 리프레시 토큰이 필요합니다.
 - Jwt에서 반환하는 액세스 토큰과 리프레시 토큰만을 담는 저장소를 만들어두면, 토큰 정보만 꺼내서 쓰기 편합니다.
 2) 없으면/틀리면?
 - 액세스 토큰, 리프레시 토큰을 직접 Map<> 등으로 반환해야 하기에 쓰기 번거롭습니다.
 */
