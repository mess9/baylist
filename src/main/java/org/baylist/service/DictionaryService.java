package org.baylist.service;

import jakarta.transaction.Transactional;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.CATEGORIES;
import static org.baylist.dto.Constants.DICT;
import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;

@Component
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class DictionaryService {

	ApplicationContext context;
	CategoryRepository categoryRepository;
	VariantRepository variantRepository;
	MenuService menuService;
	HistoryService historyService;
	CacheManager cacheManager;
	TodoistService todoistService;

	@Autowired
	public DictionaryService(ApplicationContext context,
	                         CategoryRepository categoryRepository,
	                         VariantRepository variantRepository,
	                         MenuService menuService,
	                         HistoryService historyService,
	                         CacheManager cacheManager,
	                         @Lazy TodoistService todoistService) {
		this.context = context;
		this.categoryRepository = categoryRepository;
		this.variantRepository = variantRepository;
		this.menuService = menuService;
		this.historyService = historyService;
		this.cacheManager = cacheManager;
		this.todoistService = todoistService;
	}


	public Map<String, Set<String>> parseInputWithDict(List<String> input, Long userId) {
		DictionaryService self = context.getBean(DictionaryService.class);
		Map<String, Set<String>> dict = self.getDict(userId);
		Map<String, Set<String>> buyList = new HashMap<>();
		if (dict != null) {
			input.forEach(word -> {
				String category = findCategoryInDictionary(word, dict);
				buyList.computeIfAbsent(category, v -> new HashSet<>()).add(word);
			});
		} else {
			buyList.put(UNKNOWN_CATEGORY, new HashSet<>(input));
		}

        return buyList;
    }

	@Transactional
	public Map<String, Set<String>> changeAllDict(Long userId, Map<String, Set<String>> changes) {
		DictionaryService self = context.getBean(DictionaryService.class);
		Map<String, Set<String>> dict = self.getDict(userId);
		List<Category> categoriesBeforeChanges = self.getCategoriesByUserId(userId);


		// Шаг 1: Найти различия между dict и changes
		Set<String> categoriesToRemove = new HashSet<>(dict.keySet());
		categoriesToRemove.removeAll(changes.keySet());

		Set<String> categoriesToAdd = new HashSet<>(changes.keySet());
		categoriesToAdd.removeAll(dict.keySet());

		Set<String> categoriesToUpdate = new HashSet<>(changes.keySet());
		categoriesToUpdate.retainAll(dict.keySet());

		// Шаг 2: Удалить отсутствующие категории и их варианты
		categoryRepository.deleteAll(
				categoriesToRemove.stream()
						.map(c -> categoriesBeforeChanges.stream()
								.filter(cdb -> cdb.getName().equals(c))
								.findAny()
								.orElse(null))
						.filter(Objects::nonNull)
						.toList());

		// Шаг 3: Добавить новые категории и их варианты
		categoriesToAdd.forEach(c -> {
			Category category = new Category(null, c, userId, null);
			Category save = categoryRepository.save(category);
			Set<String> variants = changes.get(c);
			variants.forEach(v -> new Variant(null, v, save));
		});

		// Шаг 4: Обновить существующие категории (если изменились варианты)
		categoriesToUpdate.forEach(c -> {
			Category category = categoriesBeforeChanges.stream()
					.filter(cdb -> cdb.getName().equals(c)).findAny().orElse(null);
			if (category != null) {
				category = getCategoryWithVariants(category.getId());
				Category finalCategory = category;
				Set<String> currentVariants = dict.get(c); // Текущие варианты в базе
				Set<String> newVariants = changes.get(c); // Новые варианты из changes

				// варианты для удаления
				Set<String> variantsToRemove = new HashSet<>(currentVariants);
				variantsToRemove.removeAll(newVariants);
				// варианты для добавления
				Set<String> variantsToAdd = new HashSet<>(newVariants);
				variantsToAdd.removeAll(currentVariants);

				List<Variant> variantsInCategory = category.getVariants();
				List<Variant> listToRemove = variantsInCategory.stream().filter(v -> variantsToRemove.contains(v.getName())).toList();
				List<Variant> listToAdd = variantsToAdd.stream().map(v -> new Variant(null, v, finalCategory)).toList();
				variantsInCategory.removeAll(listToRemove);
				variantsInCategory.addAll(listToAdd);

				category.setVariants(variantsInCategory);
				categoryRepository.save(category);
			}
		});

		List<Category> categoriesAfterChanges = categoryRepository.findAllByUserId(userId);
		cacheEvict(categoriesAfterChanges);

		return changes;
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

	public List<String> splitInputTasks(String input) {
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
			historyService.changeDict(categories.getFirst().getUserId(), Action.REMOVE_CATEGORY,
					categories.stream().map(Category::getName).toList().toString());
			cacheEvict(categories);
		}
	}

	public void renameCategory(Category category, String newCategoryName) {
		category.setName(newCategoryName);
		categoryRepository.save(category);
		historyService.changeDict(category.getUserId(), Action.RENAME_CATEGORY, category.getName() + " -> " + newCategoryName);
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

	@Transactional
	public boolean addCategory(String categoryName, Long userId) {
		if (validateCategory(categoryName)) {
			List<Category> categoriesByUserId = getCategoriesByUserIdNoCache(userId);
			if (categoriesByUserId.stream().noneMatch(c -> c.getName().equals(categoryName))) {
				categoryRepository.save(new Category(categoryName, userId));
				historyService.changeDict(userId, Action.ADD_CATEGORY, categoryName);
				cacheEvict(categoriesByUserId);
				return true;
			}
		}
		return false;
	}

	@Transactional
	@Cacheable(value = CATEGORIES, unless = "#result == null")
	public List<Category> getCategoriesByUserId(Long userId) {
		return categoryRepository.findAllByUserId(userId);
	}

	public List<Category> getCategoriesByUserIdNoCache(Long userId) {
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
			List<String> addedVariants = addVariantsToCategory(variants, category);
			todoistService.changeCategoryForExistTasks(chatValue.getToken(), category, chatValue.getUser(), addedVariants);

			menuService.dictionaryMainMenu(chatValue, false);
			postAddedMessage(chatValue, category, variants.size(), addedVariants.size());
			chatValue.setState(State.DICT_SETTING);
			chatValue.setReplyParseModeHtml();

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

	public List<String> addVariantsToCategory(List<String> variants, Category targetCategory) {
		targetCategory = getCategoryWithVariants(targetCategory.getId());
		Category finalTargetCategory = targetCategory;
		List<Category> allCategories = getAllCategoryWithVariants(targetCategory.getUserId());
		variants = variants.stream()
				.filter(v -> !allCategories.stream()
						.flatMap(c -> c.getVariants().stream())
						.map(Variant::getName)
						.toList()
						.contains(v))
				.toList();
		variantRepository.saveAll(variants.stream().map(v -> new Variant(null, v, finalTargetCategory)).toList());
		historyService.changeDict(targetCategory.getUserId(), Action.ADD_VARIANT, variants.toString());
		return variants;
	}

	public Category getCategoryWithVariants(Long categoryId) {
		return categoryRepository.findCategoryWithVariants(categoryId);
	}

	public List<Category> getAllCategoryWithVariants(Long userid) {
		return categoryRepository.findAllCategoriesWithVariants(userid);
	}


	public Category getCategoryWithVariantsByName(Long userId, String categoryName) {
		return categoryRepository.findCategoryWithVariantsByName(userId, categoryName);
	}

	@Transactional
	public List<String> removeVariants(List<String> variants, Category category) {
		category = getCategoryWithVariants(category.getId());
		List<Variant> existVariants = category.getVariants();
		List<Variant> variantsForDelete = new ArrayList<>();
		existVariants.stream().filter(v -> variants.contains(v.getName())).forEach(variantsForDelete::add);
		if (!variantsForDelete.isEmpty()) {
			category.getVariants().removeAll(variantsForDelete);
			categoryRepository.save(category);
			cacheEvict(List.of(category));
			List<String> deleted = variantsForDelete.stream().map(Variant::getName).toList();
			historyService.changeDict(category.getUserId(), Action.REMOVE_VARIANT, deleted.toString());
			return deleted;
		} else {
			return List.of();
		}
	}

	@NotNull
	private static String findCategoryInDictionary(String word, Map<String, Set<String>> dict) {
		var lowerWord = word.toLowerCase();
		return dict.entrySet().stream()
				.filter(entry -> entry.getValue()
						.stream().map(String::toLowerCase).toList()
						.contains(lowerWord))
				.map(Map.Entry::getKey)
				.findAny()
				.orElse(UNKNOWN_CATEGORY);
	}

	private void postAddedMessage(ChatValue chatValue, Category category, int inputVariantsCount, int addedVariantsCount) {
		if (addedVariantsCount == inputVariantsCount) {
			chatValue.setReplyText("все " + addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.getName() + "</b>");
		} else if (inputVariantsCount > addedVariantsCount) {
			chatValue.setReplyText(addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.getName() + "</b>");
		} else if (addedVariantsCount == 0) {
			chatValue.setReplyText("уже существующие варианты нельзя передобавить заново");
		}
	}

	private boolean validateCategory(String category) {
		return category != null && !category.isBlank() && category.length() >= 2;
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

}
