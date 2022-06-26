package ru.yndx.school.shop.controllers;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yndx.school.shop.components.ItemComponent;

import java.util.Objects;

@RestController
@Tag(name = "Главный контейнер", description = "В данном контейнере представлены все методы доступные в сервисе")
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
    public ResponseEntity statistic(@PathVariable String id, @RequestParam(value = "dateStart") @Parameter(description = "Дата начала проверки") String dateStart,
                                    @RequestParam(value = "dateEnd") @Parameter(description = "Дата окончания проверки") String dateFinish){
        return itemComponent.statistic(id, dateStart, dateFinish);
    }

}
