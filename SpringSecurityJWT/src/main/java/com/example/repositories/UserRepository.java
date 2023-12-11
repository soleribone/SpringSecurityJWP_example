package com.example.repositories;

import com.example.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    //creamos un metodo para buscar al usuario por su username:

    /*El uso de Optional en un método de un repositorio como UserRepository
    tiene que ver con la posibilidad de que la búsqueda no devuelva ningún resultado.
    Al utilizar Optional, estás indicando que el resultado de la operación puede ser nulo,
    y quien llame al método debe manejar este caso.*/
    Optional<UserEntity> findByUsername(String username);

    //"?1": significa que devuelva el primer parametro de la tabla que es el username
    @Query("select u from UserEntity u where u.username = ?1")
    Optional<UserEntity> getName(String username);


}
