package org.baylist.ai;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.baylist.ai.record.in.UserWithCategoryName;
import org.baylist.ai.record.in.UserWithCategoryRename;
import org.baylist.ai.record.in.UserWithChangeVariants;
import org.baylist.ai.record.in.UserWithDictRequest;
import org.baylist.ai.record.in.UserWithVariants;
import org.baylist.ai.record.out.ChangedVariants;
import org.baylist.ai.record.out.CreatedCategory;
import org.baylist.ai.record.out.CreatedVariants;
import org.baylist.ai.record.out.DeletedCategory;
import org.baylist.ai.record.out.DeletedVariants;
import org.baylist.ai.record.out.Dictionary;
import org.baylist.ai.record.out.RenamedCategory;
import org.baylist.db.entity.Category;
import org.baylist.exception.AiException;
import org.baylist.service.DictionaryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class AiDataChanger {

	DictionaryService dictionaryService;

	//region DICT

	@SuppressWarnings("unused")
	public Dictionary changeAllDict(UserWithDictRequest userRequest) {
		log.info("ai function called - changeAllDict");
		try {
			return new Dictionary(dictionaryService.changeAllDict(userRequest.user().getUserId(), userRequest.dict()));
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public RenamedCategory renameCategory(UserWithCategoryRename userRequest) {
		log.info("ai function called - renameCategory");
		try {
			Category category = dictionaryService.getCategoryWithVariantsByName(userRequest.user().getUserId(),
					userRequest.originalCategoryName());
			dictionaryService.renameCategory(category, userRequest.newCategoryName());
			return new RenamedCategory(
					userRequest.originalCategoryName(),
					userRequest.newCategoryName());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public CreatedCategory createCategory(UserWithCategoryName userRequest) {
		log.info("ai function called - createCategory");
		try {
			dictionaryService.addCategory(userRequest.categoryName(), userRequest.user().getUserId());
			return new CreatedCategory(userRequest.categoryName());
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public DeletedCategory deleteCategory(UserWithCategoryName userRequest) {
		log.info("ai function called - deleteCategory");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				dictionaryService.removeCategory(List.of(category));
				return new DeletedCategory(category.getName());
			} else {
				return new DeletedCategory("категория не была найдена и поэтому категория не была удалена");
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public ChangedVariants changeVariants(UserWithChangeVariants userRequest) {
		log.info("ai function called - changeVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				return new ChangedVariants(
						deleteVariants(
								new UserWithVariants(userRequest.user(),
										userRequest.categoryName(),
										userRequest.variantsForChange()))
								.deletedVariants(),
						createVariants(
								new UserWithVariants(userRequest.user(),
										userRequest.categoryName(),
										userRequest.variantsNewNames()))
								.createdVariants()
				);
			} else {
				return new ChangedVariants(
						List.of("категория не была найдена и поэтому варианты не были изменены"),
						List.of());
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public DeletedVariants deleteVariants(UserWithVariants userRequest) {
		log.info("ai function called - deleteVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				List<String> deletedVariants = dictionaryService
						.removeVariants(userRequest.variants(), category);
				return new DeletedVariants(deletedVariants);
			} else {
				return new DeletedVariants(List.of("категория не была найдена и поэтому варианты не были удалены"));
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	public CreatedVariants createVariants(UserWithVariants userRequest) {
		log.info("ai function called - createVariants");
		try {
			Category category = dictionaryService.getCategoriesByUserId(userRequest.user().getUserId()).stream()
					.filter(c -> c.getName().equals(userRequest.categoryName()))
					.findAny()
					.orElse(null);
			if (category != null) {
				List<String> addedVariantsToCategory = dictionaryService
						.addVariantsToCategory(userRequest.variants(), category);
				return new CreatedVariants(addedVariantsToCategory);
			} else {
				return new CreatedVariants(List.of("категория не была найдена и поэтому варианты не были добавлены"));
			}
		} catch (Exception e) {
			throw new AiException(e.getMessage());
		}
	}

	//endregion DICT

}
