package org.baylist.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("variants")
public record Variant(
		@Id @Column("id") Long id,
		@Column("name") String name,
		@Column("category_id") Long categoryId
) {

}
