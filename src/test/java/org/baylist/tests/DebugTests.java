package org.baylist.tests;

import org.baylist.db.repository.ItemRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest
public class DebugTests {

	@Autowired
	ItemRepository itemRepository;

	@Autowired
	MongoTemplate mt;


	@Test
	void debugTest() {

	}


}
