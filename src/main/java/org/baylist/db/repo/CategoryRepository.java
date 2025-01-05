package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	@Cacheable(value = "category", unless = "#result == null")
	Category findCategoryByName(String name);
}