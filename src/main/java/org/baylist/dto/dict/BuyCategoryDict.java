package org.baylist.dto.dict;

import lombok.Data;

import java.util.List;

@Data
public class BuyCategoryDict {

    private String category;
    private List<String> words;

}
