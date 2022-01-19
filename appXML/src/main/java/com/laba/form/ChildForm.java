package com.laba.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ChildForm {
    @NotBlank(message = "Логин не может пустым")
    @Size(min = 3,max = 100)
    String login;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6,max = 100)
    String password;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 6,max = 100)
    String passwordConfirm;


    @NotBlank(message = "Имя не может быть пустым")
    String firstName;

    @NotBlank(message = "Фамилия не может быть пустой")
    String lastName;

    @NotNull(message = "Учебное заведение должно быть указано")
    long education;


    @NotNull(message = "Возраст должен быть указан!")
    @Min(value = 1,message = "Возраст от 1 года и больше...")
    int  age;
}
