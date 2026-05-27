package test;

import model.entity.EntityCoordinate;

public class MovementCliDemo {
    private static final int WORLD_WIDTH = 40;
    private static final int WORLD_HEIGHT = 20;
    private static final int TICKS = 60;

    private static final double PREY_MIN_SPEED = 0.3;
    private static final double PREY_MAX_SPEED = 0.7;
    private static final double PREY_TURN = Math.toRadians(20.0);

    private static final double PREDATOR_BASE_SPEED = 0.5;
    private static final double PREDATOR_TURN = Math.toRadians(6.0);
    private static final double PREDATOR_FOV = Math.toRadians(150.0);
    private static final double PREDATOR_VIEW_DISTANCE = 12.0;
    private static final double PREDATOR_SPEED_BOOST = 1.8;

    public static void main(String[] args) {
        TestAnimal prey = new TestAnimal("Prey", new EntityCoordinate(10.0, 10.0), 0.5, 1.0, 0.0);
        TestAnimal predator = new TestAnimal("Predator", new EntityCoordinate(25.0, 12.0), PREDATOR_BASE_SPEED, -1.0, 0.0);

        for (int tick = 1; tick <= TICKS; tick++) {
            // Prey roams with wider turns and slower speed.
            prey.roamRandomly(PREY_MIN_SPEED, PREY_MAX_SPEED, PREY_TURN);
            clampToWorld(prey);

            // Predator speeds up and moves toward prey when it is in view.
            boolean seesPrey = predator.isInFieldOfView(prey.getPosition(), PREDATOR_FOV, PREDATOR_VIEW_DISTANCE);
            if (seesPrey) {
                predator.moveToward(prey.getPosition(), PREDATOR_SPEED_BOOST, 0.5);
            } else {
                predator.roamRandomly(PREDATOR_BASE_SPEED * 0.6, PREDATOR_BASE_SPEED, PREDATOR_TURN);
            }
            clampToWorld(predator);

            double distance = predator.distanceTo(prey.getPosition());
            System.out.printf(
                    "Tick %02d | %s(%.2f, %.2f) spd=%.2f | %s(%.2f, %.2f) spd=%.2f | dist=%.2f | sees=%s%n",
                    tick,
                    prey.getName(), prey.getPosition().getPosX(), prey.getPosition().getPosY(), prey.getSpeed(),
                    predator.getName(), predator.getPosition().getPosX(), predator.getPosition().getPosY(), predator.getSpeed(),
                    distance,
                    seesPrey
            );
        }
    }

    private static void clampToWorld(TestAnimal animal) {
        // Keep the animal within the demo world boundaries by clamping its coordinates.
        double x = clamp(animal.getPosition().getPosX(), 0.0, WORLD_WIDTH - 1.0);
        double y = clamp(animal.getPosition().getPosY(), 0.0, WORLD_HEIGHT - 1.0);
        animal.move(x, y);
    }

    private static double clamp(double value, double min, double max) {
        // Clamp a value into the inclusive [min, max] range.
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
