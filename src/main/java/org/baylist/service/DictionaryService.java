package org.baylist.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.baylist.db.entity.Category;
import org.baylist.db.entity.Variant;
import org.baylist.db.repo.CategoryRepository;
import org.baylist.db.repo.VariantRepository;
import org.baylist.dto.telegram.Action;
import org.baylist.dto.telegram.Callbacks;
import org.baylist.dto.telegram.ChatValue;
import org.baylist.dto.telegram.State;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.CATEGORIES;
import static org.baylist.dto.Constants.DICT;
import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictionaryService {

	ApplicationContext context;
	CategoryRepository categoryRepository;
	VariantRepository variantRepository;
	MenuService menuService;
	HistoryService historyService;
	CacheManager cacheManager;


	public Map<String, Set<String>> parseInputWithDict(String input, Long userId) {
		DictionaryService self = context.getBean(DictionaryService.class);
		Map<String, Set<String>> dict = self.getDict(userId);
		Map<String, Set<String>> buyList = new HashMap<>();
		List<String> words = splitInputTasks(input);
		if (dict != null) {
			words.forEach(word -> {
				String category = findCategoryInDictionary(word, dict);
				buyList.computeIfAbsent(category, v -> new HashSet<>()).add(word);
			});
		} else {
			buyList.put(UNKNOWN_CATEGORY, new HashSet<>(words));
		}

        return buyList;
    }

	@Transactional
	@Cacheable(value = DICT, key = "#userId")
	public Map<String, Set<String>> getDict(Long userId) {
		List<Category> categories = categoryRepository.findAllByUserId(userId);
		if (categories.isEmpty()) {
			return null;
		} else {
			return categories.stream().collect(Collectors.toMap(
					Category::getName,
					c -> variantRepository.findAllByCategoryId(c.getId())
							.stream()
							.map(Variant::getName)
							.collect(Collectors.toSet())));
		}
	}

	private List<String> splitInputTasks(String input) {
		if (input != null) {
			return Arrays.stream(input.split("\n")).toList();
		}
		return new ArrayList<>();
		// мб позже добавить вариант разделения по запятым или пробелам, хз пока
	}

	@Transactional
	public void removeCategory(List<Category> categories) {
		if (categories != null) {
			categoryRepository.deleteAll(categories);
			cacheEvict(categories);
		}
	}

	private void cacheEvict(List<Category> categories) {
		Cache cacheCategories = cacheManager.getCache(CATEGORIES);
		if (cacheCategories != null) {
			for (Category category : categories) {
				cacheCategories.evict(category.getUserId());
			}
		}
		//мб стоит вообще не кешировать категории, а только словарик целиком
		// и варианты в категориях подгружать как eager
		Cache cacheDict = cacheManager.getCache(DICT);
		if (cacheDict != null) {
			for (Category category : categories) {
				cacheDict.evict(category.getUserId());
			}
		}
	}

	public void renameCategory(Category category, String newCategoryName) {
		category.setName(newCategoryName);
		categoryRepository.save(category);
		cacheEvict(List.of(category));
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

	public boolean addDictCategory(String categoryName, Long userId) {
		DictionaryService self = context.getBean(DictionaryService.class);
		if (validateCategory(categoryName)) {
			List<Category> categoriesByUserId = self.getCategoriesByUserId(userId);
			if (categoriesByUserId.stream().noneMatch(c -> c.getName().equals(categoryName))) {
				categoryRepository.save(new Category(categoryName, userId));
				historyService.changeDict(userId, Action.ADD_CATEGORY, categoryName);
				cacheEvict(categoriesByUserId);
				return true;
			}
		}
		return false;
	}

	private boolean validateCategory(String category) {
		return category != null && !category.isBlank() && category.length() >= 2;
	}

	@Transactional
	@Cacheable(value = CATEGORIES, unless = "#result == null")
	public List<Category> getCategoriesByUserId(Long userId) {
		return categoryRepository.findAllByUserId(userId);
	}

	public Category getCategoryByCategoryIdAndUserId(Long categoryId, Long userId) {
		DictionaryService self = context.getBean(DictionaryService.class);
		return self.getCategoriesByUserId(userId).stream()
				.filter(c -> c.getId().equals(categoryId))
				.findAny()
				.orElseThrow();
	}

	public void addVariantToCategory(ChatValue chatValue, Category category) {
		String input = chatValue.getInputText();
		if (validateVariants(input)) {
			String[] split = input.split("\n");
			List<String> variants = Arrays.stream(split).map(String::trim).distinct().toList();
			int addedVariantsCount = addVariantsToCategory(variants, category);
			historyService.changeDict(chatValue.getUserId(), Action.ADD_VARIANT, variants.toString());
			menuService.dictionaryMainMenu(chatValue, false);
			postAddedMessage(chatValue, category, addedVariantsCount, variants);
			chatValue.setState(State.DICT_SETTING);
			cacheEvict(List.of(category));
		} else {
			menuService.dictionaryMainMenu(chatValue, false);
			chatValue.setReplyText("варианты не были добавлены. т.к. я не разобрал что добавлять\n" +
					"рекомендуется следовать рекомендациям по вводу вариантов");
		}

	}

	public boolean validateVariants(String variants) {
		return variants != null && !variants.isBlank() && variants.length() >= 2;
		//валидация
		// 1. на принадлежность вариантов категории
		// 2. на то что варианты разделены \n
	}

	public int addVariantsToCategory(List<String> variants, Category categoryDb) {
		categoryDb = getCategoryWithVariants(categoryDb.getId());
		Category finalCategoryDb = categoryDb;
		variants = variants.stream()
				.filter(v -> !finalCategoryDb.getVariants().stream()
						.map(Variant::getName)
						.toList()
						.contains(v))
				.toList();
		variantRepository.saveAll(variants.stream().map(v -> new Variant(null, v, finalCategoryDb)).toList());
		return variants.size();
	}

	@NotNull
	private static String findCategoryInDictionary(String word, Map<String, Set<String>> dict) {
		return dict.entrySet().stream()
				.filter(entry -> entry.getValue().contains(word))
				.map(Map.Entry::getKey)
				.findAny()
				.orElse(UNKNOWN_CATEGORY);
	}

	private void postAddedMessage(ChatValue chatValue, Category category, int addedVariantsCount, List<String> variants) {
		if (addedVariantsCount == variants.size()) {
			chatValue.setReplyText("все " + addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.getName() + "</b>");
		} else if (addedVariantsCount > 0) {
			chatValue.setReplyText(addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.getName() + "</b>");
		} else if (addedVariantsCount == 0) {
			chatValue.setReplyText("уже существующие варианты нельзя передобавить заново");
		}
	}

	public Category getCategoryWithVariants(Long categoryId) {
		return categoryRepository.findCategoryWithVariants(categoryId);
	}

	@Transactional
	public boolean removeVariants(List<String> variants, Category category) {
		category = getCategoryWithVariants(category.getId());
		List<Variant> existVariants = category.getVariants();
		List<Variant> variantsForDelete = new ArrayList<>();
		existVariants.stream().filter(v -> variants.contains(v.getName())).forEach(variantsForDelete::add);
		if (!variantsForDelete.isEmpty()) {
			category.getVariants().removeAll(variantsForDelete);
			categoryRepository.save(category);
			cacheEvict(List.of(category));
			return true;
		} else {
			return false;
		}
	}


}
