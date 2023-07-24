package pro.sky.petshelterbot.util;

import com.github.javafaker.Faker;

import pro.sky.petshelterbot.entity.Shelter;

public class DataGenerator {

    private final static Faker faker = new Faker();

    public static Shelter generateShelter() {
        return new Shelter(
                1L,
                faker.harryPotter().house(),
                faker.lorem().sentence(),
                faker.address().streetAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.company().url(),
                faker.animal().name());
    }
}
