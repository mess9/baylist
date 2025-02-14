package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

import java.util.List;

public record UserWithChangeVariants(User user, String categoryName,
                                     List<String> variantsForChange,
                                     List<String> variantsNewNames) {

}
