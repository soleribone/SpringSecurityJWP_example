package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //avisamos que es una configuracion de spring
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll(); //permitir a todos acceso a este endpoint
                    auth.anyRequest().authenticated(); //cualquier otro rqeuest requiere autenticacion

                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .httpBasic()
                .and()
                .build();

    }

    //desde aqui creamos un usuario con acceso a la aplicacion por ahora:

    @Bean
    UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(); //creamos un usuario en memoria por ahora

        manager.createUser(
                User.withUsername("sole")
                        .password("1234")
                        .roles()
                        .build());

        return manager;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }

    /*
    ->para que este usuario pueda funcionar, tiene que ser administrado por algun obejto que administre la autenticacion:
    ->necesitamos un AUTENTICATION MANAGER, que es el objeto que se encarga de la autenticacion de los usuarios
    ->necesitamos un password encoder porque es lo que authentication manager requiere, porque no me dejar
    acceder a la aplicacion sin credenciales ni sin politicas de encriptacion de contraseñas: por eso creamos un passwordencoder
   */
   @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService())  //rle enviamos el usuario que vamos a autenticar
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }
}
