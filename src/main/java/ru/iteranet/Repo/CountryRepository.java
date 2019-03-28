package ru.iteranet.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iteranet.Entity.Country;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Long> {

    public List<Country> findByName(String name);
}

