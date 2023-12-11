package com.example.Controllers.request;

import com.example.models.RoleEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {

    //este DTO tiene los mismos atributos que UserEntity
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String username;
    @NotBlank
    private String password;

    //necesitamos los roles aca para poder mandarlos desde nuestra peticion (request) http
    private Set<RoleEntity> roles;
    

}
