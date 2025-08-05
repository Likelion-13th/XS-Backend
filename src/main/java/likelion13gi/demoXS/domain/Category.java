package likelion13gi.demoXS.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    @Setter(AccessLevel.PRIVATE)
    private Long cId; // PK

    @Column(name = "category_name")
    private String categoryName;

    // 주문 정보 (1:N)
    @OneToMany(mappedBy = "category")
    @Column(name = "item_lists")
    @Builder.Default
    private List<Item> items = new ArrayList<>();
    public void addItems(Item item) {
        this.items.add(item);
        item.setCategory(this);
    }
}
