package ru.yndx.school.shop.components;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import nonapi.io.github.classgraph.json.JSONUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yndx.school.shop.entities.*;
import ru.yndx.school.shop.repositories.ItemRepo;
import ru.yndx.school.shop.repositories.StatisticRepo;

import javax.xml.bind.DatatypeConverter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Component
@AllArgsConstructor
@NoArgsConstructor
public class ItemComponent {

    private ItemRepo itemRepo;

    private StatisticRepo statisticRepo;

    private Map<String, Long> categoryOfferCount = new HashMap<>();

    @Autowired
    public ItemComponent(ItemRepo itemRepo, StatisticRepo statisticRepo) {
        this.itemRepo = itemRepo;
        this.statisticRepo = statisticRepo;
    }

    //Methods for return item
    public ResponseEntity getItemByIdToReturn(String id) {
        ItemReturn ans = findChildrenByRootIdToReturn(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ans);
    }

    private ItemReturn findChildrenByRootIdToReturn(String id) {
        Queue<ItemReturn> notProcessedItems = new LinkedList<>();
        Optional<Item> optionalItem = itemRepo.findById(id);
        if (optionalItem.isEmpty()) {
            return null;
        }
        Item ans = optionalItem.get();
        ItemReturn itemReturn = new ItemReturn(ans.getType(), ans.getName(), ans.getId(),
                ans.getPrice().longValue(), ans.getParentId(), ans.getDate());
        notProcessedItems.add(itemReturn);
        while (!notProcessedItems.isEmpty()) {
            ItemReturn root = notProcessedItems.poll();
            List<Item> itemChildren = itemRepo.findAllByParentId(root.getId());
            List<ItemReturn> children = new ArrayList<>();
            itemChildren.forEach(item -> {
                children.add(new ItemReturn(item.getType(), item.getName(), item.getId(),
                        item.getPrice().longValue(), item.getParentId(), item.getDate()));
            });
            if (!children.isEmpty()) {
                root.setChildren(new ArrayList<>());
                notProcessedItems.addAll(children);
                root.getChildren().addAll(children);
            }
        }
        return itemReturn;
    }

    //Methods for internal interaction
    public ResponseEntity getItemById(String id) {
        Item ans = findChildrenByRootId(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ans);
    }

    private Item findChildrenByRootId(String id) {
        Queue<Item> notProcessedItems = new LinkedList<>();
        Optional<Item> optionalItem = itemRepo.findById(id);
        if (optionalItem.isEmpty()) {
            return null;
        }
        Item ans = optionalItem.get();
        notProcessedItems.add(ans);
        while (!notProcessedItems.isEmpty()) {
            Item root = notProcessedItems.poll();
            List<Item> children = itemRepo.findAllByParentId(root.getId());
            if (!children.isEmpty()) {
                root.setChildren(new ArrayList<>());
                notProcessedItems.addAll(children);
                root.getChildren().addAll(children);
            }
        }
        return ans;
    }

    //sales
    public ResponseEntity sales(String date) {
        Timestamp start, finish;
        try {
            finish = new Timestamp(DatatypeConverter.parseDateTime(String.valueOf(date)).getTimeInMillis());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            start = new Timestamp(DatatypeConverter.parseDateTime(LocalDate.parse(finish.toString().substring(0, 19), formatter).minusDays(1).toString()).getTimeInMillis());
        } catch (Exception e) {
            return new ResponseEntity(new Answer(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        List<String> sales = itemRepo.findSales(start, finish);
        List<ItemReturn> updates = new ArrayList<>();
        sales.forEach(id -> {
            updates.add((ItemReturn) getItemByIdToReturn(id).getBody());
        });
        return ResponseEntity.ok(updates);
    }

    //Delete
    public ResponseEntity<Answer> delete(String id) {
        Item ans = findChildrenByRootId(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        Queue<Item> queueForDeletion = new LinkedList<>();
        Queue<Item> categoriesReadyToDelete = new LinkedList<>();
        queueForDeletion.add(ans);
        while (!queueForDeletion.isEmpty()) {
            Item item = queueForDeletion.poll();
            if (Objects.equals(item.getType(), "OFFER")) {
                subPrice(item, item.getPrice());
            }
            if (item.getChildren() != null)
                queueForDeletion.addAll(item.getChildren());
            if (!Objects.equals(item.getType(), "CATEGORY")) {
                itemRepo.deleteById(item.getId());
                if (statisticRepo.findByItemId(item.getId()) != null) {
                    List<Statistic> statistics = statisticRepo.findAllByItemId(item.getId());
                    statistics.forEach(value -> statisticRepo.deleteById(value.getId()));
                }
            } else
                categoriesReadyToDelete.add(item);
        }
        itemRepo.deleteAll(categoriesReadyToDelete);
        categoriesReadyToDelete.forEach(value -> {
            if (statisticRepo.findByItemId(value.getId()) != null) {
                List<Statistic> statistics = statisticRepo.findAllByItemId(value.getId());
                statistics.forEach(value1 -> statisticRepo.deleteById(value1.getId()));
            }
        });
        return ResponseEntity.ok(new Answer(200, "OK"));
    }

    public void updateRootTime(String childId, Timestamp updateTime) {
        Optional<Item> root = itemRepo.findById(childId);
        if (root.isPresent()) {
            root.get().setDate(updateTime);
            if (root.get().getParentId() != null) {
                updateRootTime(root.get().getParentId(), updateTime);
            }
        }
    }


    //Increase and decrease of the price with a rise up
    private void addOfferPrice(Item item, Double add) {
        List<Item> roots = itemRepo.findAllById(item.getParentId());
        for (Item root : roots) {
            if (root.getPrice() == null) {
                root.setPrice(0D);
            }
            if (categoryOfferCount.get(root.getId()) == null) {
                categoryOfferCount.put(root.getId(), 1L);
            } else {
                categoryOfferCount.put(root.getId(), categoryOfferCount.get(root.getId()) + 1);
            }
            Long count = categoryOfferCount.get(root.getId());
            root.setPrice(((itemRepo.getById(root.getId()).getPrice() * (count - 1)) + add) / count);
            statisticRepo.save(new Statistic(root.getId(), "ADD", root.getPrice().longValue(), "ACTIVE", item.getDate()));
            itemRepo.save(root);
            addOfferPrice(root, add);
        }
    }

    private void subPrice(Item item, Double sub) {
        List<Item> roots = itemRepo.findAllById(item.getParentId());
        for (Item root : roots) {
            if (categoryOfferCount.get(root.getId()) != null) {
                categoryOfferCount.put(root.getId(), categoryOfferCount.get(root.getId()) - 1);
            }
            Long count = categoryOfferCount.get(root.getId());
            root.setPrice(((itemRepo.getById(root.getId()).getPrice() * (count + 1)) - sub) / (count == 0 ? 1 : count));
            statisticRepo.save(new Statistic(root.getId(), "SUB", root.getPrice().longValue(), "ACTIVE", item.getDate()));
            itemRepo.save(root);
            subPrice(root, sub);
        }
    }


    //statistic
    @SneakyThrows
    public ResponseEntity statistic(String id, String dateStart, String dateFinish) {
        Timestamp start, finish;
        if (itemRepo.findById(id).isEmpty()) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        try {
            start = new Timestamp(DatatypeConverter.parseDateTime(dateStart).getTimeInMillis());
            finish = new Timestamp(DatatypeConverter.parseDateTime(dateFinish).getTimeInMillis());
        } catch (Exception e) {
            return new ResponseEntity<>(new Answer(400, "Validation Failed"), HttpStatus.BAD_REQUEST);
        }
        List<Statistic> statistics = statisticRepo.findAllStatisticByItemIdInBetween(id, start, finish);
        return ResponseEntity.ok(statistics.stream().map(value -> {
            Optional<Item> item = itemRepo.findById(value.getItemId());
            if (item.isEmpty()) {
                return null;
            }
            return new ItemStats(value.getItemId(), item.get().getName(), value.getDate().toString(), item.get().getParentId(), value.getValue(), item.get().getType());
        }).collect(Collectors.toList()));
    }

    //imports
    public ResponseEntity<Answer> parse(String stringJson) throws ParseException {
        if (stringJson.charAt(0) != '[') {
            stringJson = "[" + stringJson;
            stringJson = stringJson + "]";
        }
        stringJson = stringJson.replaceAll("None", "null");
        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(stringJson);

        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(o.toString());
            JSONArray items = (JSONArray) jsonObject.get("items");
            for (Object item : items) {
                JSONObject itemJson = (JSONObject) jsonParser.parse(String.valueOf(item));
                Object type = itemJson.get("type");
                Item itemClass;
                Optional<Item> optionalItem = itemRepo.findById(itemJson.get("id").toString());
                if (optionalItem.isPresent()) {
                    if (optionalItem.get().getParentId() != null)
                        updateRootTime(optionalItem.get().getParentId(),
                                new Timestamp(DatatypeConverter.parseDateTime(jsonObject.get("updateDate").toString()).getTimeInMillis()));
                    itemRepo.save(optionalItem.get());
                } else {
                    if ("CATEGORY".equals(type)) {
                        itemClass = new Item(itemJson.get("id"), type, itemJson.get("name"),
                                itemJson.get("parentId"), jsonObject.get("updateDate"), null);
                        if (itemClass.getParentId() != null)
                            updateRootTime(itemClass.getParentId(), itemClass.getDate());
                        itemRepo.save(itemClass);
                    } else if ("OFFER".equals(type)) {
                        itemClass = new Item(itemJson.get("id"), type, itemJson.get("name"),
                                itemJson.get("parentId") == null ? null : itemJson.get("parentId"), jsonObject.get("updateDate"),
                                Double.valueOf(String.valueOf(itemJson.get("price"))));
                        if (itemClass.getParentId() != null)
                            updateRootTime(itemClass.getParentId(), itemClass.getDate());
                        addOfferPrice(itemClass, itemClass.getPrice());
                        itemRepo.save(itemClass);
                    }
                }
            }
        }
        return ResponseEntity.ok(new Answer(200, "OK"));
    }
}
