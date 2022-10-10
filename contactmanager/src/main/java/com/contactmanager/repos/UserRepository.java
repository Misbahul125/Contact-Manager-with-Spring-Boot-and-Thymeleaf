package com.contactmanager.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contactmanager.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}
