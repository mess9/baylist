package org.baylist.ai.record.in;

import org.baylist.db.entity.User;

import java.util.List;

public record UserRequestWithTasks(User user, List<String> tasksNames) {

}
