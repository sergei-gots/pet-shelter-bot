package pro.sky.petshelterbot.util;

import com.github.javafaker.Faker;

import pro.sky.petshelterbot.entity.*;

import java.time.LocalDate;

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

    public static Adopter generateAdopter(Shelter shelter) {
        Adopter adopter = new Adopter(
                faker.random().nextLong(),
                faker.harryPotter().character()
        );
        adopter.setShelter(shelter);
        return adopter;
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

    public static Report generateReport(Pet pet) {
        return new Report(faker.random().nextLong(),
                pet,
                LocalDate.of(faker.random().nextInt(2023,2023),
                        faker.random().nextInt(1,12),
                        faker.random().nextInt(1,28)),
                faker.lorem().sentence(),
                faker.lorem().sentence(),
                faker.lorem().sentence(),
                faker.company().url(),
                faker.random().nextBoolean(),
                faker.random().nextBoolean());
    }

    public static Report generateReport() {
      return generateReport(generatePet());
    }


}
