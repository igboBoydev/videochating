package com.abel.videochattingsystem.Schemas;


import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class LoginResponse {
    private String authToken;
}
