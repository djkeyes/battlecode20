package battlecode.common;

/**
 * Struct that stores basic information that was 'sensed' of another Robot. This
 * info is ephemeral and there is no guarantee any of it will remain the same
 * between rounds.
 */
public class RobotInfo {

    /**
     * The unique ID of the robot.
     */
    public final int ID;

    /**
     * The Team that the robot is on.
     */
    public final Team team;

    /**
     * The type of the robot.
     */
    public final RobotType type;

    /**
     * The current location of the robot.
     */
    public final MapLocation location;

    /**
     * The current core delay of the robot.
     */
    public final double coreDelay;

    /**
     * The current weapon delay of the robot.
     */
    public final double weaponDelay;

    /**
     * The current health of the robot.
     */
    public final double health;

    public RobotInfo(int ID, Team team, RobotType type, MapLocation location,
                     double coreDelay, double weaponDelay, double health) {
        super();
        this.ID = ID;
        this.team = team;
        this.type = type;
        this.location = location;
        this.coreDelay = coreDelay;
        this.weaponDelay = weaponDelay;
        this.health = health;
    }

    public int hashCode() {
        return ID;
    }

    @Override
    public String toString() {
        return "RobotInfo{" +
                "ID=" + ID +
                ", team=" + team +
                ", type=" + type +
                ", location=" + location +
                ", coreDelay=" + coreDelay +
                ", weaponDelay=" + weaponDelay +
                ", health=" + health +
                '}';
    }
}
