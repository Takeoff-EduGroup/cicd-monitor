package com.devopsboard.cicd_monitor.repository;

import com.devopsboard.cicd_monitor.entity.User;
import com.devopsboard.cicd_monitor.enumerated.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User,Long>
{
    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);


}
