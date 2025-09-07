package org.baylist.db.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tasks")
public record Task(
		@Id @Column("task_id") Long taskId,
		@Column("user_id") Long userId,
		@Column("section") String section,
		@Column("task_order") Integer taskOrder,
		@Column("content") String content,
		@Column("is_completed") Boolean isCompleted
) {

}
