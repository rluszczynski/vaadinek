package com.example.application.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotEmpty
    @JsonProperty("firstname")
    private String firstName;
    @NotEmpty
    @JsonProperty("lastname")
    private String lastName;
    @NotEmpty
    @JsonProperty("email")
    private String email;
    @NotEmpty
    @JsonProperty("password")
    private String password;
    @NotEmpty
    @JsonProperty("phone")
    private String phone;
}
