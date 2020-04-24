package com.animecharjudg;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller // (1)
public class TopController {

    @ModelAttribute
    public ImageForm setForm() {
        return new ImageForm();
    }

	@RequestMapping("/top") // (2)
    public String toTop(Model model) {
        return "01_Top";
    }

}