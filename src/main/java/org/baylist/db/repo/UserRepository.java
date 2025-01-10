package org.baylist.db.repo;

import org.baylist.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserId(Long userId);

	List<User> findByFriendsUserId(Long userId);
}