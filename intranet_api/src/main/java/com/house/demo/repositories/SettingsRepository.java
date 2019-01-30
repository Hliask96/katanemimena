package com.house.demo.repositories;

import com.house.demo.classes.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingsRepository extends JpaRepository<Settings, Long> {

    @Query(value="SELECT * FROM settings ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Settings getSettings();
}