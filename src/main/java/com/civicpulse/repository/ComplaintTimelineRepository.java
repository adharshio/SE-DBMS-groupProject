package com.civicpulse.repository;

import com.civicpulse.model.ComplaintTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComplaintTimelineRepository extends JpaRepository<ComplaintTimeline, Long> {
    List<ComplaintTimeline> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
}
