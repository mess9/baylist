package org.baylist.db.entity;

import lombok.Builder;
import org.baylist.dto.telegram.Action;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

@Table("history")
@Builder
public record History(
		@Id @Column("id") Long id,
		@Column("source") Long source,
		@Column("recipient") Long recipient,
		@Column("datetime") OffsetDateTime date,
		@Column("action") Action action,
		@Column("content") String content
) {

}