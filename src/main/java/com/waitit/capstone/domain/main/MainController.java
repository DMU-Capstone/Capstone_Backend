package com.waitit.capstone.domain.main;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/main")
public class MainController {

    private final MainService mainService;

}
