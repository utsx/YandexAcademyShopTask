package ru.yndx.school.shop.entities;

import lombok.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Setter
@Getter
public class ItemReturn{
    private String type;
    private String name;
    private String id;
    private Long price;
    private String parentId;
    private String date;
    private List<ItemReturn> children = null;

    public ItemReturn(final String type,
                      final String name,
                      final String id,
                      final Long price,
                      final String parentId,
                      final Timestamp date)
    {
        this.type = type;
        this.name = name;
        this.id = id;
        this.price = !Objects.equals(price, 0) ? price : null;
        this.parentId = parentId;
        this.date = date.toString().replace(" ", "T") + "0Z";
    }

}