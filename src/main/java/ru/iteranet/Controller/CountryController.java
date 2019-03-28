package ru.iteranet.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.iteranet.Entity.Country;
import ru.iteranet.Exceptions.NotFoundException;
import ru.iteranet.Repo.CountryRepository;

import java.util.List;

@RestController
public class CountryController {

    @Autowired
    private CountryRepository repository;

    @GetMapping("/country")
    public List<Country> findAll() {
        return repository.findAll();
    }

    @GetMapping("/country/{id}")
    public Country findOne(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @PostMapping("/country")
    public Country create(@RequestBody Country country) {
        return repository.save(country);
    }

    // Save or update
    @PutMapping("/country/{id}")
    public Country saveOrUpdate(@RequestBody Country country, @PathVariable Long id) {
        Country loadedCountry = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
        if (loadedCountry.getName() != country.getName()) {
            loadedCountry.setName(country.getName());
            repository.save(loadedCountry);
        }
        return loadedCountry;

    }

    @DeleteMapping("/country/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
