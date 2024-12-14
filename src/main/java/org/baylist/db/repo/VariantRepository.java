package org.baylist.db.repo;

import org.baylist.db.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VariantRepository extends JpaRepository<Variant, Long> {
    Variant findByNameAndCategoryId(String name, Long categoryId);
}