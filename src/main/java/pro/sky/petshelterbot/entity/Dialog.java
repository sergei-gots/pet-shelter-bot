package pro.sky.petshelterbot.entity;

import javax.persistence.*;

@Entity
@Table(name = "adopter_volunteer_dialogs")
public class Dialog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * for developing purpose: we can have many waiting
     * dialogs (i.e. adopters) for the same adopter.
     * and we should have @ManyToOne - annotation.
     * <p>
     * for production purpose only @OneToOne - relation
     * is allowed.
     */

    @JoinColumn(name = "adopter_chat_id", nullable = false)
    @OneToOne //prod/dev purpose
    //@ManyToOne  //for test purpose
    private Adopter adopter;
    @JoinColumn(name = "volunteer_chat_id")
    @OneToOne
    private Volunteer volunteer;



    public Dialog() {
    }

    public Dialog(Adopter adopter) {
        this.adopter = adopter;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Adopter getAdopter() {
        return adopter;
    }
}

