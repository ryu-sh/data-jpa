package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    @Query("select m from Member m where m.username = :username and m.age = :age")
    List<Member> findUser(@Param("username")String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id, m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username in :names")
    List<Member> findByName(@Param("names") List<String> names);

    List<Member> findListByUsername(String username); //컬렉션 (find... 에 ...은 아무거나 들어가도 상관 없음)
    Member findMemberByUsername(String username); //단건
    Optional<Member> findOptionalByUsername(String username); //단건

    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m.username) from Member m")
    //@Query(value = "select m from Member m left join m.team t") 이렇게 쿼리 날릴경우 count쿼리도 join을 그대로 하므로 성능이 안좋아짐 그래서 count 쿼리 분리 필요 할 수도 있음
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true) //이 어노테이션을 넣어야 .executeUpdate(); 가 실행됨 을시 getResultList()가 실행됨
    @Query("update Member m set m.age = m.age+1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override //interface를 override 하는 것
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL에 EntityGraph 함께 쓸 수도 있다.
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);
}
