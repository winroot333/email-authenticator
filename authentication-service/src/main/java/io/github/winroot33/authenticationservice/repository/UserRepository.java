package io.github.winroot33.authenticationservice.repository;

import io.github.winroot33.authenticationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
