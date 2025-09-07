package org.baylist.db.repo;

import org.baylist.db.entity.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

	@Query("""
			select * from users
			where user_id = :userId
			""")
	User findByUserId(Long userId);


	@Query("""
			  select u.*
			  from users u
			  join friends f on f.friend_id = u.user_id
			  where f.user_id = :u
			""")
	List<User> friendsOf(@Param("u") long userId);

	@Query("""
			  select u.*
			  from users u
			  join friends f on f.user_id = u.user_id
			  where f.friend_id = :me
			""")
	List<User> friendMe(@Param("me") long meUserId);
}