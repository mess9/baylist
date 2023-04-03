package org.baylist.controller.rest.item;

import lombok.AllArgsConstructor;
import org.baylist.db.entity.Item;
import org.baylist.db.repository.ItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemsController {

    ItemRepository itemRepository;

    @PostMapping()
    public ResponseEntity<String> createItem(@RequestBody Item item) {
        item.setDateTime(LocalDateTime.now());
        itemRepository.save(item);
        return ResponseEntity.ok("Item successfully saved");
    }

    @PostMapping("/search")
    public Item searchItem(@RequestBody Item item) {
        return itemRepository.findDistinctByNameContains(item.getName());
    }

}
