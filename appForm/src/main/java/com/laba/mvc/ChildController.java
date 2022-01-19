package com.laba.mvc;

import com.laba.entity.Child;
import com.laba.entity.Parent;
import com.laba.repository.ChildRepository;
import com.laba.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/**
 * mvc контроллер для ребенка
 */
@Controller
public class ChildController {
@Autowired
    ChildRepository childRepository;
    @Autowired
    AppService appService;

    @GetMapping(value = "/children")
    public String showAll(Model model, Principal principal){
        List<Child> children = appService.getChildren();
        model.addAttribute("children",children);
        return "children";
    }


    @GetMapping(value = "/child")
    public String show(Model model, Principal principal){
        Child children = childRepository.findByLogin(principal.getName());
        model.addAttribute("child",children);
        return "child/index";
    }
}
