package likelion13gi.demoXS.DTO.response;

import likelion13gi.demoXS.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemResponse {
    private Long itemId;
    private int price;
    private String item_name;
    private String company;
    private boolean news;
    private LocalDateTime createdAt;

    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getPrice(),
                item.getName(),
                item.getCompany(),
                item.isNews(),
                item.getCreatedAt()
        );
    }
}