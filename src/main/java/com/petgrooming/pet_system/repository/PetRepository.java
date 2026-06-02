package com.petgrooming.pet_system.repository;

import com.petgrooming.pet_system.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findByOwnerUsername(String username);

    Optional<Pet> findByOwnerUsernameAndName(String username, String petName);
}
