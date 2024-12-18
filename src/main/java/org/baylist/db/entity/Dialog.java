package org.baylist.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@Table(name = "dialogs")
public class Dialog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dialog_id")
	private Long dialogId;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@Column(name = "chat_id")
	private Long chatId;

	@Column(name = "is_report")
	private boolean isReport = false;

	@Column(name = "is_add_category")
	private boolean isAddCategory = false;


	public Dialog(User user, Long chatId) {
		this.user = user;
		this.chatId = chatId;
	}


}