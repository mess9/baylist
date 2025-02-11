package org.baylist.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.baylist.dto.telegram.Action;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Table(name = "history")
@AllArgsConstructor
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "source", nullable = false)
	private User source;

	@ManyToOne
	@JoinColumn(name = "recipient", nullable = false)
	private User recipient;

	@Column(name = "datetime", nullable = false)
	private OffsetDateTime date;

	@Column(name = "action", nullable = false)
	@Enumerated(EnumType.STRING)
	private Action action;

	@Column(name = "content")
	private String content;
}
