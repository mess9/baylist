package org.baylist.tests.dict;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.baylist.service.DictionaryService;
import org.baylist.tests.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

public class DictionaryTest extends BaseTest {

    @Autowired
    DictionaryService dictionaryService;

    @InjectSoftAssertions
    private SoftAssertions s;

    @Test
    void getDictionary() {
        var buyCategoryDict = dictionaryService.getDict();

        s.assertThat(buyCategoryDict.keySet()).hasSizeGreaterThan(3);
        s.assertThat(buyCategoryDict.values()).hasSizeGreaterThan(3);
        s.assertThat(buyCategoryDict.values().stream().flatMap(Set::stream).toList()).hasSizeGreaterThan(40);
    }

    @Test
    void parse() {
        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
                картошка
                морковка
                фанера
                вб
                """);

        s.assertThat(stringSetMap.get("овощи")).hasSize(2);
        s.assertThat(stringSetMap.get("other")).hasSize(1);
        s.assertThat(stringSetMap.get("пункты выдачи")).hasSize(1);
    }

    @Test
    void parse2() {
        Map<String, Set<String>> stringSetMap = dictionaryService.parseInputBuyList("""
                вб
                морковка
                фанера
                вб
                """);

        s.assertThat(stringSetMap.get("овощи")).hasSize(1);
        s.assertThat(stringSetMap.get("other")).hasSize(1);
        s.assertThat(stringSetMap.get("пункты выдачи")).hasSize(1);
    }


}
