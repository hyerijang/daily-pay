package com.hyerijang.dailypay.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * 익명 사용자인 경우에는 null로, 익명 사용자가 아닌 경우에는 실제 user 객체로
 *
 * @see : com.hyerijang.dailypay.user.domain.User
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : user")
public @interface CurrentUser {

}
