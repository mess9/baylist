package org.baylist.ai.record.wrapdto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.baylist.db.entity.User;
import org.baylist.util.Util;
import org.hibernate.Hibernate;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class UserDto {

	private String firstName;
	private String lastName;
	private List<String> friends;

	public UserDto convertToDDto(User user) {
		UserDto dto = new UserDto();
		dto.setFirstName(user.getFirstName());
		dto.setLastName(user.getLastName());

		if (Hibernate.isInitialized(user.getFriends())) {
			List<String> friends = user.getFriends().stream()
					.map(Util::getName)
					.collect(Collectors.toList());
			dto.setFriends(friends);
		}

		return dto;
	}

}
