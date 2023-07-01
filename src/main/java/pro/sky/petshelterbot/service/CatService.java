package pro.sky.petshelterbot.service;

import org.springframework.stereotype.Service;
import pro.sky.petshelterbot.entity.Cat;
import pro.sky.petshelterbot.repository.CatRepository;

@Service
public class CatService {
    final private CatRepository catRepository;

    public CatService(CatRepository catRepository) {
        this.catRepository = catRepository;
    }

    public Cat addTestCatToDb() {
        Cat cat = new Cat(0, "Муська", false);
        return catRepository.save(cat);
    }


}
