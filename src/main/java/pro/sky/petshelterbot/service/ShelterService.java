package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.repository.ShelterRepository;

import java.util.Collection;

@Service
public class ShelterService {
    final private ShelterRepository shelterRepository;

    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
    }

    public Collection<Shelter> findAll() {
        return shelterRepository.findAll();
    }

    public Shelter add(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    public Shelter delete(Long id) {
        Shelter shelter = get(id);
        shelterRepository.delete(shelter);
        return shelter;
    }

    public Shelter get(Long id) {
        return shelterRepository.getSheltersById(id);
    }

    public Shelter update(Long id, String name, String workTime, String address, String tel, String email, String type) {
        Shelter shelter = get(id);
        if (name != null) {
            shelter.setName(name);
        }
        if (workTime != null) {
            shelter.setWorkTime(workTime);
        }
        if (address != null) {
            shelter.setAddress(address);
        }
        if (tel != null) {
            shelter.setTel(tel);
        }
        if (email != null) {
            shelter.setEmail(email);
        }
        if (type != null) {
            shelter.setType(type);
        }
        return shelterRepository.save(shelter);
    }

}
