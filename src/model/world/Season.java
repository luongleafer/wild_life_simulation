package model.world;

public enum Season {
    SPRING(0.8, 0.05, 0.03), // peaceful spring :)
    SUMMER(1.5, 0.1, 0.2), // hot brutal summer
    AUTUMN(0.9, 0.07, 0.05), // chill autumn
    WINTER(2, 0.15, 0.01); // harsh winter

    public final double animalHealthDepletionRate;
    public final double animalHungerDepletionRate;
    public final double animalThirstDepletionRate;

    Season(double healthDepletionRate, double hungerDepletionRate, double thirstDepletionRate) {
        this.animalHealthDepletionRate = healthDepletionRate;
        this.animalHungerDepletionRate = hungerDepletionRate;
        this.animalThirstDepletionRate = thirstDepletionRate;
    }

    public String getName(){
        return switch (this) {
            case SPRING -> "Spring";
            case SUMMER -> "Summer";
            case AUTUMN -> "Autumn";
            case WINTER -> "Winter";
        };
    }
}
