package ru.yndx.school.shop.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@RequiredArgsConstructor
@Data
public class ItemStats {
    private String id;
    private String name;
    private String date;
    private String parentId;
    private Long price;
    private String type;

    public ItemStats(final String id,
                     final String name,
                     final String date,
                     final String parentId,
                     final Long price,
                     final String type){
        this.id = id;
        this.name = name;
        this.date = date.toString().replace(" ", "T") + "00Z";
        this.parentId = parentId;
        this.price = price;
        this.type = type;
    }
}
