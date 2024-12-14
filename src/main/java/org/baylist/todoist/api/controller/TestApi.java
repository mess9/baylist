package org.baylist.todoist.api.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@AllArgsConstructor
public class TestApi {

    @GetMapping("/test")
    public String test() {
        return "make love not war";
    }


}
