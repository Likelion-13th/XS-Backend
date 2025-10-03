package likelion13gi.demoXS.S3;

import io.swagger.v3.oas.annotations.Operation;
import likelion13gi.demoXS.global.api.ApiResponse;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.api.SuccessCode;
import likelion13gi.demoXS.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "S3 파일 업로드", description = "AWS S3에 이미지를 업로드하고 URL을 반환합니다.")
    public ApiResponse<?> uploadFile(@RequestParam("photo") MultipartFile[] files) {
        List<String> fileUrls = new ArrayList<>();

        for(MultipartFile file: files) {
            // 1. 파일 유효성 검사
            if (file.isEmpty()) {
                throw new GeneralException(ErrorCode.S3_FILE_EMPTY);
            }

            // 2. 파일 형식 검사 (img로 제대로 오는가?)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new GeneralException(ErrorCode.S3_INVALID_FILE_TYPE);
            }

            // 3. S3 업로드
            String fileUrl = Optional.ofNullable(s3Service.uploadFile(file))
                    .orElseThrow(() -> new GeneralException(ErrorCode.S3_UPLOAD_FAILED));

            fileUrls.add(fileUrl);
        }
        //4.응답 변환
        return ApiResponse.onSuccess(SuccessCode.S3_UPLOAD_SUCCESS, fileUrls);
    }
}
