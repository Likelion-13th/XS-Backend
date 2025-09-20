package likelion13gi.demoXS.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "UserRequestDto")
    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserReqDto{
        private Long userId;
        private String providerId;
        private String usernickname;
    }
}