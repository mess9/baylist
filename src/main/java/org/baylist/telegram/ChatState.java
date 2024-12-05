package org.baylist.telegram;

import lombok.Data;

@Data
public class ChatState {
    private String Title;
    private boolean expectingTitle;

    private Long taskId;
    private boolean editingTask;

    private boolean editingTitle;
    private boolean editingDescription;
    private boolean editingDeadline;

    private boolean editingNotify;
}
