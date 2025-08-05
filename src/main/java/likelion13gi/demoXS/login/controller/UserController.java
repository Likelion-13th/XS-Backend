package likelion13gi.demoXS.login.controller;

import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ApiResponse;
import likelion13gi.demoXS.global.api.SuccessCode;
//import likelion13gi.demoXS.login.auth.dto.JwtDto;
//import likelion13gi.demoXS.login.auth.mapper.CustomUserDetails;
import likelion13gi.demoXS.login.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "회원", description = "회원 관련 api 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
}
