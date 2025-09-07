package org.baylist.db.repo;

import org.baylist.db.entity.Task;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

	@Query("select t.* from tasks t where t.user_id = :userId")
	List<Task> findByUser(Long userId);

}