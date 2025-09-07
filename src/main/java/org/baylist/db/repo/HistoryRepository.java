package org.baylist.db.repo;

import org.baylist.db.entity.History;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Long> {

}