package com.nomnom.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "subscribe",
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

    @Column(name = "subscription_from", nullable = false)
    private LocalDateTime subscriptionFrom;
}
