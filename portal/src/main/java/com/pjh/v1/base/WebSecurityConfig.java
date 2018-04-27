package com.pjh.v1.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.headers().frameOptions().disable(); // 解决spring boot不允许加载frame问题
        // 设置可以iframe访问
        http.headers().frameOptions().sameOrigin();
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()//定义不需要登录权限的页面
                .antMatchers(new String[]{"/js/**","/css/**","/img/**","/images/**","/fonts/**","/**/favicon.ico"}).permitAll()
                // 任何尚未匹配的URL只需要对用户进行身份验证
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")// 需要登录时指向的页面
                .defaultSuccessUrl("/admin")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication().passwordEncoder(NoOpPasswordEncoder.getInstance())
                .withUser("user").password("password").roles("USER");
    }
}
