package com.example.Services;

import com.example.models.UserEntity;
import com.example.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    /*
    ->Este metodo springsecurity lo usa para asegurarse de cual es el usuario
    que se va a consultar, es para indicarle a spring de donde va a sacar los usuarios
    **/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //ponemos el orElseThrow por que el metodo del repositorio es un optional
        //con esto recuperamos el usuario de la base de datos
        UserEntity userDetails = userRepository.findByUsername()
                                    .orElseThrow(()-> new UsernameNotFoundException("El usuario no existe"));

        return null;
    }
}
