package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@Rollback(false)
@SpringBootTest
class MemberRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberJpaRepository memberRepository;

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA"); Team teamB = new Team("teamB"); em.persist(teamA); em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1); em.persist(member2); em.persist(member3); em.persist(member4);
        //초기화 em.flush(); em.clear();
//확인
        List<Member> members = em.createQuery("select m from Member m", Member.class)
                .getResultList();
        for (Member member : members) {
            System.out.println("member=" + member); System.out.println("-> member.team=" + member.getTeam());
        } }

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member); //JPA 엔티티 동일성 보 }

    }

    @Test
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
    public void paging(){
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));
        memberRepository.save(new Member("member6",10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberRepository.findByPage(age, offset, limit);
        Long totalCount = memberRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(6);
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

    }