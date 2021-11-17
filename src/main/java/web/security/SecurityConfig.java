package web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import web.service.UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private UserService userService;
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setLoginSuccessHandler(LoginSuccessHandler loginSuccessHandler) {
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
//                .loginPage("/login") //указываем страницу с формой логина
                .successHandler(loginSuccessHandler) //логика обработки при вводе логина
//                .loginProcessingUrl("/login") //указываем action с формы логина
//                .usernameParameter("j_username")
//                .passwordParameter("j_password")
                .permitAll(); //доступ к форме логина всем
        http.logout()
                .permitAll() //разрешаем делать логаут всем
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //указываем URL логаута
                .logoutSuccessUrl("/login") // указывам URL при удачном логауте
                .and().csrf().disable();
        http
                .authorizeRequests() //делаем страницу регистрации недоступной для авторизованных пользователей
                .antMatchers("/login").anonymous() //страница аутенфикации доступна всем
                .antMatchers("users", "/users/**/edit/", "/admin", "/admin/**") //защищенные URL
                .access("hasAnyRole('ADMIN')")
                .anyRequest()
                .authenticated();
    }
}
