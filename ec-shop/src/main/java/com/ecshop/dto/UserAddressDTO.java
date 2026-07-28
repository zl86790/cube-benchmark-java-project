package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {

    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String streetAddress;
    private String zipCode;
    private Boolean isDefault;
}
