package pro.sky.petshelterbot.util;

import com.github.javafaker.Faker;

import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;

public class DataGenerator {

    private final static Faker faker = new Faker();

    public static Shelter generateShelter() {
        return new Shelter(
                faker.random().nextLong(),
                faker.harryPotter().house(),
                faker.lorem().sentence(),
                faker.address().streetAddress(),
                faker.phoneNumber().phoneNumber(),
                faker.company().url(),
                faker.animal().name());
    }

    public static int generateCount() {
        return  faker.random().nextInt(0, 10);
    }

    public static Adopter generateAdopter() {
        return new Adopter(
                faker.random().nextLong(),
                faker.harryPotter().character()
        );

    }

    public static Pet generatePet (Shelter shelter) {
        return new Pet(
                faker.random().nextLong(),
                faker.lorem().characters(),
                faker.cat().name(),
                shelter,
                faker.random().nextBoolean()
        );
    }
}
