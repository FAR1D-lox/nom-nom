package com.nomnom.user_service.entity;

import com.nomnom.UserRole;
import com.nomnom.user_service.SubscribeVision;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.DEFAULT;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "subscribers_count", nullable = false)
    private Long subscribersCount = 0L;

    @Builder.Default
    @Column(name = "subscriptions_count", nullable = false)
    private Long subscriptionsCount = 0L;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "subscribe_vision", nullable = false)
    private SubscribeVision subscribeVision = SubscribeVision.VISIBLE;
}
