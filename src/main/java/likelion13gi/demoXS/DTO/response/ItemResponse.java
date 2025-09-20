package likelion13gi.demoXS.DTO.response;



import com.fasterxml.jackson.annotation.JsonProperty;
import likelion13gi.demoXS.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private int price;
    private String brand;
    private String imagePath;
    private boolean isNew;

    @JsonProperty("isNew")
    public boolean getIsNew() {
        return isNew;
    }

    // Item → ItemResponseDto 변환
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getCompany(),
                item.getImagePath(),
                item.isNew()
        );
    }
}
