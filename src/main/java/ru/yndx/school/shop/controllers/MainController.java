package ru.yndx.school.shop.controllers;


import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yndx.school.shop.components.ItemComponent;

import java.util.Objects;

@RestController
public class MainController {

    private final ItemComponent itemComponent;

    public MainController(final ItemComponent itemComponent) {
        this.itemComponent = itemComponent;
    }

    @SneakyThrows
    @PostMapping(value = "/imports", consumes = "application/json", produces = "application/json")
    public ResponseEntity imports(HttpEntity<String> httpEntity){
        return itemComponent.parse(Objects.requireNonNull(httpEntity.getBody()));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        return itemComponent.delete(id);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity nodes(@PathVariable String id){
        return itemComponent.getItemByIdToReturn(id);
    }

    @GetMapping(value = "/sales")
    public ResponseEntity sales(@RequestParam(value = "date") String date){
        return itemComponent.sales(date);
    }

    @GetMapping(value = "/node/{id}/statistic")
    public ResponseEntity statistic(@PathVariable String id, @RequestParam(value = "dateStart") String dateStart,
                                    @RequestParam(value = "dateEnd") String dateFinish){
        return itemComponent.statistic(id, dateStart, dateFinish);
    }

}
