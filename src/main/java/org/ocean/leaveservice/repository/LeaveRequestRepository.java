package org.ocean.leaveservice.repository;

import org.ocean.leaveservice.constants.LeaveStatus;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findAllByUserAndStatus(UUID user, LeaveStatus status);
    Optional<LeaveRequest> findByApproverAndUuid(UUID approver, UUID leaveId);
    List<LeaveRequest> findAllByApprover(UUID approver);
    List<LeaveRequest> findAllByUser(UUID user);
    Optional<LeaveRequest> findByUserAndUuid(UUID user, UUID uuid);
}
