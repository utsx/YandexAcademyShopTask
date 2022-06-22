package ru.yndx.school.shop.controllers;


import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yndx.school.shop.components.ItemComponent;

@RestController
public class MainController {

    private final ItemComponent itemComponent;

    public MainController(final ItemComponent itemComponent) {
        this.itemComponent = itemComponent;
    }

    @SneakyThrows
    @PostMapping(value = "/imports", consumes = "application/json", produces = "application/json")
    public ResponseEntity imports(HttpEntity<String> httpEntity){
        return itemComponent.parse(httpEntity.getBody());
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable String id){
        return itemComponent.delete(id);
    }

    @GetMapping("/nodes/{id}")
    public ResponseEntity nodes(@PathVariable String id){
        return itemComponent.getItemById(id);
    }

}
