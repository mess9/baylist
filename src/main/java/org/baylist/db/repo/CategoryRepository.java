package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	Category findCategoryByName(String name);

}