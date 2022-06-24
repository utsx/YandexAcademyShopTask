package ru.yndx.school.shop.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Item {

    @Id
    private String id;

    private String type;

    @NotNull(message = "Name can't be null")
    private String name;

    @Transient
    private List<Item> children = null;

    private String parentId;

    @NotNull(message = "Update time can't be null")
    private String date;

    private Long price;

    public Item(Object id, Object type, Object name, Object parentId, Object date, Long price) {
        this.id = id.toString();
        this.type = type.toString();
        this.name = name.toString();
        this.parentId = parentId == null ? null : parentId.toString();
        this.date = date.toString();
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
