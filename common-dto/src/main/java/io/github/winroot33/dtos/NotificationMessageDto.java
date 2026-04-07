package io.github.winroot33.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationMessageDto {
    private String code;
    private String email;
    private LocalDateTime expiresAt;
}