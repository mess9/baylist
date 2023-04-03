package org.baylist.tests.items;

import org.baylist.db.entity.Item;
import org.baylist.db.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ItemsTests {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    MongoTemplate mt;

    @BeforeEach
    void cleanDb() {
        for (String collectionName : mt.getCollectionNames()) {
            mt.dropCollection(collectionName);
        }
        mt.createCollection(Item.class);
    }

    @Test
    void checkCreateItem() {
        Item item1 = Item.builder().body("body 1").name("name 1").build();

        itemRepository.save(item1);
        List<Item> all = mt.findAll(Item.class);
        Assertions.assertEquals(all.get(0).getName(), "name 1");
        Assertions.assertEquals(all.get(0).getBody(), "body 1");
        System.out.println(all);
    }

    @Test
    void fillDbTasks() {
        List<Item> lItem = new ArrayList<>();
        lItem.add(Item.builder().name("Brieana").body("Windows lunch council symptoms combine hepatitis techno. ").build());
        lItem.add(Item.builder().name("specialist").body("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.").build());
        lItem.add(Item.builder().name("Tiron").body("Egypt designed threats fatal encryption. ").build());
        lItem.add(Item.builder().name("Loreen Stack").body("1998-07-22").build());
        lItem.add(Item.builder().name("Chamroeun Turcotte").body("Implemented donate quantities optimization acceptance payroll allen, rewards importance invite prophet classroom holds incident, oz biodiversity header carlo bomb moderators during, naval reverse retrieval recent antarctica completion profits. ").build());
        lItem.add(Item.builder().name("Mirissa Wiggins").body("jamikaunlr").build());
        lItem.add(Item.builder().name("Ayasha Flores").body("ddf6d07f-4625-4b68-99af-bb5cf073a7b9").build());
        lItem.add(Item.builder().name("Aeriel Card").body("kierstin_trivette0kd@possess.val").build());
        lItem.add(Item.builder().name("Tiffani").body("https://speakerkzhti5deak46.ji").build());
        lItem.add(Item.builder().name("Albania").body("57.151.123.212").build());
        lItem.add(Item.builder().name("Missouri City").body("01%").build());
        lItem.add(Item.builder().name("Inappropriate Road 2954, Umhlanga, Vietnam, 319655").body("20 Sep").build());
        lItem.add(Item.builder().name("Ottawa St 140, Tohatchi, El Salvador, 984384").body("City biodiversity demo flights homes silver nutrition, bread. ").build());
        lItem.add(Item.builder().name("Blow Road 9222, Saint George, Netherlands Antilles, 773488").body("Reasonably liberia bangladesh federal showing sales spies, interests staff sociology containing lookup. ").build());
        itemRepository.saveAll(lItem);

        Assertions.assertEquals(itemRepository.findByBodyContains("4b68"), lItem.get(6));
        Assertions.assertEquals(itemRepository.findDistinctByNameContains("Bri"), lItem.get(0));
        System.out.println(itemRepository.findAll());

        long count = itemRepository.count();
        Assertions.assertEquals(14, count);
        System.out.println(count);
    }

}
