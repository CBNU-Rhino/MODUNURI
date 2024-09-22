package app.app.course;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TouristSpotCount {

    @Id
    private String region;
    private int count;

    // 생성자
    public TouristSpotCount(String region, int count) {
        this.region = region;
        this.count = count;
    }

    // 기본 생성자 (필수)
    public TouristSpotCount() {
    }

    // Getters and Setters
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
