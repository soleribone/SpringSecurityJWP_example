package com.example.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component //va a ser un componente administrado por spring
public class JwtUtils {

    @Value("${jwt.secret.key}") //ingresamos el valor que hay en el application properties con el value
    private String secretKey;
    /*este atributo nos va a ayudar a "firmar" nuestro metodo,
    al token que generamos lo tenemos que firmar, y eso quiere decir que
    que tenemos que ponerle el sello de que fuimos NOSOTROS Los que generamos
    ese token. Para esto buscamos una pagina que genere keys (key generators) y pegamos
    esa key que generamos en application.yml*/

    @Value("${jwt.time.expiration}")
    private String timeExpiration;
    /*tiempo que va a ser valido el token en milisegundos (si queremos que dure un dia
    pasamos el da a milisegundos)*/

    //GENERAR TOKEN DE ACCESO:
    /*
    .setSubject(username): el sujeto que lo genera
    .setIssuedAt(new Date(System.currentTimeMillis())) : fecha de creacion del token que lo creamos en milisegundos
    .setExpiration(new Date (System.currentTimeMillis() + Long.parseLong(timeExpiration))) : creamos la fecha de expiracion del token,
            con la fecha actual a la que le sumamnos cuanto dura el token, to.do en milisegundos, lo parseamos a long porque timeExpiration es un string
    .signWith(): ingresamos la firma del metodo que la habiamos guardado en secretKey, pero antes
                la decodificamos y volvemos a codificar en el metodo "getSignatureKey()", as ingresamos una firma encriptada a este metodo.
                Tambien indicamos de que forma lo volvemos a encriptar con "SignatureAlgorithm"
    .compact(): Después de configurar todas las reclamaciones (claims) del token utilizando métodos como setSubject, setIssuedAt, setExpiration, etc., el método compact() es llamado para generar el token JWT final. La llamada a compact() realiza la serialización y firma del token, produciendo una cadena compacta que puede ser enviada y verificada por las partes que participan en la comunicación.

     */
    public String generateAccessToken(String username){

        return Jwts.builder()
                .setSubject(username)   //el sujeto que lo genera
                .setIssuedAt(new Date(System.currentTimeMillis()))  //fecha de creacion del token que lo creamos en milisegundos
                .setExpiration(new Date (System.currentTimeMillis() + Long.parseLong(timeExpiration)))

                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    //VALIDAR EL TOKEN DE ACCESO: necesitamos validar que el token que usa el usuario para ingresar a la aplicacion es correcto
    /*
    ->Jwts.parserBuilder: lee el token
    ->.setSigningKey(): verifica que el token este firmado
    ->.parseClaimsJws(token): Este método realiza la operación de parseo y verificación del token JWT.
    Toma como parámetro el token que se va a validar.
    ->.getBody(): Una vez que el token ha sido parseado y verificado con éxito, esta parte extrae las reclamaciones (claims)
    contenidas en la carga útil (payload) del token. Este método devuelve un objeto que representa las reclamaciones del token.
    -> Si falla algo en la comprobacion del token, se devuelve un false, de lo contrario se devuelve true
     */
    public boolean isTokenValid(String token){

        try{

            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        }catch(Exception e){
            System.out.println("Token invalido, error: " .concat(e.getMessage()));
            return false;

        }

    }


    //OBTENER FIRMA DE ACCSEO:
    /*
    ->Con este metodo obtenemos la firma de nuestro token como un array de bytes
    ->byte[] keyBytes = Decoders.BASE64.decode(secretKey) : decodificamos la clave de secretKey, y luego la vamos a volver
    a encriptar con hmacShaKeyFor
    -> Luego, se utiliza el método hmacShaKeyFor de la clase Keys para crear una clave HMAC a partir
    de los bytes de la clave secreta. La clave HMAC es utilizada comúnmente en operaciones de firma
     para garantizar la integridad y autenticidad de los datos.

     */
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes); //devolvemos el array de bytes
    }

    //NECESITAMOS UN METODO QUE NOS PERMITA TENER LAS CARACTERISTICAS DEL TOKEN:
    //METODO PARA OBTENER LOS CLAIMS DEL TOKEN:
    public Claims extractAllClaims(String token){
        Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();


    }

    //OBTENER UN SOLO CLAIM:
    public <T> T getClaim(String token, Function<Claims,T> claimsTFunction){
         Claims claims = extractAllClaims(token);
         return claimsTFunction.apply(claims);


    }

    //OBTENER EL USERNAME DEL TOKEN:
    /*con el usuario que esta dentro del token vamos a hacer
    la autenticacion*/
    public String getUsernameFromToken(String token){

        return getClaim(token, Claims::getSubject);

    }



}
