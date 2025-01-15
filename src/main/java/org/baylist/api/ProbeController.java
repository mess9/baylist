package org.baylist.api;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@AllArgsConstructor
public class ProbeController {

    @GetMapping("/test")
    public String test() {
        return "make love not war";
    }


}
