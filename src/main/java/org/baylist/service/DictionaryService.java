package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;

@Getter
@Component
public class DictionaryService {

    private final CategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final Map<String, Set<String>> dict = new HashMap<>();

    //todo добавить механизм пополнения словарика из телеги


	public DictionaryService(CategoryRepository categoryRepository,
                             VariantRepository variantRepository) {
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;

        categoryRepository.findAll().forEach(c -> dict.put(
                c.getName(),
                variantRepository.findByCategoryId(c.getId())
                        .stream()
                        .map(Variant::getName)
                        .collect(Collectors.toSet())));
    }


    public Map<String, Set<String>> parseInputBuyList(String input) {
        Map<String, Set<String>> buyList = new HashMap<>();

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
