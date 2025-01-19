package org.baylist.tests;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.baylist.util.extension.ClearTestData;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith({
        ClearTestData.class,
        SoftAssertionsExtension.class
})
@SpringBootTest
public class BaseTest {

	@InjectSoftAssertions
	public SoftAssertions s;


}
