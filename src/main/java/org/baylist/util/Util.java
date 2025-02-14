package org.baylist.util;

import lombok.experimental.UtilityClass;
import org.baylist.db.entity.User;
import org.telegram.telegrambots.meta.api.objects.Contact;

@UtilityClass
public class Util {

	public static String getName(User user) {
		if (user.getLastName() != null) {
			return user.getFirstName() + " " + user.getLastName();
		} else {
			return user.getFirstName();
		}
	}

	public static String getName(Contact user) {
		if (user.getLastName() != null) {
			return user.getFirstName() + " " + user.getLastName();
		} else {
			return user.getFirstName();
		}
	}


}
