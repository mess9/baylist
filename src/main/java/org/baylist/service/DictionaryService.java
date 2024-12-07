package org.baylist.service;

import org.baylist.dto.dict.BuyCategoryDict;
import org.baylist.exception.DictionaryException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.baylist.util.convert.ToJson.fromJson;

@Component
public class DictionaryService {

    private static final String dictFileName = "/dict/dictionary.yml";
    private BuyCategoryDict buyCategoryDict;

    public DictionaryService() {
        File file = new File(dictFileName);
        if (file.exists()) {
            try {
                buyCategoryDict = fromJson(file.toString(), BuyCategoryDict.class);
            } catch (Exception e) {
                throw new DictionaryException(e.getMessage());
            }
        }
        throw new DictionaryException("dictionary not found");
    }

    public Map<String, List<String>> parseInputBuyList(String input) {
        Map<String, List<String>> buyList = new HashMap<>();

        List<String> words = splitInput(input);
        words.forEach(word -> {

        });

        return buyList;
    }

    private List<String> splitInput(String input) {
        return Arrays.stream(input.split("\n")).toList();
    }


}
