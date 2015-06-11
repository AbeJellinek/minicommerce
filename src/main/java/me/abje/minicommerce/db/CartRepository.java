package me.abje.minicommerce.db;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface CartRepository extends PagingAndSortingRepository<Cart, Integer> {
    Optional<Message> findById(int id);
}
