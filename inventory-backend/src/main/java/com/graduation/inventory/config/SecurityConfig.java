package com.graduation.inventory.config;

import com.graduation.inventory.security.JwtTokenFilter;
import com.graduation.inventory.security.LogoutSuccessHandlerImpl;
import com.graduation.inventory.security.AccessDeniedHandlerImpl;
import com.graduation.inventory.security.AuthenticationEntryPointImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security安全配置类
 *
 * @author graduation
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 用户详情服务
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * JWT Token过滤器
     */
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    /**
     * 访问拒绝处理器
     */
    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    /**
     * 未认证处理入口
     */
    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    /**
     * 登出成功处理器
     */
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    /**
     * 配置密码编码器
     *
     * @return BCrypt密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器
     *
     * @return 认证管理器
     * @throws Exception 异常
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置认证管理器
     *
     * @param auth 认证管理器构建器
     * @throws Exception 异常
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 配置HTTP安全规则
     *
     * @param http HTTP安全配置
     * @throws Exception 异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF
                .csrf().disable()
                // 禁用Session，使用无状态模式
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 配置异常处理
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                // 配置请求授权
                .authorizeRequests()
                // 放行登录注册接口
                .antMatchers("/auth/login", "/auth/register").permitAll()
                // 放行验证码接口
                .antMatchers("/captcha").permitAll()
                // 放行Swagger相关路径
                .antMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v2/api-docs",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/doc.html"
                ).permitAll()
                // 放行Druid监控
                .antMatchers("/druid/**").permitAll()
                // 放行静态资源
                .antMatchers("/static/**", "/favicon.ico").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
                .and()
                // 配置登出
                .logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .and()
                // 添加JWT过滤器
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                // 禁用缓存
                .headers().cacheControl();
    }
}
