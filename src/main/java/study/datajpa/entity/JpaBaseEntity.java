package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@MappedSuperclass //속성만 상속받게 하는 어노테이션
@Getter
@Setter
public class JpaBaseEntity {

    @Column(updatable = false)
    private LocalDateTime createDateTime;
    private LocalDateTime updateDateTime;

    @PrePersist
    public void PrePersist(){
        LocalDateTime now = LocalDateTime.now();
        createDateTime = now;
        updateDateTime = now;
    }

    @PreUpdate
    public void preUpdate(){
        updateDateTime = LocalDateTime.now();
    }
}
