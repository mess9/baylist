package org.baylist.db.repo;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class FriendsDao {

	private final JdbcClient jdbc;

	public FriendsDao(DataSource ds) {
		this.jdbc = JdbcClient.create(ds);
	}

	public int add(long userId, long friendId) {
		return jdbc.sql("""
						insert into friends(user_id, friend_id)
						values (:u, :f)
						on conflict do nothing
						""")
				.param("u", userId)
				.param("f", friendId)
				.update();
	}

	public int remove(long userId, long friendId) {
		return jdbc.sql("delete from friends where user_id = :u and friend_id = :f")
				.param("u", userId)
				.param("f", friendId)
				.update();
	}

	public boolean existsLink(long userId, long friendId) {
		return jdbc.sql("select exists (select 1 from friends where user_id=:u and friend_id=:f)")
				.param("u", userId).param("f", friendId)
				.query(Boolean.class).single();
	}
}
