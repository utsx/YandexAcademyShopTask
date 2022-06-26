package ru.yndx.school.shop.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Статистика вызванных методов")
public class Statistic {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String itemId;
    @NotNull
    private String method;
    @NotNull
    private Long value;
    @NotNull
    private String itemStatus;
    @NotNull
    private Timestamp date;

    public Statistic(final String itemId,
                     final String method,
                     final Long value,
                     final String itemStatus,
                     final Timestamp date){
        this.itemId = itemId;
        this.method = method;
        this.value = value;
        this.itemStatus = itemStatus;
        this.date = date;
    }

}

