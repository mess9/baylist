package org.baylist.ai.record.in;

import org.baylist.db.entity.User;
import org.baylist.dto.todoist.api.TaskRequest;

public record UserRequestWithTask(User user, TaskRequest task) {

}
