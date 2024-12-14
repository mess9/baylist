package org.baylist.todoist.api.controller;

import lombok.AllArgsConstructor;
import org.baylist.todoist.api.Todoist;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController("/")
@AllArgsConstructor
public class TestApi {

    private final Todoist todoist;
    private final RestClient restClient;

    @GetMapping("/test")
    public String test() {
        return "test";
    }


}
