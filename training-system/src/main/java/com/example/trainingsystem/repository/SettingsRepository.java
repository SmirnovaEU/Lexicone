package com.example.trainingsystem.repository;

import com.example.trainingsystem.model.Settings;
import com.example.trainingsystem.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<Settings, Long> {
    Settings findFirstByUser(User user);
}
