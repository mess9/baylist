package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
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

	private final ApplicationContext context;
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

	public boolean addDictCategory(String category) {
        // todo позже добавить валидацию
		DictionaryService self = context.getBean(DictionaryService.class);
		Category categoryByName = self.getCategoryByName(category);
		if (categoryByName == null) {
			categoryRepository.save(new Category(null, category, null));
			return true;
		} else {
			return false;
		}
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

	@Cacheable(value = "category", key = "#result.id", unless = "#result == null")
	public Category getCategoryByName(String name) {
		return categoryRepository.findCategoryByName(name);
	}

    public void addVariantToCategory(ChatValue chatValue, String category) {
	    DictionaryService self = context.getBean(DictionaryService.class);
        String input = chatValue.getInputText();
        String[] split = input.split("\n");
        List<String> variants = Arrays.stream(split).map(String::trim).distinct().toList();
	    Category categoryDb = self.getCategoryByName(category);
	    if (categoryDb == null) {
		    settingsMainMenu(chatValue, false);
            chatValue.setReplyText("категория не найдена");
        } else {
		    addVariantsToCategory(variants, categoryDb);
		    settingsMainMenu(chatValue, false);
            chatValue.setReplyText(variants.size() + ": вариантов добавлено в категорию - " + category);
        }
        chatValue.setState(State.DICT_SETTING);
    }

	public void addVariantsToCategory(List<String> variants, Category categoryDb) {
		variantRepository.saveAll(variants.stream().map(v -> new Variant(null, v, categoryDb)).toList());
	}

	public void settingsMainMenu(ChatValue chatValue, boolean isEdit) {
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
				        .text("переименовать категорию")
				        .callbackData(Callbacks.DICT_RENAME_CATEGORY.getCallbackData())
				        .build()),
		        new InlineKeyboardRow(InlineKeyboardButton.builder()
				        .text("удалить категории")
				        .callbackData(Callbacks.DICT_REMOVE_CATEGORY.getCallbackData())
                        .build()),
		        new InlineKeyboardRow(InlineKeyboardButton.builder()
				        .text("удалить варианты задач")
				        .callbackData(Callbacks.DICT_REMOVE_VARIANT.getCallbackData())
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
		if (isEdit) {
			chatValue.setEditText("настройки словарика");
			chatValue.setEditReplyKeyboard(markup);
		} else {
			chatValue.setReplyText("настройки словарика");
			chatValue.setReplyKeyboard(markup);
		}

		chatValue.setState(State.DICT_SETTING);
    }

	public List<String> getVariants(String category) {
		return variantRepository.findAllByCategoryName(category).stream().map(Variant::getName).toList();
	}

	public void settingsShortMenu(ChatValue chatValue, String message, boolean isEdit) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("настраивать словарик")
                        .callbackData(Callbacks.DICT_SETTINGS.getCallbackData())
                        .build()),
                new InlineKeyboardRow(InlineKeyboardButton.builder()
                        .text("хватит пока")
                        .callbackData(Callbacks.CANCEL.getCallbackData())
                        .build())));
		if (isEdit) {
			chatValue.setEditText(message);
			chatValue.setEditReplyKeyboard(markup);
			chatValue.setEditReplyParseModeHtml();
		} else {
			chatValue.setReplyText(message);
			chatValue.setReplyKeyboard(markup);
			chatValue.setReplyParseModeHtml();
		}
        chatValue.setState(State.DICT_SETTING);
    }

	@Transactional
	public void removeCategory(String category) {
		DictionaryService self = context.getBean(DictionaryService.class);
		Category categoryDb = self.getCategoryByName(category);
		if (categoryDb != null) {
			removeCategoryById(categoryDb);
			categoryRepository.delete(categoryDb);
		}
	}

	@CacheEvict(value = "category", key = "#categoryDb.id")
	public void removeCategoryById(Category categoryDb) {
		variantRepository.deleteCategoryById(categoryDb.getId());
	}

	@CacheEvict(value = "category", key = "#category.id")
	public void renameCategory(Category category, String newCategoryName) {
		category.setName(newCategoryName);
		categoryRepository.save(category);
	}

	@Transactional
	public void removeVariants(String variants) {
		for (String s : variants.split("\n")) {
			variantRepository.deleteByName(s.trim());
		}
	}
}
