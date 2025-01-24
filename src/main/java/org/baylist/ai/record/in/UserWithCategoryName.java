package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

public record UserWithCategoryName(User user, String categoryName) {

}
