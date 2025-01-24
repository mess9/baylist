package org.baylist.db.repo;

import org.baylist.db.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository extends JpaRepository<Variant, Long> {

	List<Variant> findAllByCategoryId(Long id);

}