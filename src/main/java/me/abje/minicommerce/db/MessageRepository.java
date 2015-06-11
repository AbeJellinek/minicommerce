package me.abje.minicommerce.db;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface MessageRepository extends PagingAndSortingRepository<Message, Integer> {
    Optional<Message> findById(int id);
}
