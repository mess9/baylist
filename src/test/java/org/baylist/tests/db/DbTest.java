package org.baylist.tests.db;

import org.baylist.db.repo.CategoryRepository;
import org.baylist.tests.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DbTest extends BaseTest {


    @Autowired
    CategoryRepository categoryRepository;


//    @Test
//    void test2() {
//        Category category = categoryRepository.findCategoryByName("продукты");
//        System.out.println(category);
//
//        Category category2 = categoryRepository.findCategoryByName("продукты");
//        System.out.println(category);
//    }

}
