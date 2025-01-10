package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findCategoryByName(String name);

	List<Category> findAllByUserId(Long userId);
}