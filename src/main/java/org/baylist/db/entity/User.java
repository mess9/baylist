package org.baylist.db.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"dialog", "friends"})
@Table(name = "users")
public class User {

	@Id
	@Column(name = "user_id", nullable = false, unique = true)
	private Long userId;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "todoist_token")
	private String todoistToken;

	@Column(name = "registered")
	private OffsetDateTime registered;

	@Column(name = "last_seen")
	private OffsetDateTime lastSeen;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Dialog dialog;

	@ManyToMany
	@JoinTable(
			name = "friends",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "friend_id")
	)
	private List<User> friends = new ArrayList<>();

}
