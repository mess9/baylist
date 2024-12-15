package org.baylist.db.repo;

import org.baylist.db.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VariantRepository extends JpaRepository<Variant, Long> {

    Variant findByNameAndCategoryId(String name, Long categoryId);

    List<Variant> findByCategoryId(Long id);
}