package org.baylist.db.repo;

import org.baylist.db.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	@Cacheable(value = "user", unless = "#result == null")
	Optional<User> findByUserId(Long userId);

}