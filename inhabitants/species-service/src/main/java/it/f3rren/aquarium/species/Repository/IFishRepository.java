package it.f3rren.aquarium.species.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.f3rren.aquarium.species.model.Fish;

public interface IFishRepository extends JpaRepository<Fish, Integer> {
    
    @Query("SELECT f FROM Fish f ORDER BY f.commonName ASC")
    public List<Fish> findAllSortedByName();
}
