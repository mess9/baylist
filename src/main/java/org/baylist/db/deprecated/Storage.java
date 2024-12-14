package org.baylist.db.deprecated;

import lombok.Data;

import java.util.List;

@Data
public class Storage {

    private List<ProjectDb> projects;

    public boolean isEmpty() {
        return projects == null || projects.isEmpty();
    }


}
