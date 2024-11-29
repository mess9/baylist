package org.baylist.todoist.dto;

import lombok.Data;

@Data
public class Section {
    private String id;
    private String v2Id;
    private String projectId;
    private String v2ProjectId;
    private int order;
    private String name;
}
