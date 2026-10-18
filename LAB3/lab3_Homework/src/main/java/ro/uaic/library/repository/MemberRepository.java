package ro.uaic.library.repository;

import org.springframework.stereotype.Repository;
import ro.uaic.library.domain.Member;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class MemberRepository {
    private final ConcurrentHashMap<Long, Member> members = new ConcurrentHashMap<>();
    public Member save(Member member) { members.put(member.id(), member); return member; }
    public Optional<Member> findById(long id) { return Optional.ofNullable(members.get(id)); }
    public List<Member> findAll() { return List.copyOf(members.values()); }
}
