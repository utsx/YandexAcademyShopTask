package ru.yndx.school.shop.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yndx.school.shop.entities.Item;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

public interface ItemRepo extends JpaRepository<Item, String> {
    List<Item> findAllByParentId(String id);
    Optional<Item> findById (String id);
    void deleteById(String id);
}
