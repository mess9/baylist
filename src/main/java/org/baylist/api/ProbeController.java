package org.baylist.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProbeController {

    @GetMapping("/test")
    public String test() {
        return "make love not war";
    }

}
