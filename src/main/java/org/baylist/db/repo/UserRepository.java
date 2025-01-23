package org.baylist.db.repo;

import org.baylist.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUserId(Long userId);

	List<User> findByFriendsUserId(Long userId);

	@Query("SELECT u FROM User u LEFT JOIN FETCH u.friends where u.userId = :userId")
	User findUserWithMyFriends(@Param("userId") Long userId);


}