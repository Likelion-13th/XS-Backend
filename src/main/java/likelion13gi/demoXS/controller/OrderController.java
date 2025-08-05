package likelion13gi.demoXS.controller;

import io.swagger.v3.oas.annotations.Operation;
import likelion13gi.demoXS.DTO.request.OrderCreateRequest;
import likelion13gi.demoXS.DTO.response.ItemResponse;
import likelion13gi.demoXS.DTO.response.OrderResponse;
import likelion13gi.demoXS.domain.Category;
import likelion13gi.demoXS.domain.Order;
import likelion13gi.demoXS.domain.User;
import likelion13gi.demoXS.global.api.ApiResponse;
import likelion13gi.demoXS.global.api.ErrorCode;
import likelion13gi.demoXS.global.api.SuccessCode;
import likelion13gi.demoXS.global.exception.GeneralException;
//import likelion13gi.demoXS.login.auth.mapper.CustomUserDetails;
import likelion13gi.demoXS.login.service.UserService;
import likelion13gi.demoXS.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    // 주문 생성
    @PostMapping
    @Operation(summary = "주문 생성", description = "로그인한 사용자의 주문을 생성합니다.")
    public ApiResponse<?> createOrder(
            @RequestBody String providerId,
            @RequestBody OrderCreateRequest request
    ) {
        User user = userService.findByProviderId(providerId);

        log.info("[STEP 1] 주문 생성 요청 수신...");
        try {
            OrderResponse newOrder = orderService.createOrder(request, user);
            log.info("[STEP 2] 주문 생성 성공");
            return ApiResponse.onSuccess(SuccessCode.ORDER_CREATE_SUCCESS, newOrder);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 주문 생성 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e){
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    //개별 주문 조회
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 개별 조회", description = "로그인한 사용자의 주문을 개별 조회합니다.")
    public ApiResponse<?> deleteOrderById(@PathVariable Long orderId) {
        log.info("[STEP 1] 개별 주문 조회 요청 수신...");

        try{
            OrderResponse order = orderService.getOrderById(orderId);
            log.info("[STEP 2] 개별 주문 조회 성공");
            return ApiResponse.onSuccess(SuccessCode.ORDER_GET_SUCCESS, order);
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 개별 주문 조회 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e){
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

    }

    //모든 주문 목록 조회
    @GetMapping
    @Operation(summary = "모든 주문 조회", description = "로그인한 사용자의 모든 주문을 목록으로 조회합니다.")
    public ApiResponse<?> getAllOrders(
            @RequestBody String providerId
    ) {
        User user = userService.findByProviderId(providerId);
        List<OrderResponse> orders = orderService.getAllOrders(user);
        /*if (orders.isEmpty()) {
            return ApiResponse.onFailure(
                    ErrorCode.ORDER_NOT_FOUND,
                    "등록된 주문이 없습니다.");}
        return ApiResponse.onSuccess(SuccessCode.ORDER_LIST_SUCCESS,orders);*/

        // 주문이 없더라도 성공 응답 + 빈 리스트 반환
        if (orders.isEmpty()) {
            return ApiResponse.onSuccess(SuccessCode.ORDER_LIST_EMPTY, Collections.emptyList());
        }
        return ApiResponse.onSuccess(SuccessCode.ORDER_LIST_SUCCESS, orders);
    }

    //주문 취소
    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "로그인한 사용자의 주문을 취소합니다.")
    public ApiResponse<?> cancelOrder(@PathVariable Long orderId) {
        log.info("[STEP 1] 주문 취소 요청 수신");

        try {
            orderService.cancelOrder(orderId); // ❌ boolean X → ✅ void로 바뀐 메서드
            log.info("[STEP 2] 주문 취소 성공");
            return ApiResponse.onSuccess(SuccessCode.ORDER_CANCEL_SUCCESS, "주문이 성공적으로 취소되었습니다.");
        } catch (GeneralException e) {
            log.error("❌ [ERROR] 주문 취소 중 예외 발생: {}", e.getReason().getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ [ERROR] 알 수 없는 예외 발생: {}", e.getMessage());
            throw new GeneralException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}


