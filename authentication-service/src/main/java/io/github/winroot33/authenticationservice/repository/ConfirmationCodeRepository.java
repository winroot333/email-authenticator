package io.github.winroot33.authenticationservice.repository;

import io.github.winroot33.authenticationservice.entity.ConfirmationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с ConfirmationCode
 *
 * @author Mikhail Vasiliev (winroot123@gmail.com)
 */
@Repository
public interface ConfirmationCodeRepository extends JpaRepository<ConfirmationCode, UUID> {
    Optional<ConfirmationCode> findByUserIdAndCode(Long userId, String code);
}
