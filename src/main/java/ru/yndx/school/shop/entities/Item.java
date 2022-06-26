package ru.yndx.school.shop.entities;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import javax.xml.bind.DatatypeConverter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Сущность товара/категории")
public class Item{

    @Id
    @Schema(description = "ID товара/категории", example = "d515e43f-f3f6-4471-bb77-6b455017a2d2")
    private String id;

    @Schema(description = "Тип товара/категории", example = "CATEGORY")
    private String type;

    @NotNull(message = "Name can't be null")
    @Schema(description = "Имя товара/категории",  example = "Смартфоны")
    private String name;

    @Transient
    @Schema(description = "Лист потомков")
    private List<Item> children = null;

    @Schema(description = "ID родителя товара/категории", example = "069cb8d7-bbdd-47d3-ad8f-82ef4c269df1")
    private String parentId;

    @NotNull(message = "Update time can't be null")
    @Schema(description = "Дата обновления товара/категории", example = "2022-02-01T12:00:00.000Z")
    private Timestamp date;

    @Schema(description = "Цена товара/категории", example = "59999")
    private Double price;

    public void setPrice(Double price) {
        this.price = price;
    }

    @SneakyThrows
    public Item(Object id, Object type, Object name, Object parentId, Object date, Double price) {
        this.id = id.toString();
        this.type = type.toString();
        this.name = name.toString();
        this.parentId = parentId == null ? null : parentId.toString();
        this.date = new Timestamp(DatatypeConverter.parseDateTime(date.toString()).getTimeInMillis());
        this.price = price;
    }

    @Override
    public String toString() {
        return "{item[" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", date='" + date + '\'' +
                ", price=" + price +
                ", children=" + children +
                "]}";
    }
}


