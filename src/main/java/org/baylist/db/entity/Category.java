package org.baylist.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("categories")
public record Category(
		@Id @Column("id") Long id,
		@Column("name") String name,
		@Column("user_id") Long userId
) {

}
