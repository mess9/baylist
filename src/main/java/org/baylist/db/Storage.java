package org.baylist.db;

import lombok.Data;

import java.util.List;

@Data
public class Storage {

    private List<ProjectDb> projects;

    public boolean isEmpty() {
        return projects == null || projects.isEmpty();
    }


}
