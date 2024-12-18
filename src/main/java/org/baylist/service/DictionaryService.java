package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.ChatState;
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
@AllArgsConstructor
public class DictionaryService {

    private final CategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final UserService userService;


    public Map<String, Set<String>> parseInputBuyList(String input) {
        Map<String, Set<String>> buyList = new HashMap<>();

        List<String> words = splitInput(input);
        words.forEach(word -> {
            String category = getDict().entrySet().stream()
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
        // мб позже добавить вариант разделения по запятым или пробелам, хз пока
    }

    public void addDictCategory(ChatState chatState) {
        String input = chatState.getUpdate().getMessage().getText().trim().toLowerCase();
        // мб позже добавить валидацию
        categoryRepository.save(new Category(null, input, null));
        chatState.setReplyText("категория - [ " + input + " ] - добавлена");
        //todo кнопки о добавлении ещё категорий или таск внутрь категорий
        userService.addCategoryOff(chatState);
    }

    public Map<String, Set<String>> getDict() {
        return categoryRepository.findAll().stream().collect(Collectors.toMap(
                Category::getName,
                c -> variantRepository.findByCategoryId(c.getId())
                        .stream()
                        .map(Variant::getName)
                        .collect(Collectors.toSet())
        ));
    }


}
