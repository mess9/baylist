package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

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

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("добавить ещё категорию")
                        .callbackData(Callbacks.ADD_CATEGORY.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("добавить варианты задач в категорию")
                        .callbackData(Callbacks.ADD_TASK_TO_CATEGORY.getCallbackData())
                        .build()), //todo в разработке. пока - не работает
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("пока всё, вернись в дефолтный режим")
                        .callbackData(Callbacks.CANCEL.getCallbackData())
                        .build())));
        chatState.setReplyKeyboard(markup);
//        userService.addCategoryOff(chatState);
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
