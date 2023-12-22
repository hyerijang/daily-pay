package com.hyerijang.dailypay;

import com.hyerijang.dailypay.auth.dto.UserAdapter;
import com.hyerijang.dailypay.user.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class WithMockCustomUserSecurityContextFactory implements
    WithSecurityContextFactory<WithMockCurrentUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCurrentUser annotation) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        // @CurrentUser 사용을 위해 UserAdapter를 context에 저장
        securityContext.setAuthentication(createAuthToken(annotation));
        return securityContext;
    }

    private  UsernamePasswordAuthenticationToken createAuthToken(WithMockCurrentUser annotation) {
        User user = createUser(annotation);
        UserDetails userDetails = new UserAdapter(user);
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
    }

    private  User createUser(WithMockCurrentUser annotation) {
        User user = User.builder()
            .email(annotation.email())
            .password(
                annotation.password()).build();
        ReflectionTestUtils.setField(user,"id", annotation.id());
        return user;
    }


}
