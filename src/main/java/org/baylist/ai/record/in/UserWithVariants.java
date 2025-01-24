package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

import java.util.List;

public record UserWithVariants(User user, String categoryName, List<String> variants) {

}
