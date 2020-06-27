package com.mindtree.shopping.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mindtree.shopping.model.User;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer>{

}
