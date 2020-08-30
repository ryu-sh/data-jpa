package study.datajpa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

@RestController
public class TestController {

    @Autowired
    MemberRepository memberRepository;

    @GetMapping("/test")
    public Long test(){
        Member member = new Member("test");
        Member member1 = new Member("test");

        memberRepository.save(member);
        memberRepository.save(member1);
        System.out.println(member.getId());
        System.out.println(member1.getId());
        return member.getId();
    }

}
