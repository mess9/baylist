package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

public record UserWithCategoryRename(User user, String originalCategoryName, String newCategoryName) {

}
