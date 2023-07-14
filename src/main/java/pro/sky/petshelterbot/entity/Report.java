package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "pet_id")
    @ManyToOne
    private Pet pet;

    private LocalDate sent;

    private String diet;
    private String wellBeing;
    private String behaviour;
    private String photoFilename;
    private String thumbnailPhotoFilename;

    /**
     * indicates whether the report has been reviewed or not
     **/
    private boolean checked;
    /**
     * indicates if the report is ok or should be improved next time
     * TODO think about how to manage or change this note
     **/
    private boolean approved;

    public Report() {
    }

    public Report(Pet pet,
                  LocalDate sent,
                  String diet, String wellBeing,
                  String behaviour,
                  String photoFilename,
                  String thumbnailPhotoFilename,
                  boolean checked,
                  boolean approved) {
        this.pet = pet;
        this.sent = sent;
        this.diet = diet;
        this.wellBeing = wellBeing;
        this.behaviour = behaviour;
        this.photoFilename = photoFilename;
        this.thumbnailPhotoFilename = thumbnailPhotoFilename;
        this.checked = checked;
        this.approved = approved;
    }

    public long getId() {
        return id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public LocalDate getSent() {
        return sent;
    }

    public void setSent(LocalDate sent) {
        this.sent = sent;
    }

    public String getDiet() {
        return diet;
    }

    public void setDiet(String diet) {
        this.diet = diet;
    }

    public String getWellBeing() {
        return wellBeing;
    }

    public void setWellBeing(String wellBeing) {
        this.wellBeing = wellBeing;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

    public String getPhotoFilename() {
        return photoFilename;
    }

    public void setPhotoFilename(String photoFilename) {
        this.photoFilename = photoFilename;
    }

    public String getThumbnailPhotoFilename() {
        return thumbnailPhotoFilename;
    }

    public void setThumbnailPhotoFilename(String thumbnailPhotoFilename) {
        this.thumbnailPhotoFilename = thumbnailPhotoFilename;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", pet=" + pet +
                ", sent=" + sent +
                ", diet='" + diet + '\'' +
                ", wellBeing='" + wellBeing + '\'' +
                ", behaviour='" + behaviour + '\'' +
                ", photoFilename='" + photoFilename + '\'' +
                ", thumbnailPhotoFilename='" + thumbnailPhotoFilename + '\'' +
                ", checked=" + checked +
                ", approved=" + approved +
                '}';
    }
}
