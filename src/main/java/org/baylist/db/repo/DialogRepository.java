package org.baylist.db.repo;

import org.baylist.db.entity.Dialog;
import org.baylist.dto.telegram.State;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


public interface DialogRepository extends CrudRepository<Dialog, Long> {

	@Query("""
			select *
			from dialogs
			where user_id = :userId
			""")
	Dialog findByUserId(@Param("userId") long userId);

	@Modifying
	@Query("""
			update dialogs set state = :state
			where user_id = :userId
			""")
	int updateState(@Param("userId") long userId, @Param("state") State state);
}
