package likelion13gi.demoXS.DTO.response;

import likelion13gi.demoXS.domain.Order;
import likelion13gi.demoXS.global.constant.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private String usernickname;
    private String item_name;
    private int quantity;
    private int totalPrice;
    private int finalPrice;
    private int mileageToUse; //사용한 마일리지
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getUsernickname(),
                order.getItem().getName(),//원래 getItemName이었음 (7.6 오후 5시 3분)
                order.getQuantity(),
                order.getTotalPrice(),
                order.getFinalPrice(),
                order.getTotalPrice() - order.getFinalPrice(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}