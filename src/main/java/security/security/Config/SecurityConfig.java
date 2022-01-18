package security.security.Config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import security.security.Service.Impl.MemberServiceImpl;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberServiceImpl memberService;
    private AuthFailureHandler AuthFailureHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
            // 페이지 권한 설정
            .antMatchers("/", "/login", "/css/**", "/lib/**", "/js/**", "/editor/**").permitAll()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .antMatchers("/admin/**").hasRole("ADMIN")
            .antMatchers("/user/**").hasRole("MEMBER")
            .anyRequest().permitAll()
            .and() // 로그인 설정
            .formLogin()
            .loginPage("/login")
    //                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin")
            .failureHandler(AuthFailureHandler)
            .usernameParameter("id") // 로그인 시 사용할 파라미터 이름정하기
    //                .successHandler(new MyLoginSuccessHandler())
            .permitAll()
            .and() // 로그아웃 설정
            .logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
            .logoutSuccessUrl("/user/logout/result")
            .invalidateHttpSession(true)
            .deleteCookies("remember-me")
            .and()
            // 403 예외처리 핸들링
            .exceptionHandling()
            .accessDeniedPage("/user/denied")
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

    }

    // 아래경로는 인증 무시한다
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**", "/lib/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // 대충 인증에 사용하는 객체를 memberService를 쓰겠고 비밀번호 암호화를 위해 passwordEncoder를 쓰겠다는 말
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }









}

