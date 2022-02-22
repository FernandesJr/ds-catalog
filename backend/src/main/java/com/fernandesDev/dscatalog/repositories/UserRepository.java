package com.fernandesDev.dscatalog.repositories;

import com.fernandesDev.dscatalog.entities.Role;
import com.fernandesDev.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
