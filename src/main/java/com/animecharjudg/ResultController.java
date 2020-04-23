package com.animecharjudg;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller // (1)
public class ResultController {
	@RequestMapping("/result") // (2)
    public String toTop(Model model) {
        return "02_Result";
    }
}