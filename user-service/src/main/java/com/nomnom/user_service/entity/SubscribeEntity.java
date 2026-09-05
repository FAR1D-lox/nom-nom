package com.nomnom.user_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscribes",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "subscriber_id",
                "subscription_id"
        }
))
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SubscribeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @CreationTimestamp
    @Column(name = "subscription_from", nullable = false, updatable = false)
    private LocalDateTime subscriptionFrom;
}
