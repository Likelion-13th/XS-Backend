package likelion13gi.demoXS.DTO.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class AddressRequest {
    private String address;
    private String addressDetail;
    private String zipcode;
}

