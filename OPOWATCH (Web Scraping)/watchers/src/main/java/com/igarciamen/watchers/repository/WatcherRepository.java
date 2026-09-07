package com.igarciamen.watchers.repository;

import com.igarciamen.watchers.model.Watcher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatcherRepository extends JpaRepository<Watcher, Long> {

    List<Watcher> findByActiveTrue();
}