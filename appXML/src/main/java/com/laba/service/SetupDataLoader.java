package com.laba.service;

import com.laba.entity.*;
import com.laba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Random;


/**
 * так как у нас база в памяти - то нужно её заполнять при первом запуске, поэтому
 * сформируем справочиники районов, адресов и учебных заведений 
 */
@Component
public class SetupDataLoader  implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    EducationalInstitutionRepository educationalInstitutionRepository;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    ChildRepository childRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        init();
    }

    @Transactional
    public void init(){

    if (regionRepository.findAll().size()>0) return;

    String[] regions ={"Центральный","Западный","Восточный","Северный"};
    String[] streets ={"Центральная","Молодежная","Школьная","Лесная",
        "Садовая",   "Советская", "Новая","Некрасова",
            "Набережная",    "Заречная",     "Зеленая","Горная",
            "Цветочная","Березовая","Мичурина","Чкалова",
            "Лермонтова","Пушкина","Толстого","Трудовая"};
    int i=0;
    int k =1;
    for(String name:regions){
        Region region = new Region(name);
        region =  regionRepository.save(region);
        Random random = new Random();
        for(int j=0;j<4;j++){
            String street = streets[i];
            int r = random.nextInt(5)+5;
            while (r>0){
                Address address = new Address(street, Integer.toString(r));
                address.setRegion(region);
                addressRepository.save(address);
                if (((i+1)%4==0 && r==1) || (((i+1)%4!=0) && random.nextInt(100)>93)){
                    address = new Address(street, Integer.toString((random.nextInt(10)+10)));
                    address.setRegion(region);
                    address = addressRepository.save(address);
                    EducationalInstitution educationalInstitution = new EducationalInstitution(Integer.toString(k),address);
                    educationalInstitutionRepository.save(educationalInstitution);
                    k++;
                }
                r--;
            }
            i++;
        }

    }
     Address address = addressRepository.findAll().get(0);
     Parent parent = new Parent();
     parent.setLogin("ivanov");
     parent.setFirstName("Иван");
     parent.setLastName("Иванов");
     parent.setRole(ROLE.ROLE_PARENT);
     parent.setAddress(address);
     parent.setPassword(passwordEncoder.encode("123456"));
     parent = parentRepository.saveAndFlush(parent);

     System.out.println(parent);

     Parent parent2 = new Parent();
     parent2.setLogin("ivanova");
     parent2.setFirstName("Светлана");
     parent2.setLastName("Ивановa");
     parent2.setRole(ROLE.ROLE_PARENT);
     parent2.setAddress(address);
     parent2.setPassword(passwordEncoder.encode("123456"));
     parent2 = parentRepository.saveAndFlush(parent2);

     System.out.println(parent2);

     EducationalInstitution ed = educationalInstitutionRepository.searchByAddress(address).get(0);

     Child child = new Child();
     child.setLogin("serg");
     child.setFirstName("Сергей");
     child.setLastName("Иванов");
     child.setRole(ROLE.ROLE_CHILD);
     child.setAge(10);
     child.setEducationalInstitution(ed);
     child.setPassword(passwordEncoder.encode("123456"));
     child.getParents().add(parent);
     child.getParents().add(parent2);
     child = childRepository.saveAndFlush(child);
     System.out.println(child);



     Child child2 = new Child();
     child2.setLogin("olga");
     child2.setFirstName("Ольга");
     child2.setLastName("Иванова");
     child2.setRole(ROLE.ROLE_CHILD);
     child2.setAge(12);
     child2.getParents().add(parent);
     child2.getParents().add(parent2);
     child2.setEducationalInstitution(ed);
     child2.setPassword(passwordEncoder.encode("123456"));
     child2=childRepository.saveAndFlush(child2);

     System.out.println(child2);





    }
}
