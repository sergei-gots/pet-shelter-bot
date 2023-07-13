package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(name = "pet_id")
    @ManyToOne
    private Pet pet;

    LocalDateTime sentTime;

    String diet;
    String wellBeing;
    String behaviour;
    String photoFilename;
    String thumbnailPhotoFilename;

    /**
     * indicates whether the report has been reviewed or not
     **/
    boolean checked;
    /**
     * indicates if the report is ok or should be improved next time
     * TODO think about how to manage or change this note
     **/
    boolean approved;

    public Report() {
    }

    public Report(Pet pet,
                  LocalDateTime sentTime,
                  String diet, String wellBeing,
                  String behaviour,
                  String photoFilename,
                  String thumbnailPhotoFilename,
                  boolean checked,
                  boolean approved) {
        this.pet = pet;
        this.sentTime = sentTime;
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

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
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
                ", sentTime=" + sentTime +
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
