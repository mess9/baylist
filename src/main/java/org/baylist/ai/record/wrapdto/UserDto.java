package org.baylist.ai.record.wrapdto;

import lombok.Data;
import org.baylist.db.entity.User;

import java.util.List;

@Data
public class UserDto {

	private String firstName;
	private String lastName;
	private List<String> friends;

	public UserDto convertToDDto(User user) {
		UserDto dto = new UserDto();
		dto.setFirstName(user.firstName());
		dto.setLastName(user.lastName());

//		if (Hibernate.isInitialized(user.getFriends())) {
//			List<String> friends = user.getFriends().stream()
//					.map(Util::getName)
//					.collect(Collectors.toList());
//			dto.setFriends(friends);
//		}

		return dto;
	}

}
