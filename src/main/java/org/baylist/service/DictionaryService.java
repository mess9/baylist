package org.baylist.service;

import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.baylist.dto.Constants.CATEGORIES;
import static org.baylist.dto.Constants.DICT;
import static org.baylist.dto.Constants.UNKNOWN_CATEGORY;

@Slf4j
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

	/* ======================= ПАРСИНГ И СЛОВАРЬ ======================= */

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

	@Cacheable(value = DICT, key = "#userId")
	public Map<String, Set<String>> getDict(Long userId) {
		List<Category> categories = categoryRepository.findByUserId(userId);
		if (categories.isEmpty()) {
			return null;
		} else {
			return categories.stream().collect(Collectors.toMap(
					Category::name,
					c -> variantRepository.findAllByCategoryId(c.id())
							.stream()
							.map(Variant::name)
							.collect(Collectors.toSet())));
		}
	}

	/* ======================= МЕНЮ/ВЫВОДЫ ======================= */

	public List<String> splitInputTasks(String input) {
		if (input != null) {
			return Arrays.stream(input.split("\n")).toList();
		}
		return new ArrayList<>();
	}

	/* ======================= CRUD КАТЕГОРИЙ ======================= */

	@Cacheable(value = CATEGORIES, unless = "#result == null")
	public List<Category> getCategoriesByUserId(Long userId) {
		return categoryRepository.findByUserId(userId);
	}

	public List<Category> getCategoriesByUserIdNoCache(Long userId) {
		return categoryRepository.findByUserId(userId);
	}

	public long countVariantsForUser(long userId) {
		return variantRepository.countVariantsForUser(userId);
	}

	public Category getCategoryByCategoryIdAndUserId(Long categoryId, Long userId) {
		// для правильной работы кешей дергаем через self
		DictionaryService self = context.getBean(DictionaryService.class);
		return self.getCategoriesByUserId(userId).stream()
				.filter(c -> c.id().equals(categoryId))
				.peek(c -> log.info("Found category: {}", c.name()))
				.findAny()
				.orElseThrow();
	}

	@Transactional
	public boolean addCategory(String categoryName, Long userId) {
		if (!validateCategory(categoryName)) return false;

		var categoriesByUserId = getCategoriesByUserIdNoCache(userId);
		boolean exists = categoriesByUserId.stream().anyMatch(c -> c.name().equalsIgnoreCase(categoryName));
		if (exists) return false;

		categoryRepository.save(new Category(null, categoryName, userId));
		historyService.changeDict(userId, Action.ADD_CATEGORY, categoryName);
		cacheEvict(categoriesByUserId);
		return true;
	}

	@Transactional
	public void removeCategory(List<Category> categories) {
		if (categories == null || categories.isEmpty()) return;

		// FK ON DELETE CASCADE удалит варианты
		categoryRepository.deleteAll(categories);
		historyService.changeDict(categories.getFirst().userId(), Action.REMOVE_CATEGORY,
				categories.stream().map(Category::name).toList().toString());
		cacheEvict(categories);
	}

	@Transactional
	public void renameCategory(Category category, String newCategoryName) {
		Category updated = new Category(category.id(), newCategoryName, category.userId());
		categoryRepository.save(updated);
		historyService.changeDict(updated.userId(), Action.RENAME_CATEGORY,
				category.name() + " -> " + newCategoryName);
		cacheEvict(List.of(updated));
	}

	/* ======================= ВАРИАНТЫ ======================= */

	public boolean validateVariants(String variants) {
		return variants != null && !variants.isBlank() && variants.length() >= 2;
	}

	@Transactional
	public List<String> addVariantsToCategory(List<String> variants, Category targetCategory) {
		List<String> input = variants.stream()
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.distinct()
				.toList();

		long userId = targetCategory.userId();
		long categoryId = targetCategory.id();

		// исключаем дубли по всем категориям пользователя
		Set<String> existingAll = new HashSet<>(variantRepository.findAllVariantsByUserId(userId)
				.stream().toList());
		List<String> toInsert = input.stream()
				.filter(v -> !existingAll.contains(v.toLowerCase()))
				.toList();

		if (toInsert.isEmpty()) return List.of();


		variantRepository.saveAll(toInsert.stream()
				.map(v -> new Variant(null, v, categoryId))
				.toList());

		historyService.changeDict(userId, Action.ADD_VARIANT, toInsert.toString());
		return toInsert;
	}

	/**
	 * Удаление вариантов по именам. Возвращает реально удалённые названия.
	 */
	@Transactional
	public List<String> removeVariants(List<String> variants, Category category) {
		if (variants == null || variants.isEmpty()) return List.of();

		long categoryId = category.id();
		Set<String> toDelete = variants.stream()
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.collect(Collectors.toSet());

		// какие реально существуют
		Set<String> existing = variantRepository.findAllByCategoryId(categoryId)
				.stream().map(Variant::name).collect(Collectors.toSet());
		List<String> present = toDelete.stream()
				.filter(existing::contains)
				.toList();

		if (present.isEmpty()) return List.of();

		variantRepository.deleteByCategoryIdAndNameIn(categoryId, present);
		cacheEvict(List.of(category));
		historyService.changeDict(category.userId(), Action.REMOVE_VARIANT, present.toString());
		return present;
	}

	/* ======================= Массовая замена словаря ======================= */

	@Transactional
	@SuppressWarnings("unused")
	public Map<String, Set<String>> changeAllDict(Long userId, Map<String, Set<String>> changes) {
		DictionaryService self = context.getBean(DictionaryService.class);
		Map<String, Set<String>> dict = Optional.ofNullable(self.getDict(userId)).orElseGet(HashMap::new);
		List<Category> categoriesBefore = self.getCategoriesByUserId(userId);

		// 1) вычисляем множества
		Set<String> oldCats = new HashSet<>(dict.keySet());
		Set<String> newCats = new HashSet<>(changes.keySet());

		Set<String> categoriesToRemove = new HashSet<>(oldCats);
		categoriesToRemove.removeAll(newCats);
		Set<String> categoriesToAdd = new HashSet<>(newCats);
		categoriesToAdd.removeAll(oldCats);
		Set<String> categoriesToUpdate = new HashSet<>(newCats);
		categoriesToUpdate.retainAll(oldCats);

		// 2) удаляем отсутствующие категории (варианты уйдут каскадом)
		if (!categoriesToRemove.isEmpty()) {
			List<Category> toDelete = categoriesToRemove.stream()
					.map(name -> categoriesBefore.stream()
							.filter(c -> c.name().equalsIgnoreCase(name))
							.findAny().orElse(null))
					.filter(Objects::nonNull)
					.toList();
			if (!toDelete.isEmpty()) categoryRepository.deleteAll(toDelete);
		}

		// 3) добавляем новые категории и их варианты
		for (String cName : categoriesToAdd) {
			Category saved = categoryRepository.save(new Category(null, cName, userId));
			Set<String> vars = Optional.ofNullable(changes.get(cName)).orElse(Set.of());
			if (!vars.isEmpty()) {
				List<Variant> rows = vars.stream()
						.map(v -> new Variant(null, v, saved.id()))
						.toList();
				variantRepository.saveAll(rows);
			}
		}

		// 4) обновляем пересекающиеся категории (добавляем/удаляем варианты)
		for (String cName : categoriesToUpdate) {
			Category cat = categoriesBefore.stream()
					.filter(c -> c.name().equalsIgnoreCase(cName))
					.findAny().orElseGet(() ->
							categoryRepository.findByUserIdAndName(userId, cName).orElseThrow()
					);

			long catId = cat.id();
			Set<String> current = variantRepository.findAllByCategoryId(catId).stream()
					.map(Variant::name).collect(Collectors.toSet());
			Set<String> fresh = Optional.ofNullable(changes.get(cName)).orElse(Set.of());

			Set<String> toRemove = new HashSet<>(current);
			toRemove.removeAll(fresh);
			Set<String> toAdd = new HashSet<>(fresh);
			toAdd.removeAll(current);

			if (!toRemove.isEmpty()) {
				variantRepository.deleteByCategoryIdAndNameIn(catId, toRemove);
			}
			if (!toAdd.isEmpty()) {
				List<Variant> rows = toAdd.stream()
						.map(v -> new Variant(null, v, catId))
						.toList();
				variantRepository.saveAll(rows);
			}
		}

		// 5) кеши
		List<Category> categoriesAfter = categoryRepository.findByUserId(userId);
		cacheEvict(categoriesAfter);

		return changes;
	}

	/* ======================= Утилиты ======================= */

	private boolean validateCategory(String category) {
		return category != null && !category.isBlank() && category.length() >= 2;
	}

	private void cacheEvict(List<Category> categories) {
		Cache cacheCategories = cacheManager.getCache(CATEGORIES);
		if (cacheCategories != null) {
			for (Category category : categories) cacheCategories.evict(category.userId());
		}
		Cache cacheDict = cacheManager.getCache(DICT);
		if (cacheDict != null) {
			for (Category category : categories) cacheDict.evict(category.userId());
		}
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

	public List<Category> getCategoriesByUserId(long userId) {
		return categoryRepository.findByUserId(userId);
	}


	private void postAddedMessage(ChatValue chatValue, Category category, int inputVariantsCount, int addedVariantsCount) {
		if (addedVariantsCount == inputVariantsCount) {
			chatValue.setReplyText("все " + addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.name() + "</b>");
		} else if (inputVariantsCount > addedVariantsCount) {
			chatValue.setReplyText(addedVariantsCount + " вариантов было добавлено в категорию - <b>" + category.name() + "</b>");
		} else if (addedVariantsCount == 0) {
			chatValue.setReplyText("уже существующие варианты нельзя передобавить заново");
		}
	}


}
