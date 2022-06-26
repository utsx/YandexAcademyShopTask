package ru.yndx.school.shop.entities;


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
public class Item{

    @Id
    private String id;

    private String type;

    @NotNull(message = "Name can't be null")
    private String name;

    @Transient
    private List<Item> children = null;

    private String parentId;

    @NotNull(message = "Update time can't be null")
    private Timestamp date;

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


