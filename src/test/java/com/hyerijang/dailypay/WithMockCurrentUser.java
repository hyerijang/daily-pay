package com.hyerijang.dailypay;

import com.hyerijang.dailypay.auth.dto.CustomUserDetails;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;

/**
 * {@link WithMockUser} 대신 사용하는 어노테이션 <br/> {@link CustomUserDetails}을 사용하여 user객체를 context에 저장하므로,
 * 테스트 시 {@link WithMockUser} 사용 불가
 */
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCurrentUser {

    String email() default "dailypay@gmail.com";

    String password() default "password";

    long id() default 12345;
}
