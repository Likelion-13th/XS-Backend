package likelion13gi.demoXS.domain;

import jakarta.persistence.*; // * = 이 패키지 안에 있는 모든 파일 다 가져온다!
import java.time.LocalDateTime;
import likelion13gi.demoXS.domain.entity.BaseEntity;
import likelion13gi.demoXS.domain.Category;
import likelion13gi.demoXS.global.constant.OrderStatus;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    @Setter(AccessLevel.PRIVATE)
    private Long id; // PK

    @Column(nullable = false)
    @Setter
    private int price;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private String company;

    @Column(nullable = false)
    @Setter
    private boolean news;

    // Category 와 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cId")
    @Setter
    private Category category;



    // Order와의 관계
    @OneToMany(mappedBy = "item")
    private List<Order> orders = new ArrayList<>();

    private boolean isNewProduct() {
        LocalDateTime productCreationTime = this.getCreatedAt();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3); // 3개월 전 날짜 계산
        return productCreationTime.isAfter(threeMonthsAgo);
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public Item(String name, int price) {
        if (price < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 합니다.");
        }
        this.name = name;
        this.price = price;
        this.news = isNewProduct();
    }

}
