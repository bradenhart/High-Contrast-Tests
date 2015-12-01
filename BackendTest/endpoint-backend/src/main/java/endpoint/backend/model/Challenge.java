package endpoint.backend.model;

/**
 * Created by bradenhart on 7/09/15.
 */
public class Challenge {

    private Long id;
    private String name;
    private String description;
    private String difficulty;
    private Integer min;
    private Integer max;
    private Boolean completed;

    public Challenge() {}

    public Challenge(Long id, String name, String description, String difficulty, Integer min, Integer max, Boolean completed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.min = min;
        this.max = max;
        this.completed = completed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
}
