package com.animecharjudg;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class ResultController {

    @ModelAttribute
    public ImageForm setForm() {
        return new ImageForm();
    }

    @PostMapping("/result")
    public String upload(ImageForm imageForm, Model model) throws Exception {

        StringBuffer data = new StringBuffer();
        String base64 = new String(Base64.encodeBase64(imageForm.getImage().getBytes()),"ASCII");
        data.append("data:image/jpeg;base64,");
        data.append(base64);

        model.addAttribute("preview64image", data.toString());

        model.addAttribute("fileName", "ファイル名：" + imageForm.getImage().getOriginalFilename());


        return "02_Result";
    }

}