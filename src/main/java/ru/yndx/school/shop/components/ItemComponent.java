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

    public ResponseEntity getItemById(String id) {
        Item ans = findChildrenByRootId(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(ans);
    }

    private Item findChildrenByRootId(String id){
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
                long count = children.stream().map(Item::getPrice).mapToLong(value -> value).sum();
                root.setChildren(new ArrayList<>());
                notProcessedItems.addAll(children);
                root.getChildren().addAll(children);
                root.setPrice((root.getPrice() + count) / children.size());
            }
            else{
                root.setChildren(null);
            }
        }
        if(ans.getChildren() != null) {
            long count = ans.getChildren().stream().map(Item::getPrice).mapToLong(value -> value).sum();
            ans.setPrice((ans.getPrice() + count) / ans.getChildren().size());
        }
        return ans;
    }

    public ResponseEntity delete(String id) {
        Item ans = findChildrenByRootId(id);
        if (ans == null) {
            return new ResponseEntity<>(new Answer(404, "Item not found"), HttpStatus.NOT_FOUND);
        }
        Queue<Item> queueForDeletion = new LinkedList<>();
        queueForDeletion.add(ans);
        while (!queueForDeletion.isEmpty()){
            Item item = queueForDeletion.poll();
            if(item.getChildren() != null)
                queueForDeletion.addAll(item.getChildren());
            itemRepo.deleteById(item.getId());
        }
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


    private boolean checkValidItem(Item item){
        Optional<Item> optionalItem = itemRepo.findById(item.getParentId());
        if(optionalItem.isPresent()){
            if(optionalItem.get().getType().equals("OFFER") && item.getType().equals("OFFER"))
            {
                return false;
            }
        }
        return true;
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
                            itemJson.get("parentId"), jsonObject.get("updateDate"), 0L);
                    if(itemClass.getParentId() != null)
                        updateRootTime(itemClass.getParentId(), itemClass.getDate());
                    itemRepo.save(itemClass);
                } else if ("OFFER".equals(type)) {
                    itemClass = new Item(itemJson.get("id"), type, itemJson.get("name"),
                            itemJson.get("parentId") == null ? null : itemJson.get("parentId"), jsonObject.get("updateDate"),
                            Long.valueOf(String.valueOf(itemJson.get("price"))));
                    if(itemClass.getParentId() != null)
                        updateRootTime(itemClass.getParentId(), itemClass.getDate());
                    itemRepo.save(itemClass);
                }
            }
        }
        return ResponseEntity.ok(new Answer(200, "OK"));
    }


}
