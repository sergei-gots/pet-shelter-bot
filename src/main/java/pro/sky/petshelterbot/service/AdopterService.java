package pro.sky.petshelterbot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.repository.AdopterRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdopterService {

    private final AdopterRepository adopterRepository;

    public AdopterService(AdopterRepository adopterRepository) {
        this.adopterRepository = adopterRepository;
    }

    public List<Adopter> getAllReadyToAdopt(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Adopter> adopters = adopterRepository.getAllReadyToAdopt(pageable);
        if (adopters.hasContent()) {
            return adopters.getContent();
        } else {
            return new ArrayList<Adopter>();
        }
    }
}
