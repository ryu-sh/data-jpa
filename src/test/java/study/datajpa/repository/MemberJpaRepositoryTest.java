package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberJpaRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    TeamRepository teamRepository;

    @Test
    @Transactional
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        findMember1.setUsername("updateTest");

//        List<Member> all = memberRepository.findAll();
//        assertThat(all.size()).isEqualTo(2);
//
//        long count = memberRepository.count();
//        assertThat(count).isEqualTo(2);
//
//        memberRepository.delete(member1);
//        memberRepository.delete(member2);
//
//        long deleteCount = memberRepository.count();
//
//        assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGraterThan(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA",15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findUser(){
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA",10);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(10);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void paging(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        //PageRequest 는 Pageable 인터페이스의 구현
        Page<Member> page = memberRepository.findByAge(age,pageRequest);

        List<Member> content = page.getContent();
        long totalCount = page.getTotalElements();

        //page.map(member -> new MemberDto()); //이렇게 dto로 변경 가능

        assertThat(content.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate(){
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member1", 19));
        memberRepository.save(new Member("member1", 20));
        memberRepository.save(new Member("member1", 21));
        memberRepository.save(new Member("member1", 40));

        int resultCount = memberRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy(){
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);

        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();

        // N+1 문제 발생
        for(Member member : members){
            System.out.println(member.getUsername());
            System.out.println(member.getTeam().getName());
        }

        em.flush();
        em.clear();

        List<Member> fetchMemers = memberRepository.findMemberFetchJoin();


    }

    @Test
    public void queryHint(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        em.flush(); //update 쿼리가 나가지 않음(readOnly라서)
    }

    @Test
    public void callCustom(){
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void JpaBaseEntity() throws InterruptedException {
        Member member = new Member("member1", 10);
        memberRepository.save(member);

        Thread.sleep(100);

        member.setUsername("member2");
        em.flush();
        em.clear();

        Member member1 = memberRepository.findById(member.getId()).get();

        System.out.println("============");
        System.out.println(member1.getCreateDateTime());
        System.out.println(member1.getUpdateDateTime());
        System.out.println("=============");
    }
}
