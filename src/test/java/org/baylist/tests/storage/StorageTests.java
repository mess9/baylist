package org.baylist.tests.storage;

import org.baylist.db.Storage;
import org.baylist.todoist.dto.Task;
import org.baylist.todoist.service.TodoistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.baylist.util.convert.ToJson.toJson;

@SpringBootTest
public class StorageTests {

    @Autowired
    TodoistService todoistService;

    @Test
    void test() {
        Storage storage = todoistService.syncDate();

        System.out.println("1------------------");
        System.out.println(storage);

        System.out.println("2------------------");
        List<Task> list = storage.getProjects().stream().flatMap(p -> p.getTasks().stream()).filter(t -> t.getCreatedAt() != null).toList();
        list.forEach(System.out::println);
        System.out.println("3------------------");

        System.out.println("4------------------");
        System.out.println(toJson(storage));

    }

}
