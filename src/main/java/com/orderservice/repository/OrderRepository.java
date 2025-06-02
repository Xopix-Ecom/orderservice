package com.orderservice.repository;

import com.orderservice.model.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    Page<Orders> findByUserId(String userId, Pageable pageable);}

