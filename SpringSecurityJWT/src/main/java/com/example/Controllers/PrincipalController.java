package com.example.Controllers;

import com.example.Controllers.request.CreateUserDTO;
import com.example.models.ERole;
import com.example.models.RoleEntity;
import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController

public class PrincipalController {


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @GetMapping("/hello")
    public String hello(){
      return "Hello world secured";

    }

    @GetMapping("/helloSecured")
    public String helloSecured(){

        return "hello world secured";
    }

    /*
    @Valid: que se validen los datos que ingresan
    @RequestBody: los datos del "createUserDTO" vienen en el cuerpo de la request
    * */
    @PostMapping("/createUser")
    public ResponseEntity<?> createdUser(@Valid @RequestBody CreateUserDTO createUserDTO){

        /*nos envian los roles por el request como un string, y yo necesito transformarlos en un set:
        ->con stream lo conseguimos
        ->luego con builder construimos los objetos tipo RoleEntity para poder insertarlos en la db
         */


        Set<RoleEntity> roles = createUserDTO.getRoles().stream()
                .map(role-> RoleEntity.builder()
                        .name(ERole.valueOf(String.valueOf(role)))
                        .build())
                        .collect(Collectors.toSet());


        /*con la notacion @Builder que indicamos en UserEntity, porque vamos a poder construir
        nuestro objeto por partes:
        a nuestro objeto UserEntity le seteamos los datos que vienen en createUserDTO
        */

        UserEntity userEntity = UserEntity.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .email(createUserDTO.getEmail())
                .roles(roles)
                .build();

        userRepository.save(userEntity);
        CreateUserDTO userResponse= mapper.convertValue(userEntity, CreateUserDTO.class);

        return ResponseEntity.ok(userResponse);

    }

    //recibimos la request con el id del usuario que queremos borrar, lo modificamos
    //a Long con parseLong, y lo borramos con el metodo delete de repository
    @DeleteMapping("/deleteUser")
    public String deleteUser(@RequestParam String id){
        userRepository.deleteById(Long.parseLong(id));
        return "Se ha borrado el usuario con id" .concat(id);

    }

}
