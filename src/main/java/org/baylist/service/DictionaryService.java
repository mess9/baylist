package org.baylist.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
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

    public void addDictCategory(String category) {
        // todo позже добавить валидацию
        categoryRepository.save(new Category(null, category, null));
    }

    public Map<String, Set<String>> getDict() {
        return categoryRepository.findAll().stream().collect(Collectors.toMap(
                Category::getName,
		        c -> variantRepository.findAllByCategoryId(c.getId())
                        .stream()
                        .map(Variant::getName)
                        .collect(Collectors.toSet())
        ));
    }

    public List<String> getCategories() {
        return categoryRepository.findAll().stream().map(Category::getName).toList();
    }

	public Category getCategory(String category) {
		return categoryRepository.findCategoryByName(category);
	}

	public List<String> getVariants(String category) {
		return variantRepository.findAllByCategoryName(category).stream().map(Variant::getName).toList();
	}


    public void addVariantToCategory(ChatValue chatValue, String category) {
        String input = chatValue.getInputText();
        String[] split = input.split("\n");
        List<String> variants = Arrays.stream(split).map(String::trim).distinct().toList();
        Category categoryDb = categoryRepository.findByName(category);
        if (categoryDb == null) {
            settingsMainMenu(chatValue);
            chatValue.setReplyText("категория не найдена");
        } else {
            variantRepository.saveAll(variants.stream().map(v -> new Variant(null, v, categoryDb)).toList());
            settingsMainMenu(chatValue);
            chatValue.setReplyText(variants.size() + ": вариантов добавлено в категорию - " + category);
        }
        chatValue.setState(State.DICT_SETTING);
    }

    public void settingsMainMenu(ChatValue chatValue) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("показать словарик")
                        .callbackData(Callbacks.DICT_VIEW.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("новая категория")
                        .callbackData(Callbacks.DICT_ADD_CATEGORY.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("добавить варианты в категорию")
                        .callbackData(Callbacks.DICT_ADD_TASKS_TO_CATEGORY.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("справка по словарику")
                        .callbackData(Callbacks.DICT_HELP.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("фсё, пока хватит")
                        .callbackData(Callbacks.CANCEL.getCallbackData())
                        .build())
        ));
        chatValue.setReplyKeyboard(markup);
        chatValue.setReplyText("настройки словарика");
        chatValue.setState(State.DICT_SETTING);
    }

    public void settingsShortMenu(ChatValue chatValue, String message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("настраивать словарик")
                        .callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("хватит пока")
                        .callbackData(Callbacks.CANCEL.getCallbackData())
                        .build())));
        chatValue.setReplyText(message);
        chatValue.setReplyKeyboard(markup);
        chatValue.setState(State.DICT_SETTING);
    }
}
