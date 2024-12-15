package org.baylist.dto.todoist.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Label {
    private String id;
    private String name;
    private String color;
    private Integer order;
    @JsonProperty("is_favorite")
    private boolean isFavorite;

}
