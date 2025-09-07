package org.baylist.db.entity;


import org.baylist.dto.telegram.State;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("dialogs")
public record Dialog(
		@Id @Column("dialog_id") Long dialogId,
		@Column("user_id") Long userId,
		@Column("chat_id") Long chatId,
		@Column("state") State state
) {

}