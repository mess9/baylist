package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findAllByUserId(Long userId);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.variants where c.id = :categoryId")
	Category findCategoryWithVariants(Long categoryId);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.variants where c.name = :categoryName and c.userId = :userId")
	Category findCategoryWithVariantsByName(Long userId, String categoryName);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.variants where c.userId = :userId")
	List<Category> findAllCategoriesWithVariants(Long userId);

}