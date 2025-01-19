package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	List<Category> findAllByUserId(Long userId);

	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.variants where c.id = :id")
	Category findCategoryWithVariants(@Param("id") Long id);

}