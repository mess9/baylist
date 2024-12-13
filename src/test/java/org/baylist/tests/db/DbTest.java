package org.baylist.tests.db;

import org.baylist.db.YourEntityRepository;
import org.baylist.dto.db.YourEntity;
import org.baylist.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DbTest extends BaseTest {

    @Autowired
    YourEntityRepository yourEntityRepository;

    @Test
    void test() {
        YourEntity yourEntity = new YourEntity();
        yourEntity.setName("test");
        yourEntityRepository.save(yourEntity);

    }

}
