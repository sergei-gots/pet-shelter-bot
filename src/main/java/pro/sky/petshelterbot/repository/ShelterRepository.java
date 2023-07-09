package pro.sky.petshelterbot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.petshelterbot.entity.Button;
import pro.sky.petshelterbot.entity.Shelter;

import java.util.Collection;

public interface ShelterRepository extends JpaRepository<Shelter,Long> {}
