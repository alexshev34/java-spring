package com.laba.service;

import com.laba.entity.*;
import com.laba.form.ChildForm;
import com.laba.form.ParentForm;
import com.laba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
public class AppService {
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    EducationalInstitutionRepository institutionRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ParentRepository parentRepository;
    @Autowired
    ChildRepository childRepository;

    // возвращает список регионов и адресов, и учебных заведений
    public Map<String,String[][]> getFullRegionInfo(){
        List<Region> regions = regionRepository.findAll();
        final Map<String,String[][]> map = new LinkedHashMap<>();
        regions.sort((o1, o2)->o1.getName().compareTo(o2.getName()));
        for(Region region:regions){
            String name = region.getName();
            String[][] arr =new String[2][];
            List<Address> addresses = region.getAddresses();

            Map<String, List<String>> addMap = addresses.stream().collect(groupingBy(Address::getStreet,
                    mapping(Address::getHouse, toList())));

            arr[0] = new String[addMap.keySet().size()];
            int i=0;
            List<String> keys = new ArrayList<>(addMap.keySet());
            Collections.sort(keys);
            for(String street:keys){
                String add = street.concat(": ").concat(addMap.get(street).stream()
                                .mapToInt(Integer::parseInt).boxed().sorted().map(a->Integer.toString(a)).collect(joining(",")));
                arr[0][i]=add;
                i++;
            }
            final Set<EducationalInstitution> educationalInstitutions = new HashSet<>();
            for(Address address:addresses){
                educationalInstitutions.addAll(institutionRepository.searchByAddress(address));
            }
            arr[1] = new String[educationalInstitutions.size()];
             i=0;
            for(EducationalInstitution ed:educationalInstitutions.stream().sorted((e1,e2)->e1.getNum().compareTo(e2.getNum())).collect(Collectors.toList())){
                String add = "№ ".concat(ed.getNum().concat(", ").concat(ed.getAddress().getStreet()).concat(", ").concat(ed.getAddress().getHouse()));
                arr[1][i]=add;
                i++;
            }
            map.put(name,arr);
        }
        return map;
    }

    /**
     * сохранение родителя в базе
     * @param form
     * @return
     * @throws Exception
     */
    @Transactional
    public Parent saveParent(ParentForm form) throws Exception{
        Optional<Address> address = addressRepository.findById(form.getAddress());
        if (!address.isPresent()) throw  new IllegalArgumentException("Адрес не найден!");
        Parent parent = new Parent();
        parent.setLogin(form.getLogin());
        parent.setPassword(passwordEncoder.encode(form.getPassword()));
        parent.setRole(ROLE.ROLE_PARENT);
        parent.setAddress(address.get());
        parent.setFirstName(form.getFirstName());
        parent.setLastName(form.getLastName());
        return parentRepository.saveAndFlush(parent);

    }

    /**
     * сохранение ребенка в базе
     * @param form
     * @param login
     * @return
     * @throws Exception
     */
    @Transactional
    public Child saveChild(ChildForm form,String login) throws Exception{
        Optional<EducationalInstitution> educationalInstitution = institutionRepository.findById(form.getEducation());
        if (!educationalInstitution.isPresent()) throw  new IllegalArgumentException("Заведение не найдено!");
        Parent parent = parentRepository.findByLogin(login);
        if (parent==null)  throw  new IllegalArgumentException("Родитель не найден!");
        Child child = new Child();
        child.setLogin(form.getLogin());
        child.setPassword(passwordEncoder.encode(form.getPassword()));
        child.setRole(ROLE.ROLE_CHILD);
        child.setEducationalInstitution(educationalInstitution.get());
        child.setFirstName(form.getFirstName());
        child.setLastName(form.getLastName());
        child.setAge(form.getAge());
        child.getParents().add(parent);
        return childRepository.saveAndFlush(child);

    }

    // возращает список регионво
    public List<Region> getRegions(){
        List<Region> regions = regionRepository.findAll();
        return  regions;
    }

    // возвращает список родителей
    public List<Parent> getParents(){
        return parentRepository.findAll(Sort.by(Sort.Direction.ASC,"lastName"));
    }

    // возращает список детей
    public List<Child> getChildren(){
        return childRepository.findAll(Sort.by(Sort.Direction.ASC,"lastName"));
    }

    public List<EducationalInstitution> getEducationalInstitutions(Set<Address> addresses){
        Set<EducationalInstitution> educationalInstitutions = new HashSet<>();
        for(Address address:addresses){
            educationalInstitutions.addAll(institutionRepository.searchByAddress(address));
        }
        return educationalInstitutions.stream().sorted((e1,e2)->e1.getNum().compareTo(e2.getNum())).collect(toList());
    }

}
