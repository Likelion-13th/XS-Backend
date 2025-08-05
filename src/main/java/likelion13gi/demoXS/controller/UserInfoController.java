package likelion13gi.demoXS.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion13gi.demoXS.DTO.response.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "회원", description = "회원 관련 API 입니다.")
public class UserInfoController {

    @Operation(summary = "사용자 조회", description = "사용자의 정보를 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<UserInfoResponse> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(new UserInfoResponse(userId, "홍길동"));
    }
}
