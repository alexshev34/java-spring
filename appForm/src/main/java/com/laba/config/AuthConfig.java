package com.laba.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * настройка безопасности через java config
 */

@Configuration
@EnableWebSecurity
public class AuthConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    UserService userService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers( "/login**", "/logout**","h2-console/**",
                        "/bootstrap/**","/jquery/**","/popper/**" ).permitAll()

                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/parent/**").hasRole("PARENT")
                .antMatchers("/child/**").hasRole("CHILD")
                //Доступ разрешен всем пользователей
                .antMatchers("/**").permitAll()
                //Все остальные страницы требуют аутентификации
                .anyRequest().authenticated()
                .and()
                //Настройка для входа в систему
                .authorizeRequests().and().formLogin()//
                .loginProcessingUrl("/login") // Submit URL
                .loginPage("/login")
                .successHandler(myAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")//
                .usernameParameter("username")//
                .passwordParameter("password")
                .and()
                .exceptionHandling().accessDeniedPage("/403")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/")        ;
        httpSecurity.headers().frameOptions().disable();


    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
    }
}
