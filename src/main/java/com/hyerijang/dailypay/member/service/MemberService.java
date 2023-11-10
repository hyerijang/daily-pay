package com.hyerijang.dailypay.member.service;

import com.hyerijang.dailypay.member.MemberRepository;
import com.hyerijang.dailypay.member.dto.RegisterRequest;
import com.hyerijang.dailypay.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public Long register(RegisterRequest registerRequest) {
        Member member = memberRepository.save(registerRequest.toEntity());
        return member.getId();
    }


}
