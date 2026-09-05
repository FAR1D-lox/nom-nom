package com.nomnom.user_service.repository;

import com.nomnom.user_service.entity.SubscribeEntity;
import com.nomnom.user_service.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<SubscribeEntity, Long> {

    @Query(value = """
        SELECT u.*\s
        FROM users u\s
        INNER JOIN (SELECT subscriber_id FROM subscribes WHERE subscription_id = :user_id) s\s
        ON u.id = s.subscriber_id
        """, nativeQuery = true)
    List<UserEntity> findSubscribersById(@Param("user_id") Long userId, Pageable pageable);

    @Query(value = """
        SELECT u.*\s
        FROM users AS u\s
        INNER JOIN (SELECT subscription_id FROM subscribes WHERE subscriber_id = :user_id) AS s\s
        ON s.subscription_id = u.id
        """, nativeQuery = true)
    List<UserEntity> findSubscriptionsById(@Param("user_id") Long userId, Pageable pageable);

    boolean existsBySubscriberIdAndSubscriptionId(Long myId, Long otherId);

    @Modifying(clearAutomatically = true)
    @NativeQuery("""
            DELETE FROM subscribes
            WHERE subscriber_id = :my_id AND subscription_id = :other_id
            """)
    void deleteBySubscriberIdAndSubscriptionId(
            @Param("my_id") Long myId,
            @Param("other_id") Long otherId
    );
}
