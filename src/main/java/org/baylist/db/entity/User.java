package org.baylist.db.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("users")
public record User(
		@Id @Column("user_id") Long userId,
		@Column("first_name") String firstName,
		@Column("last_name") String lastName,
		@Column("todoist_token") String todoistToken,
		@Column("registered") OffsetDateTime registered,
		@Column("last_seen") OffsetDateTime lastSeen
) {

	public String bearerToken() {
		return todoistToken == null ? null : "Bearer " + todoistToken;
	}
}
