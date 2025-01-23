package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

import java.util.Map;
import java.util.Set;

public record UserWithDictRequest(User user, Map<String, Set<String>> dict) {

}
