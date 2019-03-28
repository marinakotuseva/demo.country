package ru.iteranet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteranet.Entity.Country;
import ru.iteranet.Exceptions.NameNotFoundException;
import ru.iteranet.Exceptions.RecordAlreadyExistsException;
import ru.iteranet.Exceptions.RecordNotFoundException;
import ru.iteranet.Repo.CountryRepository;

import java.util.List;

@RestController
public class CountryController {

    @Autowired
    private CountryRepository repository;

    @GetMapping("/api/country")
    public List<Country> findAll() {
        return repository.findAll();
    }

    @GetMapping("/api/country/{id}")
    public Country findOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    @PostMapping("/api/country")
    public Country create(@RequestBody Country country) {
        String name = country.getName();
        if (name == null) {
            throw new NameNotFoundException();
        }
        List<Country> existingCountries = repository.findByName(name);
        if (existingCountries.size() != 0) {
            throw new RecordAlreadyExistsException(name);
        }
        return repository.save(country);
    }

    // Save or update
    @PutMapping("/api/country/{id}")
    public Country saveOrUpdate(@RequestBody Country country, @PathVariable Long id) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        if (existingCountry.getName() != country.getName()) {
            existingCountry.setName(country.getName());
            repository.save(existingCountry);
        }
        return existingCountry;
    }

    @DeleteMapping("/api/country/{id}")
    public void delete(@PathVariable Long id) {
        Country existingCountry = repository
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        repository.deleteById(id);
    }
}
