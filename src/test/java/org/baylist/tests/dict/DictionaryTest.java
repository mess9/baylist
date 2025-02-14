package org.baylist.tests.dict;

import org.baylist.service.DictionaryService;
import org.baylist.tests.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;

public class DictionaryTest extends BaseTest {

    @Autowired
    DictionaryService dictionaryService;

    //нужно переписать все тесты

//    @Test
//    void getDictionary() {
//	    var buyCategoryDict = dictionaryService.getDict(123L);
//
//        s.assertThat(buyCategoryDict.keySet()).hasSizeGreaterThan(3);
//        s.assertThat(buyCategoryDict.values()).hasSizeGreaterThan(3);
//        s.assertThat(buyCategoryDict.values().stream().flatMap(Set::stream).toList()).hasSizeGreaterThan(40);
//    }
//
//    @Test
//    void parse() {
//        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
//                картошка
//                морковка
//                фанера
//                вб
//                """);
//
//        s.assertThat(stringSetMap.get("овощи")).hasSize(2);
//        s.assertThat(stringSetMap.get("other")).hasSize(1);
//        s.assertThat(stringSetMap.get("пункты выдачи")).hasSize(1);
//    }
//
//    @Test
//    void parse2() {
//        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
//                вб
//                морковка
//                фанера
//                вб
//                """);
//
//        s.assertThat(stringSetMap.get("овощи")).hasSize(1);
//        s.assertThat(stringSetMap.get("other")).hasSize(1);
//        s.assertThat(stringSetMap.get("пункты выдачи")).hasSize(1);
//    }


}
