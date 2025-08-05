package likelion13gi.demoXS.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "UserReqDto")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @Schema(description = "이메일")
    private String phoneNumber;

    @Schema(description = "id(username)")
    private String usernickname;

    @Schema(description = "social type")
    private String provider;

}