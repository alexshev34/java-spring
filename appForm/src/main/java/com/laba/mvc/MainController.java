package com.laba.mvc;

import com.laba.entity.Region;
import com.laba.repository.AddressRepository;
import com.laba.repository.RegionRepository;
import com.laba.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * mvc контроллер входа и выхода))
 */
@Controller
public class MainController {
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    AppService appService;


    @GetMapping("/")
    public String homePage(Model model) {

        Map<String, String[][]> map = appService.getFullRegionInfo();
        model.addAttribute("regions",map);
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

    @RequestMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
