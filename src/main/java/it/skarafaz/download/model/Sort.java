package it.skarafaz.download.model;

public class Sort {
    public enum Direction {
        ASC, DESC
    }

    private String property;
    private Direction direction;

    public Sort(String sort) {
        String[] params = sort.split("-");
        this.property = params[1];
        this.direction = Direction.valueOf(params[0]);
    }

    public String getProperty() {
        return property;
    }

    public Direction getDirection() {
        return direction;
    }
}
