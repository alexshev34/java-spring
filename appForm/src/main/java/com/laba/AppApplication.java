package com.laba;

import com.laba.entity.*;
import com.laba.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@SpringBootApplication
public class AppApplication implements CommandLineRunner {

    static final Scanner scanner = new Scanner(System.in);
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    EducationalInstitutionRepository educationalInstitutionRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    ParentRepository parentRepository;

    @Autowired
    ChildRepository childRepository;

    @Autowired
    UsersRepository usersRepository;


    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {

        usersRepository.findAll().forEach(u->{
            System.out.println(u.getId()+" "+u.getLogin()+" "+u.getRole());


        });
        // menu();
    }

    /**
     * возращает введенное число или 0 в случае не корректного ввода
     * @return число
     */
    public int choose(){
        int ch = 0;
        while (ch==0){
            System.out.print("Ваш выбор ");
            String temp = scanner.nextLine();
            try{
                ch = Integer.parseInt(temp);
            }catch (Exception e){
                ch=0;
            }
        }

        return ch;
    }

    /**
     * ввод адреса с клавиатуры, будем вводить пока не введем корректный
     * @return адрес с таблицы
     */
    public Address inputAddress(){
        Address address = null;
        while (address==null){
            System.out.print("Введите название улицы: ");
            String street = scanner.nextLine();
            System.out.print("Введите номер дома: ");
            String house = scanner.nextLine();
            address = addressRepository.findByStreetAndHouse(street,house);
            if (address==null) System.out.println("Адрес не найден, повторите ввод!");
        }
        return address;
    }
    public void printInfo(){
        List<Region> regions = regionRepository.findAll();
        System.out.println("******** Справочна информация  *************");
        regions.forEach(r->{
            System.out.println("Район " + r.getName());
            List<Address> addresses = r.getAddresses().stream().collect(toList());
            Map<String, List<Address>> map = addresses.stream().collect(groupingBy(Address::getStreet));
            map.forEach((k,v)->{
                String houses = v.stream().map(s->s.getHouse()).sorted().collect(Collectors.joining(","));
                System.out.printf(" Улица %s, дома: %s\n",k,houses);
            });
            educationalInstitutionRepository.searchByAddress(addresses.get(0)).forEach(e->{
                System.out.println(e);
            });
            System.out.println("--------------------------------------------------------");
        });
    }

    /**
     * основное меню
     */
    public void menu(){
        System.out.println("1) Вывести справочник");
        System.out.println("2) Меню Родители ");
        System.out.println("3) Меню Дети ");
        System.out.println("4) Завершить работу");
        int ch = choose();
        switch (ch){

            case 2:menuParent(); break;
            case 3:menuChild(); break;
            case 1:printInfo();break;
            case 4:System.exit(1); break;
        }
        menu();
    }

    /**
     * меню родители
     */
    public void menuParent(){
        System.out.println("1) Вывести родителей");
        System.out.println("2) Добавить родителя");
        System.out.println("3) Сменить адрес ");
        System.out.println("4) Вернуться в главное меню");
        int ch = choose();
        switch (ch){
            case 1:showParent(); break;
            case 2:addParent();break;
            case 3:changeAddress();break;
            case 4:menu(); break;
        }
        menuParent();
    }

    /**
     * меню дети
     */
    public void menuChild(){
        System.out.println("1) Вывести детей");
        System.out.println("2) Добавить ребенка");
        System.out.println("3) Сменить учебное заведение ");
        System.out.println("4) Вернуться в главное меню");
        int ch = choose();
        switch (ch){
            case 1:showChildren(); break;
            case 2:addChild();break;
            case 3:changeEducation();break;
            case 4:menu(); break;
        }
        menuChild();
    }


    /**
     * добавление ребенка
     */
    private void addChild() {
        if (parentRepository.countAll()==0){
            System.out.println("Сначала нужно заполнить родителей");
            return;
        }
        System.out.print("Введите имя и фамилию ребенка: ");
        String firstName = scanner.next();
        String lastName = scanner.nextLine();
        if (firstName.isEmpty() || lastName.isEmpty()){
            System.out.println("Не корректный ввод!");
            return;
        }
        int age= 0;
        while (age==0){
            System.out.print("Введите возраст ребенка ");
            try {
                age =Integer.parseInt(scanner.nextLine());
            }catch (Exception e){
                age=0;
            }
        }

        List<Parent> parents =new ArrayList<>();
        List<EducationalInstitution> educations = new ArrayList<>();
        Parent parent = null;
        // перый родитель обязателен
        while (parent==null){
            parent = findParent();
            parents.add(parent);
            List<EducationalInstitution> e = educationalInstitutionRepository.searchByAddress(parent.getAddress());
            if (!e.isEmpty()) educations.addAll(e);
        }

        System.out.print("Добавить ещё одного родителя? ");
        String choose = scanner.nextLine();
        if (!choose.isEmpty() && (choose.equalsIgnoreCase("да") || choose.equalsIgnoreCase("yes"))){
            parent = null;
            parent = findParent();
            // если нашло
            if (parent!=null){
                parents.add(parent);
                List<EducationalInstitution> e = educationalInstitutionRepository.searchByAddress(parent.getAddress());
                if (!e.isEmpty()) educations.addAll(e);
            }
        }
        // если список заведений с одного заведения - то его и выберем автоматом
        EducationalInstitution e = educations.size()==1?educations.get(0):inputEducation(educations);
        Child child = new Child(firstName,lastName,age);
        child.setEducationalInstitution(e);
        child.getParents().addAll(parents);
        child = childRepository.save(child);
        System.out.println(child);


    }

    /**
     * повзоляет выбрать учебное заведения из списка, выполняется пока не введем корректно
     * @param list список
     * @return учебное заведение
     */
    public EducationalInstitution inputEducation(List<EducationalInstitution> list){
        EducationalInstitution e =null;
        while (e==null){
            System.out.println("Выбирете учебное заведение: ");
            list.forEach(System.out::println);
            long id = choose();
            e = list.stream().filter(i->i.getId().equals(id)).findFirst().orElse(null);
        }
        return e;

    }

    /**
     * выводит список детей на экран
     */
    private void showChildren() {
        List<Child> children = childRepository.findAll();
        if (children.isEmpty()){
            System.out.println("Пусто!");
            return;
        }
        children.forEach(c->{

            System.out.printf(" Ребенок %s %s, возраст %d, id %d, учится %s\n",c.getLastName(),
                    c.getFirstName(),c.getAge(),c.getId(),c.getEducationalInstitution());
            c.getParents().forEach(p->{
                System.out.printf(" Родитель %s %s,  id %d,  %s\n",p.getLastName(),
                        p.getFirstName(),p.getId(),p.getAddress());
            });
        });
    }

    /**
     * меню добавления родителя
     */
    private void addParent() {
        System.out.print("Введите имя и фамилию родителя: ");
        String firstName = scanner.next();
        String lastName = scanner.nextLine();
        if (firstName.isEmpty() || lastName.isEmpty()){
            System.out.println("Не корректный ввод!");
            return;
        }
        Address address = inputAddress();
        Parent parent = new Parent(firstName,lastName,address);
        parent = parentRepository.save(parent);
        System.out.println(parent);
    }

    /**
     * выводит на экран список родителей
     */
    public void showParent(){
        List<Parent> parents = parentRepository.findAll();
        if (parents.isEmpty()){
            System.out.println("Пусто!");
            return;
        }
        parents.forEach(p->{
            System.out.printf("Родитель %s %s, id %d, %s\n",p.getLastName(),p.getFirstName(),p.getId(),p.getAddress());
            if (p.getChildren().isEmpty()) System.out.println("Детей нет");
            else p.getChildren().forEach(c->{
                System.out.printf(" Ребенок %s %s, возраст %d, id %d, учится %s\n",c.getLastName(),
                        c.getFirstName(),c.getAge(),c.getId(),c.getEducationalInstitution());
            });
        });
    }

    /**
     * поиск родителя по id
     * @return найденый родитель или null при некоректном вводе или если не найдено
     */
    public Parent findParent(){
        System.out.print("Введите id родителя ");
        long id=0;
        try {
            id = Long.parseLong(scanner.nextLine());
        }catch (Exception e){
            System.out.println("Не корректный ввод! ");
            return null;
        }
        Optional<Parent> p = parentRepository.findById(id);
        if (!p.isPresent()){
            System.out.println("Родитель не найден! ");
            return null;
        }
        return p.get();
    }

    /**
     * поиск ребенка по id
     * @return найденый ребенок или null при неккоректном вводе или если не найден
     */
    public Child findChild(){
        System.out.print("Введите id ребенка ");
        long id=0;
        try {
            id = Long.parseLong(scanner.nextLine());
        }catch (Exception e){
            System.out.println("Не корректный ввод! ");
            return null;
        }
        Optional<Child> p = childRepository.findById(id);
        if (!p.isPresent()){
            System.out.println("Ребенок не найден! ");
            return null;
        }
        return p.get();
    }

    /**
     * смена адреса у родителя
     */
    public void changeAddress(){

        Parent parent = findParent();
        if (parent==null) return;
        Address address = inputAddress();
        parent.setAddress(address);
        parent = parentRepository.save(parent);
        System.out.println(parent);
    }

    /**
     * выбору учебного заведения из доступных
     */
    public void changeEducation(){
        Child child = findChild();
        if (child==null) return;
        final List<EducationalInstitution> educations = new ArrayList<>();
        child.getParents().forEach(p->{
            List<EducationalInstitution> e = educationalInstitutionRepository.searchByAddress(p.getAddress());
            if (!e.isEmpty()) educations.addAll(e);
        });
        EducationalInstitution education = educations.size()==1? educations.get(0): inputEducation(educations);
        child.setEducationalInstitution(education);
        child = childRepository.save(child);
        System.out.println(child);

    }



}
