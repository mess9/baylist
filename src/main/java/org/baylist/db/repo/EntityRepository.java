package org.baylist.db.repo;

import org.baylist.db.entity.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<Entity, Long> {
}