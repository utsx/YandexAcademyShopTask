package ru.yndx.school.shop.components;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yndx.school.shop.entities.Answer;
import ru.yndx.school.shop.entities.Item;
import ru.yndx.school.shop.repositories.ItemRepo;

import java.util.*;


@Component
@AllArgsConstructor
@NoArgsConstructor
public class ItemComponent {

    @Autowired
    private ItemRepo itemRepo;

    private Map<String, Long> categoryOfferCount = new HashMap<>();

    public ResponseEntity getItemById(String id) {
        Item ans = findChildrenByRootId(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        ans = calculateOffers(ans);
        return ResponseEntity.ok(ans);
    }

    private Item calculateOffers(Item item) {
        Queue<Item> queue = new LinkedList<>();
        queue.add(item);
        while (!queue.isEmpty()) {
            Item root = queue.poll();
            if (Objects.equals(root.getType(), "CATEGORY")) {
                root.setPrice(root.getPrice() / (categoryOfferCount.get(root.getId()) != 0? categoryOfferCount.get(root.getId()) : 1));
                if (root.getChildren() != null)
                    queue.addAll(root.getChildren());
            }
        }
        return item;
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

    public ResponseEntity delete(String id) {
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
            if (!Objects.equals(item.getType(), "CATEGORY"))
                itemRepo.deleteById(item.getId());
            else
                categoriesReadyToDelete.add(item);
        }
        itemRepo.deleteAll(categoriesReadyToDelete);
        return ResponseEntity.ok(new Answer(200, "OK"));
    }

    public void updateRootTime(String childId, String updateTime) {
        Optional<Item> root = itemRepo.findById(childId);
        if (root.isPresent()) {
            root.get().setDate(updateTime);
            if (root.get().getParentId() != null) {
                updateRootTime(root.get().getParentId(), updateTime);
            }
        }
    }

    private void addOfferPrice(Item item, long add) {
        List<Item> roots = itemRepo.findAllById(item.getParentId());
        for (Item root : roots) {
            if (root.getPrice() == null) {
                root.setPrice(0L);
            }
            if (categoryOfferCount.get(root.getId()) == null) {
                categoryOfferCount.put(root.getId(), 1L);
            } else {
                categoryOfferCount.put(root.getId(), categoryOfferCount.get(root.getId()) + 1);
            }
            root.setPrice(itemRepo.getById(root.getId()).getPrice() + add);
            itemRepo.save(root);
            addOfferPrice(root, add);
        }
    }

    private void subPrice(Item item, long sub) {
        List<Item> roots = itemRepo.findAllById(item.getParentId());
        for (Item root : roots) {
            if (categoryOfferCount.get(root.getId()) != null) {
                categoryOfferCount.put(root.getId(), categoryOfferCount.get(root.getId()) - 1);
            }
            root.setPrice(itemRepo.getById(root.getId()).getPrice() - sub);
            itemRepo.save(root);
            subPrice(root, sub);
        }
    }

    public ResponseEntity parse(String stringJson) throws ParseException {
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
                if ("CATEGORY".equals(type)) {
                    itemClass = new Item(itemJson.get("id"), type, itemJson.get("name"),
                            itemJson.get("parentId"), jsonObject.get("updateDate"), null);
                    if (itemClass.getParentId() != null)
                        updateRootTime(itemClass.getParentId(), itemClass.getDate());
                    itemRepo.save(itemClass);
                } else if ("OFFER".equals(type)) {
                    itemClass = new Item(itemJson.get("id"), type, itemJson.get("name"),
                            itemJson.get("parentId") == null ? null : itemJson.get("parentId"), jsonObject.get("updateDate"),
                            Long.valueOf(String.valueOf(itemJson.get("price"))));
                    if (itemClass.getParentId() != null)
                        updateRootTime(itemClass.getParentId(), itemClass.getDate());
                    addOfferPrice(itemClass, itemClass.getPrice());
                    itemRepo.save(itemClass);
                }
            }
        }
        return ResponseEntity.ok(new Answer(200, "OK"));
    }
}
