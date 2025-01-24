package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

public record UserRequestWithFriend(User user, String friendName) {

}
