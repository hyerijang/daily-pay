package com.hyerijang.dailypay.member.contoller;

import com.hyerijang.dailypay.member.MemberRepository;
import com.hyerijang.dailypay.member.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberRepository memberRepository;

    @PostMapping
    public ResponseEntity<RegisterRequest> save(@RequestBody RegisterRequest registerRequest) {
        memberRepository.save(registerRequest.toEntity());
        return ResponseEntity.noContent().build();
    }

}
