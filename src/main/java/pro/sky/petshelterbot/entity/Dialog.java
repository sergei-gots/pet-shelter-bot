package pro.sky.petshelterbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "adopter_volunteer_dialogs")
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "adopter_id", nullable = false)
    @OneToOne
    private Adopter adopter;
    @JoinColumn(name = "volunteer_id")
    @OneToOne
    private Volunteer volunteer;


    public Dialog() {
    }

    public Dialog(Long id, Adopter adopter, Volunteer volunteer) {
        this.id = id;
        this.adopter = adopter;
        this.volunteer = volunteer;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public Adopter getAdopter() {
        return adopter;
    }
}

