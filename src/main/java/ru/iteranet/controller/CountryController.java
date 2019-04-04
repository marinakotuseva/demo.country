package ru.iteranet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteranet.entity.Country;
import ru.iteranet.exceptions.IncorrectNameException;
import ru.iteranet.exceptions.RecordAlreadyExistsException;
import ru.iteranet.exceptions.RecordNotFoundException;
import ru.iteranet.repo.CountryRepository;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class CountryController {

    private CountryRepository countryRepository;

    @Autowired
    public CountryController(CountryRepository repository) {
        this.countryRepository = repository;
    }

    // Read all
    @GetMapping("/country")
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    // Find by name
    @GetMapping(value = "/country", params = "name")
    public Country findByName(@RequestParam String name) {
        if (name == null){
            throw new IncorrectNameException();
        }
        return countryRepository.findByName(name);
    }

    // Fund by id
    @GetMapping("/country/{id}")
    public Country findByID(@PathVariable Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
    }

    // Create new
    @PostMapping("/country")
    public Country create(@RequestBody Country country) {
        String name = country.getName();
        if (name == null || name == "") {
            throw new IncorrectNameException();
        }
        Country existingCountry = countryRepository.findByName(name);
        if (existingCountry != null) {
            throw new RecordAlreadyExistsException(name);
        }
        return countryRepository.save(country);
    }

    // Update
    @PostMapping("/country/{id}")
    public Country update(@RequestBody Country country, @PathVariable Long id) {
        Country existingCountry = countryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));
        existingCountry.setName(country.getName());
        countryRepository.save(existingCountry);
        return existingCountry;
    }

    // Delete
    @DeleteMapping("/country/{id}")
    public void delete(@PathVariable Long id) {
        countryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException(id));

        countryRepository.deleteById(id);
    }
}
