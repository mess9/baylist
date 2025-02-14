package org.baylist.db.repo;

import org.baylist.db.entity.Task;
import org.baylist.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByUser(User user);

}