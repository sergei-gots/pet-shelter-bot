package pro.sky.petshelterbot.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "pet_id")
    @ManyToOne
    private Pet pet;

    private LocalDate sent;

    private String diet;
    private String wellBeing;
    private String behaviour;
    private String imgPath;

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

    public Report(Pet pet) {
        this.pet = pet;
    }

    public Report(Long id,
                  Pet pet,
                  LocalDate sent,
                  String diet, String wellBeing,
                  String behaviour,
                  String imgPath,
                  boolean checked,
                  boolean approved) {
        this.id = id;
        this.pet = pet;
        this.sent = sent;
        this.diet = diet;
        this.wellBeing = wellBeing;
        this.behaviour = behaviour;
        this.imgPath = imgPath;
        this.checked = checked;
        this.approved = approved;
    }

    public Long getId() {
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

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
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
                ", imgPath='" + imgPath + '\'' +
                ", checked=" + checked +
                ", approved=" + approved +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return checked == report.checked && approved == report.approved && Objects.equals(id, report.id) && Objects.equals(pet, report.pet) && Objects.equals(sent, report.sent) && Objects.equals(diet, report.diet) && Objects.equals(wellBeing, report.wellBeing) && Objects.equals(behaviour, report.behaviour) && Objects.equals(imgPath, report.imgPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pet, sent, diet, wellBeing, behaviour, imgPath, checked, approved);
    }
}
