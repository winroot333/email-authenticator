package io.github.winroot33.dtos;

import lombok.*;

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