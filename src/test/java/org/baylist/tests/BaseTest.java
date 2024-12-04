package org.baylist.tests;

import org.baylist.util.extension.ClearTestData;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith({
        ClearTestData.class
})
@SpringBootTest
public class BaseTest {
}
