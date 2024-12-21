package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
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

    public void addDictCategory(ChatValue chatValue) {
        String input = chatValue.getUpdate().getMessage().getText().trim().toLowerCase();
        // мб позже добавить валидацию
        categoryRepository.save(new Category(null, input, null));
        chatValue.setReplyText("категория - [ " + input + " ] - добавлена");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("добавить ещё категорию")
                        .callbackData(Callbacks.ADD_CATEGORY.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("добавить варианты задач в категорию")
                        .callbackData(Callbacks.CATEGORY_CHOICE.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("пока всё, вернись в дефолтный режим")
                        .callbackData(Callbacks.CANCEL.getCallbackData())
                        .build())));
        chatValue.setReplyKeyboard(markup);
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

    public List<String> getCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).toList();
    }
}

//    public void addDictVariant(ChatState chatState) {
//        String input = chatState.getInputText();
//        String[] split = input.split("\n");
//        List<String> variants = Arrays.stream(split).map(String::trim).toList();
//        Category category = categoryRepository.findByName(chatState.getCategory()).orElse(null);
//        if (category == null) {
//            chatState.setReplyText("категория не найдена");
//            return;
//        }
//        variantRepository.saveAll(variants.stream().map(v -> new Variant(null, v, category.getId())).toList());
//        chatState.setReplyText("варианты добавлены");
//
//        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
//                new InlineKeyboardRow(InlineKeyboardButton.builder()
//                        .text("добавить ещё варианты")
//                        .callbackData(Callbacks.ADD_VARIANT.getCallbackData())
//                        .build())));
//        chatState.setReplyKeyboard(markup);
//    }
