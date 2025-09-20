package likelion13gi.demoXS.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ApiResponse;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.api.SuccessCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.authorize.jwt.CustomUserDetails;
import likelion13gi.demoXS.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    //토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급해주는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 요청입니다.")
    })
    @PostMapping("/reissue")
    public ApiResponse<JwtDto> reissue(HttpServletRequest request) {
        try {
            JwtDto jwt = userService.reissue(request);
            return ApiResponse.onSuccess(SuccessCode.USER_REISSUE_SUCCESS, jwt);
        }
        catch (GeneralException e) {
            throw e;
        } catch (Exception e) {
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "로그아웃", description = "로그아웃을 실행합니다.")
    @DeleteMapping("/logout")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청에 문제가 있습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "유효하지 않은 요청입니다.")
    })
    public ApiResponse<Void> logout(HttpServletRequest request) {
        userService.logout(request);
        return ApiResponse.onSuccess(SuccessCode.USER_LOGOUT_SUCCESS, null);
    }
}
