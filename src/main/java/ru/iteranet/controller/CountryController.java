package ru.iteranet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.NameNotFoundException;
import ru.iteranet.exceptions.RecordAlreadyExistsException;
import ru.iteranet.exceptions.RecordNotFoundException;
import ru.iteranet.repo.CountryRepository;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class CountryController {

    @Autowired
    private CountryRepository repository;

    @GetMapping("/country")
    public List<Country> findAll(@RequestParam(required = false) String name) {
        System.out.println(name);
        if (name == null){
            return repository.findAll();
        } else {
            List<Country> loadedCountries = repository.findByName(name);
            return loadedCountries;
        }
    }

    @GetMapping("/country/{id}")
    public Country findOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    @PostMapping("/country")
    public Country create(@RequestBody Country country) {
        String name = country.getName();
        if (name == null || name == "") {
            throw new NameNotFoundException();
        }
        List<Country> existingCountries = repository.findByName(name);
        if (existingCountries.size() != 0) {
            throw new RecordAlreadyExistsException(name);
        }
        return repository.save(country);
    }

    // Save or update
    @PutMapping("/country/{id}")
    public Country saveOrUpdate(@RequestBody Country country, @PathVariable Long id) {
        Country existingCountry = repository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        if (existingCountry.getName() != country.getName()) {
            existingCountry.setName(country.getName());
            repository.save(existingCountry);
        }
        return existingCountry;
    }

    @DeleteMapping("/country/{id}")
    public void delete(@PathVariable Long id) {
        Country existingCountry = repository
                .findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        repository.deleteById(id);
    }
}
