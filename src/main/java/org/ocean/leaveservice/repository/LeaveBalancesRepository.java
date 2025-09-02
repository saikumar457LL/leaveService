package org.ocean.leaveservice.repository;

import org.ocean.leaveservice.entity.LeaveBalances;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveBalancesRepository extends JpaRepository<LeaveBalances, Integer> {

    List<LeaveBalances> findAllByUser(Integer user_id);
}
