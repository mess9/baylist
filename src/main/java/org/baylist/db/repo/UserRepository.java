package org.baylist.db.repo;

import org.baylist.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserId(Long userId);

}