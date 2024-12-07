package org.baylist.dto.dict;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode
public class BuyCategoryDict {

    private Map<String, Set<String>> dict = new HashMap<>();

    @JsonAnySetter
    public void addYaml(String key, Set<String> value) {
        dict.put(key, value);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        dict.forEach((category, words) -> {
            sb.append(category).append(":\n");
            words.forEach(word -> sb.append(" - ").append(word).append("\n"));
        });
        return sb.toString();
    }



}
