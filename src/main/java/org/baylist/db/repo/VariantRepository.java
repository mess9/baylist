package org.baylist.db.repo;

import org.baylist.db.entity.Variant;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VariantRepository extends CrudRepository<Variant, Long> {

	// суммарное число вариантов по всем категориям пользователя
	@Query("""
			  select count(*) from variants v
			  join categories c on c.id = v.category_id
			  where c.user_id = :u
			""")
	long countVariantsForUser(@Param("u") long userId);

	@Query("select * from variants where category_id = :id")
	List<Variant> findAllByCategoryId(Long id);

	@Query("""
			select v.name
			from variants v
			join categories c on c.id = v.category_id
			where c.user_id = :u
			""")
	List<String> findAllVariantsByUserId(@Param("u") long userId);

	@Modifying
	@Query("""
			delete from variants v
			where v.category_id = :c and v.name in (:names)
			""")
	int deleteByCategoryIdAndNameIn(@Param("c") long categoryId, @Param("names") Collection<String> names);

}
