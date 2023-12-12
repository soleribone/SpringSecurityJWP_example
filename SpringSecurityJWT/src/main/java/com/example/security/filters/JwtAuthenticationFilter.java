package com.example.security.filters;

import com.example.models.UserEntity;
import com.example.security.jwt.JwtUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    /*Sobreescribimos dos metodos:
    -> AttemptAuthentication: intento de autenticacion, lo que vamos a hacer cuando el usuario intenta autenticarse
    -> Recuperamos el usuario que se intent+o autenticar
    -> dentro de request viene el usuario que intentamos autenticar, y tenemos que extraerlo del json,
     por eso lo mapeamos con la libreria jackson
     ->   userEntity = new ObjectMapper().readValue(request.getInputStream(),UserEntity.class): esta linea indica que
     tomo los parametros username y password del json y los conviertes a un objeto tipo UserEntity
     ->Luego guardamos estos datos en las variables username y password

    ->UsernamePasswordAuthenticationToken: Si lo que hacemos en el try no lanza ningun error, entonces nos podemos autenticar,
    por eso generamos un objeto UserNamePasswordtoken que recibe el usuario y la contraseña

    ->  return getAuthenticationManager().authenticate(authenticationToken): esta la clase de "UserAuthienticationFilter"
     que administra la autenticacion, al metodo "autenticate" le pasamos el token que generamos cuando se crea el usuario.

    -> Este método no genera un token de autenticación en sí mismo, pero prepara la información necesaria para que el AuthenticationManager lo haga.
     El AuthenticationManager tomará el UsernamePasswordAuthenticationToken creado en este método y llevará a cabo el proceso de autenticación,
     que incluirá verificar las credenciales del usuario y decidir si la autenticación es exitosa o no.
    */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UserEntity userEntity = null;
        String username="";
        String password = "";

        try {

            userEntity = new ObjectMapper().readValue(request.getInputStream(),UserEntity.class);
            username = userEntity.getUsername();
            password = userEntity.getPassword();
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username,password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    //SI EL METODO ATTEMPTAUTHENTICATION ES EXITOSO, VENIMOS A ESTE METODO:
    /*SuccessfulAuthentication: autenticacion exitosa, lo que vamos a hacer cuando se autentique correctamente
    -> despues de la autenticacion generamos el token

    ->Para generar el token necesitamos los datos del usuario: username, password, roles, y para eso
    necesito una nueva clase User de springsecurity (userdetails)
    ->Para recuperar los datos del usuario que se ha logueado, extraemos esa info de "authResult.getPrincipal()"
    y obtenemos el objeto que tiene los datos del usuario

    -> String token = jwtUtils.generateAccessToken(userDetails.getUsername()): Generamos el token con la clase JwtUtils

    ->  response.addHeader("Authorization", token): Luego respondemos a la solicitud de login con el token de acceso,
    y mandamos el token en el header de la respuesta

    ->Tambien mandamos el token en el cuerpo del response
    */

    //inyectamos jwtUtils con un constructor
    private JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils){
        this.jwtUtils=jwtUtils;
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        User user = (User) authResult.getPrincipal();

        String token = jwtUtils.generateAccessToken(user.getUsername()); //con esto generamos el token de acceso a los endpoints

        response.addHeader("Authorization", token); //mandamos el token en el header de la respuesta

        Map<String,Object> httpResponse = new HashMap<>(); //con este map vamos a mapear la respuesta para conmvertirla en un json

        httpResponse.put("token",token);
        httpResponse.put("Message", "Authentication correct");
        httpResponse.put("Username", user.getUsername());

        //convertimos el map  httpResponse en json con jackson:
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        //otras cosas que ponemos en el json:
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush(); //con esto nos aseguramos que se escriba correctamente

        super.successfulAuthentication(request, response, chain, authResult);
    }
}
