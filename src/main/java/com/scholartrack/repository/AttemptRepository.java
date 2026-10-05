package com.scholartrack.repository;

import com.scholartrack.model.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    List<Attempt> findAllByUserIdOrderByAttemptedAtDesc(Long userId);

    void deleteAllByUserId(Long userId);
}
