package org.ddmed.pump.config;

import org.ddmed.pump.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // zk specific -- maybe disable CSRF set by default via auto-configuration? --
        http.csrf().disable() // can be disabled safely; ZK unique desktop ID generation prevents Cross-Site Request Forgery attacks

                // application specific
                .authorizeRequests()
                .antMatchers("/zkau/web/**/js/**","/zkau/web/**/zul/css/**","/zkau/web/**/img/**").permitAll()

                .mvcMatchers("/login","/logout").permitAll()
                .mvcMatchers("/*").hasRole("ADMIN")
                .antMatchers("/zkau/web/**/**.zul").denyAll() // calling a zul-page directly is not allowed -- should we put this in the auto-configuration to? --

                .and()
                .formLogin().loginPage("/login").defaultSuccessUrl("/")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/");
    }

}