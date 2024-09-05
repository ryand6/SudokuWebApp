package com.github.ryand6.sudokuweb.repositories;

import com.github.ryand6.sudokuweb.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
