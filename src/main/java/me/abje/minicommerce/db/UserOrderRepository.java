package me.abje.minicommerce.db;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserOrderRepository extends PagingAndSortingRepository<UserOrder, Integer> {
    public List<UserOrder> findByFulfilledFalseAndShippableTrue();

    public List<UserOrder> findAll();
}
