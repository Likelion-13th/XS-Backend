package likelion13gi.demoXS.login.authorize.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion13gi.demoXS.global.api.ApiResponse;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.api.SuccessCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import likelion13gi.demoXS.login.authorize.dto.JwtDto;
import likelion13gi.demoXS.login.dto.UserRequestDto;
import likelion13gi.demoXS.login.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "토큰", description = "액세스/리프레시 토큰 API")
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    private final UserService userService;

    @Operation(summary = "토큰 생성(회원가입&로그인)", description = "provider_id 기반으로 토큰을 반환하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER_200", description = "회원가입 & 로그인 성공")
    })
    @PostMapping("/generate")
    public ApiResponse<JwtDto> generateToken(@RequestBody UserRequestDto.UserReqDto userReqDto) {
        try {
            String providerId = userReqDto.getProviderId();
            JwtDto jwt = userService.jwtMakeSave(providerId);
            return ApiResponse.onSuccess(SuccessCode.USER_LOGIN_SUCCESS, jwt);
        } catch (GeneralException e) {
            log.error("회원가입/로그인 과정에서 문제가 발생함 : {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("예기치 못한 오류 발생 : {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


}
