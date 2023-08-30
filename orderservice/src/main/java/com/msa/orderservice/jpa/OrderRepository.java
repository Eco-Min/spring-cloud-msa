package com.msa.orderservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Iterable<OrderEntity> findByUserId(String userId);

    OrderEntity findByOrderId(String orderId);
}
