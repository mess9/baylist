package org.baylist.tests.db;

import org.baylist.db.entity.Entity;
import org.baylist.db.repo.EntityRepository;
import org.baylist.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DbTest extends BaseTest {

    @Autowired
    EntityRepository entityRepository;

    @Test
    void test() {
        Entity entity = new Entity();
        entity.setName("test");
        entityRepository.save(entity);

    }

}
