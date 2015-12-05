package battlecode.common;

/**
 * A RobotController allows contestants to make their robot sense and interact
 * with the game world. When a contestant's <code>RobotPlayer</code> is
 * constructed, it is passed an instance of <code>RobotController</code> that
 * controls the newly created robot.
 */
@SuppressWarnings("unused")
public interface RobotController {

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    /**
     * Gets the number of rounds in the game. After this many rounds, if neither
     * team has destroyed the enemy HQ, then the tiebreakers will be used.
     *
     * @return the number of rounds in the game.
     */
    int getRoundLimit();

    /**
     * Gets the team's total ore.
     *
     * @return the team's total ore.
     */
    double getTeamOre();

    /**
     * Returns the zombie spawn schedule for the map. Only works on zombie dens. NOT AVAILABLE TO COMPETITORS?
     *
     * @return the zombie spawn schedule.
     */
    ZombieCount[] getZombieSpawnSchedule(int round);

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    /**
     * Use this method to access your ID.
     *
     * @return the ID of the robot.
     */
    int getID();

    /**
     * Gets the Team of this robot.
     *
     * @return this robot's Team
     */
    Team getTeam();

    /**
     * Gets this robot's type (SOLDIER, HQ, etc.).
     *
     * @return this robot's type.
     */
    RobotType getType();

    /**
     * Gets the robot's current location.
     *
     * @return this robot's current location.
     */
    MapLocation getLocation();

    /**
     * Returns the amount of core delay a robot has accumulated. If the result
     * is strictly less than 1, then the robot can perform a core action.
     *
     * @return the amount of core delay a robot has accumulated.
     */
    double getCoreDelay();

    /**
     * Returns the amount of weapon delay a robot has accumulated. If the result
     * is strictly less than 1, then the robot can attack.
     *
     * @return the number of weapon delay a robot has accumulated.
     */
    double getWeaponDelay();

    /**
     * Gets the robot's current health.
     *
     * @return this robot's current health.
     */
    double getHealth();

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    /**
     * Senses the rubble at the given location. Returns -1 for a location
     * outside sensor range.
     *
     * @param loc
     *            the location to check.
     * @return the amount of rubble at the location
     */
    int senseRubble(MapLocation loc);
    
    /**
     * Senses the parts at the given location. Returns -1 for a location
     * outside sensor range.
     *
     * @param loc
     *            the location to check.
     * @return the amount of parts at the location
     */   
    int senseParts(MapLocation loc);

    /**
     * Returns true if the given location is within the robot's sensor range, or
     * within the sensor range of some ally.
     *
     * @param loc
     *            the location to check.
     * @return whether the given location is within the robot's sensor range.
     */
    boolean canSenseLocation(MapLocation loc);

    /**
     * Returns whether there is a robot at the given location.
     *
     * @param loc
     *            the location to check.
     * @return whether there is a robot at the given location.
     * @throws GameActionException
     *             if <code>loc</code> is not within sensor range
     *             (CANT_SENSE_THAT).
     */
    boolean isLocationOccupied(MapLocation loc) throws GameActionException;

    /**
     * Returns the robot at the given location, or <code>null</code> if there is
     * no object there.
     *
     * @param loc
     *            the location to check.
     * @return the robot at the given location.
     * @throws GameActionException
     *             if <code>loc</code> is not within sensor range
     *             (CANT_SENSE_THAT).
     */
    RobotInfo senseRobotAtLocation(MapLocation loc)
            throws GameActionException;

    /**
     * Returns true if the given robot is within the robot's sensor range.
     * 
     * @param id
     *            the ID of the robot to query.
     * @return whether the given robot is within the robot's sensor range.
     */
    boolean canSenseRobot(int id);

    /**
     * Senses information about a particular robot given its ID.
     * 
     * @param id
     *            the ID of the robot to query.
     * @return a RobotInfo object for the sensed robot.
     * @throws GameActionException
     *             if the robot cannot be sensed (for example, if it doesn't
     *             exist or is out of sight range).
     */
    RobotInfo senseRobot(int id) throws GameActionException;

    /**
     * Returns all robots that can be sensed on the map.
     * 
     * @return array of class type of game objects.
     */
    RobotInfo[] senseNearbyRobots();

    /**
     * Returns all robots that can be sensed within a certain radius of the
     * robot.
     * 
     * @param radiusSquared
     *            return objects this distance away from the center.
     * @return array of class type of game objects.
     */
    RobotInfo[] senseNearbyRobots(int radiusSquared);

    /**
     * Returns all robots of a given team that can be sensed within a certain
     * radius of the robot.
     * 
     * @param radiusSquared
     *            return objects this distance away from the center.
     * @param team
     *            filter game objects by the given team. If null is passed,
     *            objects from all teams are returned.
     * @return array of class type of game objects.
     */
    RobotInfo[] senseNearbyRobots(int radiusSquared, Team team);

    /**
     * Returns all robots of a givin team that can be sensed within a certain
     * radius of a specified location.
     *
     * @param center
     *            center of the given search radius.
     * @param radiusSquared
     *            return objects this distance away from the center.
     * @param team
     *            filter game objects by the given team. If null is passed,
     *            objects from all teams are returned.
     * @return array of class type of game objects.
     */
    RobotInfo[] senseNearbyRobots(MapLocation center, int radiusSquared,
            Team team);

    // ***********************************
    // ****** READINESS METHODS **********
    // ***********************************

    /**
     * Returns whether the core delay is strictly less than 1 (whether the robot
     * can perform a core action in the given turn).
     * 
     * @return whether the robot can perform a core action in this turn.
     */
    boolean isCoreReady();

    /**
     * Returns whether the weapon delay is less than 1 (whether the robot can
     * attack in the given turn).
     *
     * @return whether the robot is able to attack in the current turn.
     */
    boolean isWeaponReady();

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    /**
     * Tells whether this robot can move in the given direction, without taking
     * any sort of delays into account. Takes into account only the map terrain,
     * positions of other robots, and the current robot's type (MISSILE and
     * DRONE can move over VOID). Does not take into account whether this robot
     * is currently active, but will only return true for units that are capable
     * of movement. Returns false for the OMNI and NONE directions.
     *
     * @param dir
     *            the direction to move in.
     * @return true if there are no robots or voids preventing this robot from
     *         moving in the given direction; false otherwise.
     */
    boolean canMove(Direction dir);

    /**
     * Queues a move in the given direction to be performed at the end of this
     * turn.
     *
     * @param dir
     *            the direction to move in.
     * @throws GameActionException
     *             if the robot cannot move in this direction.
     */
    void move(Direction dir) throws GameActionException;

    // ***********************************
    // ****** ATTACK METHODS *************
    // ***********************************

    /**
     * Returns whether the given location is within the robot's attack range.
     * Does not take into account whether the robot is currently attacking or
     * has the delay to do so.
     *
     * @param loc
     *            the location to attempt to attack.
     * @return true if the given location is within this robot's attack range.
     *         Does not take into account whether the robot is currently
     *         attacking.
     */
    boolean canAttackLocation(MapLocation loc);

    /**
     * Queues an attack on the given location to be performed at the end of this
     * turn.
     *
     * @param loc
     *            the location to attack.
     * @throws GameActionException
     *             if the robot cannot attack the given square.
     */
    void attackLocation(MapLocation loc) throws GameActionException;

    // ***********************************
    // ****** BROADCAST METHODS **********
    // ***********************************

    /**
     * Broadcasts a message to the global message board.
     *
     * @param channel
     *            the channel to write to, from 0 to
     *            <code>BROADCAST_MAX_CHANNELS</code>.
     * @param data
     *            one int's worth of data to write.
     * @throws GameActionException
     *             if the channel is invalid.
     */
    void broadcast(int channel, int data) throws GameActionException;

    /**
     * Retrieves the message stored at the given radio channel.
     *
     * @param channel
     *            radio channel to query, from 0 to
     *            <code>BROADCAST_MAX_CHANNELS</code>.
     * @return data currently stored on the channel.
     * @throws GameActionException
     *             if the channel is invalid.
     */
    int readBroadcast(int channel) throws GameActionException;

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    /**
     * Returns whether you have the ore and the dependencies to build the given
     * robot, and that the robot can build structures.
     *
     * @param type
     *            the type to build.
     * @return whether the requirements to build are met.
     */
    boolean hasBuildRequirements(RobotType type);

    /**
     * Returns whether the robot can build a structure of the given type in the
     * given direction, without taking delays into account. Checks dependencies,
     * ore costs, whether the robot can build, and that the given direction is
     * not blocked. Does not check if a robot has sufficiently low coreDelay or
     * not.
     *
     * @param dir
     *            the direction to build in.
     * @param type
     *            the robot type to spawn.
     * @return whether it is possible to build a building of the given type in
     *         the given direction.
     */
    boolean canBuild(Direction dir, RobotType type);

    /**
     * Builds a structure in the given direction, queued for the end of the
     * turn. The structure will initially be inactive for a number of turns
     * (during which this robot cannot move or attack). After a number of turns,
     * the structure will become active.
     *
     * @param dir
     *            the direction to bulid in.
     * @param type
     *            the type to build.
     * @throws GameActionException
     *             if the build is bad.
     */
    void build(Direction dir, RobotType type) throws GameActionException;

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    /**
     * Ends the current round. Never fails.
     */
    void yield();

    /**
     * Kills your robot and ends the current round. Never fails.
     */
    void disintegrate();

    /**
     * Causes your team to lose the game. It's like typing "gg."
     */
    void resign();

    /**
     * Turret only. Transforms the turret into a TTM after a short delay.
     */
    void pack() throws GameActionException;

    /**
     * TTM only. Transforms the TTM into a turret after a short delay.
     */
    void unpack()throws GameActionException;

    // ***********************************
    // ******** MISC. METHODS ************
    // ***********************************

    /**
     * Sets the team's "memory", which is saved for the next game in the match.
     * The memory is an array of {@link GameConstants#TEAM_MEMORY_LENGTH} longs.
     * If this method is called more than once with the same index in the same
     * game, the last call is what is saved for the next game.
     *
     * @param index
     *            the index of the array to set.
     * @param value
     *            the data that the team should remember for the next game.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if {@code index} is less than zero or greater than or equal
     *             to {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long, long)
     */
    void setTeamMemory(int index, long value);

    /**
     * Sets this team's "memory". This function allows for finer control than
     * {@link #setTeamMemory(int, long)} provides. For example, if
     * {@code mask == 0xFF} then only the eight least significant bits of the
     * memory will be set.
     *
     * @param index
     *            the index of the array to set.
     * @param value
     *            the data that the team should remember for the next game.
     * @param mask
     *            indicates which bits should be set.
     * @throws java.lang.ArrayIndexOutOfBoundsException
     *             if {@code index} is less than zero or greater than or equal
     *             to {@link GameConstants#TEAM_MEMORY_LENGTH}.
     * @see #getTeamMemory
     * @see #setTeamMemory(int, long)
     */
    void setTeamMemory(int index, long value, long mask);

    /**
     * Returns the team memory from the last game of the match. The return value
     * is an array of length {@link GameConstants#TEAM_MEMORY_LENGTH}. If
     * setTeamMemory was not called in the last game, or there was no last game,
     * the corresponding long defaults to 0.
     *
     * @return the team memory from the the last game of the match.
     * @see #setTeamMemory(int, long)
     * @see #setTeamMemory(int, long, long)
     */
    long[] getTeamMemory();

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    /**
     * Sets one of this robot's 'indicator strings' for debugging purposes.
     * These strings are displayed in the client. This method has no effect on
     * gameplay (aside from the number of bytecodes executed to call this
     * method).
     *
     * @param stringIndex
     *            the index of the indicator string to set. Must be between 0
     *            and GameConstants.NUMBER_OF_INDICATOR_STRINGS.
     * @param newString
     *            the value to which the indicator string should be set.
     */
    void setIndicatorString(int stringIndex, String newString);

    /**
     * Draws a dot on the game map, for debugging purposes. Press V in the
     * client to toggle which team's indicator dots are displayed.
     *
     * @param loc
     *            the location to draw the dot.
     * @param red
     *            the red component of the dot's color.
     * @param green
     *            the green component of the dot's color.
     * @param blue
     *            the blue component of the dot's color.
     */
    void setIndicatorDot(MapLocation loc, int red, int green, int blue);

    /**
     * Draws a line on the game map, for debugging purposes. Press V in the
     * client to toggle which team's indicator lines are displayed.
     *
     * @param from
     *            the location to draw the line from.
     * @param to
     *            the location to draw the line to.
     * @param red
     *            the red component of the line's color.
     * @param green
     *            the green component of the line's color.
     * @param blue
     *            the blue component of the line's color.
     */
    void setIndicatorLine(MapLocation from, MapLocation to, int red,
            int green, int blue);

    /**
     * Gets this robot's 'control bits' for debugging purposes. These bits can
     * be set manually by the user, so a robot can respond to them.
     *
     * @return this robot's control bits
     */
    long getControlBits();

    /**
     * Adds a custom observation to the match file, such that when it is
     * analyzed, this observation will appear.
     *
     * @param observation
     *            the observation you want to inject into the match file.
     */
    void addMatchObservation(String observation);

    /**
     * If breakpoints are enabled, calling this method causes the game engine to
     * pause execution at the end of this round, until the user decides to
     * resume execution.
     */
    void breakpoint();

    /**
     * Returns the number of bytecodes the current robot has executed since the beginning
     * of the current round.
     * @return the number of bytecodes the current robot has executed since the beginning of the current round.
     */
    int getBytecodeNum();

    /**
     * Returns the current round number, where round 0 is the first round of the match.
     * @return the current round number, where 0 is the first round of the match.
     */
    int getRoundNum();

    /**
     * Returns the number of bytecodes this robot has left in this round.
     * @return the number of bytecodes this robot has left in this round.
     */
    int getBytecodesLeft();
}
