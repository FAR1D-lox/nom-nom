package com.nomnom.user_service.repository;

import com.nomnom.user_service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByUsername(String username);

    @Modifying(clearAutomatically = true)
    @NativeQuery("""
            UPDATE users SET subscribers_count = subscribers_count + 1 WHERE id = :user_id
            """)
    void incrementSubscribersCount(@Param("user_id") Long userId);

    @Modifying(clearAutomatically = true)
    @NativeQuery("""
            UPDATE users SET subscribers_count = GREATEST(subscribers_count - 1, 0) WHERE id = :user_id
            """)
    void decrementSubscribersCount(@Param("user_id") Long userId);

    @Modifying(clearAutomatically = true)
    @NativeQuery("""
            UPDATE users SET subscriptions_count = subscriptions_count + 1 WHERE id = :user_id
            """)
    void incrementSubscriptionsCount(@Param("user_id") Long userId);

    @Modifying(clearAutomatically = true)
    @NativeQuery("""
            UPDATE users SET subscriptions_count = GREATEST(subscriptions_count - 1, 0) WHERE id = :user_id
            """)
    void decrementSubscriptionsCount(@Param("user_id") Long userId);
}
