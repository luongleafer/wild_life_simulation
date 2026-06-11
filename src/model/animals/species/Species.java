package model.animals.species;


 // Luu cac thong so di chuyen va cam nhan dung chung cho cung mot loai dong vat.

public class Species {
    private final String name;
    private final double minSpeed;
    private final double maxSpeed;
    private final double viewDistance;
    private final double fovRadians;
    private final double turnRate;
    private final double huntSpeedMultiplier;
    private final double fleeSpeedMultiplier;
    private final double followDistance;

    public Species(
            String name,
            double minSpeed,
            double maxSpeed,
            double viewDistance,
            double fovRadians,
            double turnRate,
            double huntSpeedMultiplier,
            double fleeSpeedMultiplier,
            double followDistance
    ) {
        this.name = name;
        this.minSpeed = minSpeed;
        this.maxSpeed = maxSpeed;
        this.viewDistance = viewDistance;
        this.fovRadians = fovRadians;
        this.turnRate = turnRate;
        this.huntSpeedMultiplier = huntSpeedMultiplier;
        this.fleeSpeedMultiplier = fleeSpeedMultiplier;
        this.followDistance = followDistance;
    }

    public String getName() {
        return name;
    }

    public double getMinSpeed() {
        return minSpeed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getViewDistance() {
        return viewDistance;
    }

    public double getFovRadians() {
        return fovRadians;
    }

    public double getTurnRate() {
        return turnRate;
    }

    public double getHuntSpeedMultiplier() {
        return huntSpeedMultiplier;
    }

    public double getFleeSpeedMultiplier() {
        return fleeSpeedMultiplier;
    }

    public double getFollowDistance() {
        return followDistance;
    }
}
