package com.laba.mvc;

import com.laba.entity.*;
import com.laba.form.ChildForm;
import com.laba.form.ParentForm;
import com.laba.repository.*;
import com.laba.service.AppService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;


/**
 * mvс контроллер родителя
 */
@Controller
public class ParentController {
    @Autowired
    RegionRepository regionRepository;

    @Autowired
    UsersRepository usersRepository;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ChildRepository childRepository;

    @Autowired
    AppService appService;

    /**
     * выдача страницы регистрации родителя
     */
    @GetMapping("/registration")
    public String registerParent(Model model){
        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions",regions);
        model.addAttribute("parentForm",new ParentForm());
        return "register";
    }

    /**
     * сохранения родителя
     * @param parentForm форма с данными
     * @param bindingResult ошибки
     * @param model модель
     * @return страница вывода
     */
    @PostMapping("/registration")
    public String createParent(@ModelAttribute @Valid ParentForm parentForm,
                               BindingResult bindingResult, Model model){
        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions",regions);
        System.out.println(bindingResult.getModel());
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!parentForm.getPassword().equals(parentForm.getPasswordConfirm())){
            bindingResult.rejectValue("passwordConfirm", "passwordConfirm.error", "Пароли не совпадают");
            return "register";
        }

        if (usersRepository.findByLogin(parentForm.getLogin()).isPresent()){
            bindingResult.rejectValue("login", "loginError", "Пользователь с таким именем уже существует");
            return "register";
        }


        try {
            appService.saveParent(parentForm);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/parent";
    }

    /**
     * главная страница для роли родитель
     * @param model
     * @param principal
     * @return
     */
    @GetMapping(value = "/parent")
    public String index(Model model, Principal principal){
        Parent parent = parentRepository.findByLogin(principal.getName());
        ParentForm parentForm = new ParentForm();
        parentForm.setLogin(parent.getLogin());
        parentForm.setFirstName(parent.getFirstName());
        parentForm.setLastName(parent.getLastName());
        parentForm.setAddress(parent.getAddress().getId());
        model.addAttribute("parentForm",parentForm);
        model.addAttribute("parent",parent);

        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions",regions);
        return "parent/index";
    }

    /**
     * общая страница - список всех родителей
     * @param model
     * @param principal
     * @return
     */
    @GetMapping(value = "/parents")
    public String showAll(Model model, Principal principal){
        List<Parent> parents = appService.getParents();
        model.addAttribute("parents",parents);
        return "parents";
    }

    /**
     * смена адреса родителя
     * @param parentForm
     * @param bindingResult
     * @param model
     * @return
     */
    @PostMapping("/parent")
    public String updateParent(@ModelAttribute  ParentForm parentForm,
                               BindingResult bindingResult, Model model){
        List<Region> regions = regionRepository.findAll();
        model.addAttribute("regions",regions);
        Parent parent  = parentRepository.findByLogin(parentForm.getLogin());
        Address address = addressRepository.findById(parentForm.getAddress()).get();
        parent.setAddress(address);
        parentRepository.save(parent);
        return "redirect:/parent";
    }

    /**
     * возвращает страницу ребенка по логину
     * @param model
     * @param principal
     * @param login
     * @return
     */
    @GetMapping("/parent/child/{login}")
    public String getChild(Model model, Principal principal, @PathVariable String login){
        Parent parent = parentRepository.findByLogin(principal.getName());
        Set<Address> addresses = new HashSet<>();
        addresses.add(parent.getAddress());
        Child child = childRepository.findByLogin(login);
        Optional<Parent> otherParent = child.getParents().stream().filter(p -> !p.getLogin().equals(parent.getLogin())).findFirst();
        if (otherParent.isPresent()){
            addresses.add(otherParent.get().getAddress());
        }
        List<EducationalInstitution> educations = appService.getEducationalInstitutions(addresses);

        model.addAttribute("educations",educations);
        model.addAttribute("child",child);
        List<Parent> parents = parentRepository.findAll().stream().sorted((p1,p2)->p1.getLastName().compareTo(p2.getLastName())).collect(Collectors.toList());
        model.addAttribute("parents",parents);

        return "parent/child";
    }

    /**
     * обновляет возраст и учебное заведение ребенка
     * @param model
     * @param principal
     * @param login
     * @param child
     * @return
     */
    @PostMapping("/parent/child/{login}")
    public String updateChild(Model model, Principal principal, @PathVariable String login,
                              @ModelAttribute Child child){
        Parent parent = parentRepository.findByLogin(principal.getName());
        Set<Address> addresses = new HashSet<>();
        addresses.add(parent.getAddress());
        Child oldChild = childRepository.findByLogin(login);
        if (oldChild==null){
            return "redirect:/parent";
        }
        oldChild.setAge(child.getAge());
        oldChild.setEducationalInstitution(child.getEducationalInstitution());
        childRepository.save(oldChild);

        return "redirect:/parent/child/"+login;
    }

    /**
     * возвращает страницу создания нового ребенка
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/parent/add/child")
    public String prepareAdd(Model model,Principal principal){
        Parent parent = parentRepository.findByLogin(principal.getName());
        Set<Address> addresses = new HashSet<>();
        addresses.add(parent.getAddress());
        List<EducationalInstitution> educations = appService.getEducationalInstitutions(addresses);
        model.addAttribute("educations",educations);
        model.addAttribute("childForm",new ChildForm());
        return "parent/newChild";
    }

    /**
     * сохранение ребенка в базу
     * @param childForm
     * @param bindingResult
     * @param model
     * @param principal
     * @return
     */
    @PostMapping("/parent/add/child")
    public String createChild(@ModelAttribute @Valid ChildForm childForm,
                               BindingResult bindingResult, Model model,Principal principal){
        Parent parent = parentRepository.findByLogin(principal.getName());
        Set<Address> addresses = new HashSet<>();
        addresses.add(parent.getAddress());
        List<EducationalInstitution> educations = appService.getEducationalInstitutions(addresses);
        model.addAttribute("educations",educations);
        if (bindingResult.hasErrors()) {
            return "parent/newChild";
        }
        if (!childForm.getPassword().equals(childForm.getPasswordConfirm())){
            bindingResult.rejectValue("passwordConfirm", "passwordConfirm.error", "Пароли не совпадают");
            return "parent/newChild";
        }

        if (usersRepository.findByLogin(childForm.getLogin()).isPresent()){
            bindingResult.rejectValue("login", "loginError", "Пользователь с таким именем уже существует");
            return "parent/newChild";
        }


        try {
            appService.saveChild(childForm,principal.getName());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "parent/newChild";
        }
        return "redirect:/parent";
    }

    /**
     * удаление ребенка
     * @param login
     * @param principal
     * @param model
     * @param redirectAttrs
     * @return
     */
    @PostMapping("/parent/child/del/{login}")
    public String delChild(@PathVariable String login, Principal principal, Model model,
                           RedirectAttributes redirectAttrs){
        Child child = childRepository.findByLogin(login);
        if (child==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Ребенок не найден!");
            return "redirect:/parent";
        }
        childRepository.delete(child);
        return "redirect:/parent";
    }

    /**
     * удаление родителя у ребенка
     * @param childLogin логин ребенка
     * @param parentLogin логин родителя
     * @param model
     * @param redirectAttrs
     * @return
     */
    @PostMapping("/parent/{childLogin}/del/{parentLogin}")
    public String delParentFromChild(@PathVariable String childLogin,@PathVariable String parentLogin,
                                     Model model,
                                     RedirectAttributes redirectAttrs){
        Child child = childRepository.findByLogin(childLogin);
        if (child==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Ребенок не найден!");
            return "redirect:/parent";
        }
        Parent parent = parentRepository.findByLogin(parentLogin);
        if (parent==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Ребенок не найден!");
            return "redirect:/parent/child/"+childLogin;
        }
        child.getParents().remove(parent);
        childRepository.save(child);
        return "redirect:/parent/child/"+childLogin;
    }

    /**
     * добавление родителя к ребенку
     * @param login логин ребенка
     * @param parentLogin логин родителя
     * @param redirectAttrs
     * @return
     */
    @PostMapping("/parent/child/addParent/{login}")
    public String addParentToChile(@PathVariable String login,@RequestBody String parentLogin, RedirectAttributes redirectAttrs){
        Child child = childRepository.findByLogin(login);
        if (child==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Ребенок не найден!");
            return "redirect:/parent";
        }
        System.out.println(parentLogin);
        Parent parent = parentRepository.findByLogin(parentLogin.replace("parentLogin=",""));
        if (parent==null){
            redirectAttrs.addFlashAttribute("errorMessage", "Родитель не найден!");
            return "redirect:/parent/child/"+login;
        }
        child.getParents().add(parent);
        childRepository.save(child);
        return "redirect:/parent/child/"+login;
    }

}
