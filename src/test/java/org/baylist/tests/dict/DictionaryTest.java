package org.baylist.tests.dict;

import org.baylist.dto.dict.BuyCategoryDict;
import org.baylist.service.DictionaryService;
import org.baylist.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class DictionaryTest extends BaseTest {

    @Autowired
    DictionaryService dictionaryService;

    @Test
    void getDictionary() {
        BuyCategoryDict buyCategoryDict = dictionaryService.getBuyCategoryDict();
        System.out.println(buyCategoryDict);
    }

    @Test
    void parse() {
        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
                картошка
                морковка
                фанера
                вб
                """);
        System.out.println("---------");
        System.out.println(stringSetMap);
    }

    @Test
    void parse2() {
        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
                вб
                морковка
                фанера
                вб
                """);
        System.out.println("---------");
        System.out.println(stringSetMap);
    }

    @Test
    void fillDb() {
        dictionaryService.fillCategoriesFromDictFile();
    }

}
