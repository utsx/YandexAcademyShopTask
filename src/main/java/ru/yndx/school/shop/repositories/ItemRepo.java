package ru.yndx.school.shop.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yndx.school.shop.entities.Item;

import java.security.SecureRandom;
import java.security.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ItemRepo extends JpaRepository<Item, String> {
    List<Item> findAllByParentId(String id);
    List<Item> findAllById(String id);
    Optional<Item> findById(String id);
    void deleteById(String id);
    @Query("select i.id from Item i where i.date >= :start and i.date <= :finish")
    List<String> findSales(@Param("start") java.sql.Timestamp start, @Param("finish") java.sql.Timestamp finish);
}
