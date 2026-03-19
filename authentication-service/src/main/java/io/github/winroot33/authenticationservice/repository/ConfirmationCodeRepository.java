package io.github.winroot33.authenticationservice.repository;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, Long> {
    boolean existsByUserIdAndCodeAndExpiresAtAfter(Long userId, String code, LocalDateTime expirationDate);
}
