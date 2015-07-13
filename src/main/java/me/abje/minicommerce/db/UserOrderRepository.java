package me.abje.minicommerce.db;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserOrderRepository extends PagingAndSortingRepository<UserOrder, Integer> {
}
