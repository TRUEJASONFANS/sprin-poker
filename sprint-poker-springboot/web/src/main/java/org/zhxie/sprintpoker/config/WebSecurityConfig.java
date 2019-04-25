package org.zhxie.sprintpoker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.stereotype.Component;
import org.zhxie.sprintpoker.service.CustomUserDetailsService;
import org.zhxie.sprintpoker.service.LoginSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianyang on 1/7/2019.
 */

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(SecuritySettings.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  private SecuritySettings settings;

  @Autowired @Qualifier("dataSource")
  private DataSource dataSource;

  @Autowired
  private AuthFailureHandler authFailureHandler;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    /**
     *  / , /login are allowed to directly visit
     * others routes visit need be authenticated
     *
     */

    //include /login?msg=Bad Credentials
    http.authorizeRequests()
            .antMatchers("/images/**", "/checkcode", "/scripts/**", "/styles/**", "/login*").permitAll()
            .anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll().successHandler
            (loginSuccessHandler()).failureHandler
            (authFailureHandler);

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

    http.logout().logoutSuccessUrl(settings.getLogoutsuccssurl())
            .and().rememberMe().userDetailsService(customUserDetailsService)
            .tokenValiditySeconds(86400).tokenRepository(tokenRepository());

    // enable the cookie transfer from different port
    http.csrf().disable();
  }

  private JdbcTokenRepositoryImpl tokenRepository() {
    JdbcTokenRepositoryImpl jtr = new JdbcTokenRepositoryImpl();
    jtr.setDataSource(dataSource);
    return jtr;
  }

  @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/user/regist");
    }

    @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(customUserDetailsService).passwordEncoder(bcryptPasswordEncoder());
    //remember me
    auth.eraseCredentials(false);
  }

  @Bean
  public BCryptPasswordEncoder bcryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public LoginSuccessHandler loginSuccessHandler(){
    return new LoginSuccessHandler();
  }

  @Bean
  public CustomSecurityMetadataSource securityMetadataSource() {
    return new CustomSecurityMetadataSource(settings.getUrlroles());
  }

  @Bean
  public CustomFilterSecurityInterceptor customFilter() throws Exception{
    CustomFilterSecurityInterceptor customFilter = new CustomFilterSecurityInterceptor();
    customFilter.setSecurityMetadataSource(securityMetadataSource());
    customFilter.setAccessDecisionManager(accessDecisionManager());
    return customFilter;
  }

  @Bean
  public AccessDecisionManager accessDecisionManager() {
    return new CustomAccessDecisionManager();
  }

  @Component
  public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private ObjectMapper objectMapper = new ObjectMapper();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {
      System.out.println("AuthFailureHandler.onAuthenticationFailure()");
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      Map<String, Object> data = new HashMap<>();
      data.put(
              "timestamp",
              Calendar.getInstance().getTime());
      data.put(
              "exception",
              exception.getMessage());

      response.getOutputStream()
              .println(objectMapper.writeValueAsString(data));
      //redirectStrategy.sendRedirect(request, response, "/login?msg=Bad Credentials");
    }
  }

}