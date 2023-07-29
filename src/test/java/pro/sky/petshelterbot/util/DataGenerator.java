package pro.sky.petshelterbot.util;

import com.github.javafaker.Faker;

import pro.sky.petshelterbot.entity.Adopter;
import pro.sky.petshelterbot.entity.Pet;
import pro.sky.petshelterbot.entity.Shelter;
import pro.sky.petshelterbot.entity.Volunteer;

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

    /** @return random int value in range between 4 to 20 inclusive */
    public static int generateCount() {
        return  faker.random().nextInt(4, 20);
    }

    public static Adopter generateAdopter() {
        return new Adopter(
                faker.random().nextLong(),
                faker.harryPotter().character()
        );

    }

    public static Pet generatePet() {
        return generatePet(generateShelter());
    }

    public static Pet generatePet (Shelter shelter) {
        return new Pet(
                faker.random().nextLong(),
                faker.lorem().characters(),
                faker.cat().name(),
                shelter,
                faker.random().nextBoolean(),
                faker.company().url()
        );
    }

    public static Volunteer generateVolunteer(Shelter shelter) {
        return new Volunteer(
                faker.random().nextLong(),
                faker.harryPotter().character(),
                shelter
        );
    }

    public static Volunteer generateVolunteer() {
        return generateVolunteer(generateShelter());
    }
}
