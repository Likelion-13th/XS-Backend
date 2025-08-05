package likelion13gi.demoXS.repository;

import likelion13gi.demoXS.domain.Order;
import likelion13gi.demoXS.global.constant.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime dateTime);
    List<Order> findOrderByTotalPrice(int totalPrice);
    List<Order> findOrderByFinalPrice(int finalPrice);
}