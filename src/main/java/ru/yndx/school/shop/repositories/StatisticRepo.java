package ru.yndx.school.shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.yndx.school.shop.entities.Statistic;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

public interface StatisticRepo extends JpaRepository<Statistic, Long> {

    List<Statistic> findAllByItemId(@NotNull String itemId);

    void deleteByItemId(String itemId);

    List<Statistic> findByItemId(String itemId);

    @Query("select s from Statistic s WHERE s.itemId = :itemId and s.date >= :start and s.date <= :finish")
    List<Statistic> findAllStatisticByItemIdInBetween(@Param("itemId") String itemId, @Param("start")
            Timestamp start, @Param("finish") Timestamp finish);
}
