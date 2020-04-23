package com.animecharjudg;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller // (1)
public class TopController {

	@RequestMapping("/top") // (2)
    public String toTop(Model model) {
        return "01_Top";
    }
}