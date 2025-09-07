package org.baylist.db.repo;

import org.baylist.db.entity.Category;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

	@Query("select c.* from categories c where c.user_id = :u")
	List<Category> findByUserId(@Param("u") long userId);

	@Query("SELECT c FROM categories c JOIN variants v on v.category_id = c.id where c.id = :categoryId")
	Category findCategoryWithVariants(Long categoryId);

	@Query("""
			select c.*
			from categories c
			where user_id = :u and lower(name) = lower(:name)
			""")
	Optional<Category> findByUserIdAndName(@Param("u") long userId, @Param("name") String name);

}