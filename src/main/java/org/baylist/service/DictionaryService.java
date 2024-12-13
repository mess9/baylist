package org.baylist.service;

import lombok.Getter;
import org.baylist.dto.dict.BuyCategoryDict;
import org.baylist.exception.DictionaryException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;
import static org.baylist.util.convert.ToJson.fromYaml;

@Getter
@Component
public class DictionaryService {

    private static final String dictFilePath = "dict/dictionary.yml";
    //    private static final String dictFilePath = "src/main/resources/dict/dictionary.yml";
    private BuyCategoryDict buyCategoryDict;

    //todo добавить механизм пополнения словарика из телеги

    public DictionaryService() {
        try {
            ClassPathResource resource = new ClassPathResource(dictFilePath);
            Path path = Paths.get(resource.getURI());
            buyCategoryDict = fromYaml(Files.readString(path), BuyCategoryDict.class);
        } catch (Exception e) {
            throw new DictionaryException("invalid yaml\n" + e.getMessage());
        }
    }


    public Map<String, Set<String>> parseInputBuyList(String input) {
        Map<String, Set<String>> buyList = new HashMap<>();
        Map<String, Set<String>> dict = buyCategoryDict.getDict();

        List<String> words = splitInput(input);
        words.forEach(word -> {
            String category = dict.entrySet().stream()
                    .filter(entry -> entry.getValue().contains(word))
                    .map(Map.Entry::getKey)
                    .findAny()
                    .orElse(UNKNOWN_CATEGORY);
            buyList.computeIfAbsent(category, v -> new HashSet<>()).add(word);
        });

        return buyList;
    }

    private List<String> splitInput(String input) {
        return Arrays.stream(input.split("\n")).toList();
        //todo добавить вариант разделения по запятым или пробелам, хз пока
    }


}
