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
     * The number of times this robot has moved in the current turn.
     */
    public final int moveCount;


    @Override
    public int getID() {
        return this.ID;
    }

    @Override
    public MapLocation getLocation() {
        return this.location;
    }

    public RobotInfo(int ID, Team team, RobotType type, MapLocation location, int moveCount) {
        super();
        this.ID = ID;
        this.team = team;
        this.type = type;
        this.location = location;
        this.moveCount = moveCount;
    }

    /**
     * Returns the team that this robot is on.
     *
     * @return the team that this robot is on.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Returns the type of this robot.
     *
     * @return the type of this robot.
     */
    public RobotType getType() {
        return type;
    }


    /**
     * Returns the number of times this robot has moved this turn.
     *
     * @return the number of times this robot has moved this turn.
     */
    public int getMoveCount() {
        return moveCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotInfo robotInfo = (RobotInfo) o;
        if (ID != robotInfo.ID) return false;
        if (team != robotInfo.team) return false;
        if (type != robotInfo.type) return false;
        if (moveCount != robotInfo.moveCount) return false;
        return location.equals(robotInfo.location);
    }
    /*
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = ID;
        result = 31 * result + team.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }
    */
    @Override
    public String toString() {
        return "RobotInfo{" +
                "ID=" + ID +
                ", team=" + team +
                ", type=" + type +
                ", location=" + location +
                 ", moveCount=" + moveCount +
                '}';
    }
}
