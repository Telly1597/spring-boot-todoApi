package com.mohamed.todo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // WHERE title = ?
    List<Task> findByTitle(String title);

    @Query("""
        SELECT t FROM Task t
        WHERE t.startTime < :rangeEnd
          AND t.endTime   > :rangeStart
        ORDER BY t.startTime
        """)
    List<Task> findOverlapping(@Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd);

    // NEW: Used exclusively for validating time-slot collisions on create/update
    @Query("""
        SELECT t FROM Task t
        WHERE (:excludeId IS NULL OR t.id <> :excludeId)
          AND t.startTime < :newEnd
          AND t.endTime   > :newStart
        """)
    List<Task> findConflicts(@Param("excludeId") Long excludeId,
                             @Param("newStart") LocalDateTime newStart,
                             @Param("newEnd") LocalDateTime newEnd);
}
