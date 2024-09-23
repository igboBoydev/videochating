package com.abel.videochattingsystem.Schemas;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class LoginRequest {
    private final String email;
    private final String password;
}
