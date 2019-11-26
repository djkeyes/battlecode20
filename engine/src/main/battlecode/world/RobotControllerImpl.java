package battlecode.world;

import battlecode.common.*;
import static battlecode.common.GameActionExceptionType.*;
import battlecode.instrumenter.RobotDeathException;
import battlecode.schema.Action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The actual implementation of RobotController. Its methods *must* be called
 * from a player thread.
 *
 * It is theoretically possible to have multiple for a single InternalRobot, but
 * that may cause problems in practice, and anyway why would you want to?
 *
 * All overriden methods should assertNotNull() all of their (Object) arguments,
 * if those objects are not explicitly stated to be nullable.
 */
public final strictfp class RobotControllerImpl implements RobotController {

    /**
     * The world the robot controlled by this controller inhabits.
     */
    private final GameWorld gameWorld;

    /**
     * The robot this controller controls.
     */
    private final InternalRobot robot;

    /**
     * Create a new RobotControllerImpl
     *
     * @param gameWorld the relevant world
     * @param robot the relevant robot
     */
    public RobotControllerImpl(GameWorld gameWorld, InternalRobot robot) {
        this.gameWorld = gameWorld;
        this.robot = robot;
    }

    // *********************************
    // ******** INTERNAL METHODS *******
    // *********************************

    /**
     * @return the robot this controller is connected to
     */
    public InternalRobot getRobot() {
        return robot;
    }

    /**
     * Throw a null pointer exception if an object is null.
     *
     * @param o the object to test
     */
    private static void assertNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException("Argument has an invalid null value");
        }
    }

    @Override
    public int hashCode() {
        return robot.getID();
    }

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    @Override
    public int getRoundLimit() {
        return gameWorld.getGameMap().getRounds();
    }

    @Override
    public int getRoundNum() {
        return gameWorld.getCurrentRound();
    }

    @Override
    public int getTeamSoup() {
        return gameWorld.getTeamInfo().getSoup(getTeam());
    }

    @Override
    public int getRobotCount() {
        return gameWorld.getObjectInfo().getRobotCount(getTeam());
    }

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    @Override
    public int getID() {
        return this.robot.getID();
    }

    @Override
    public Team getTeam() {
        return this.robot.getTeam();
    }

    @Override
    public RobotType getType() {
        return this.robot.getType();
    }

    @Override
    public MapLocation getLocation() {
        return this.robot.getLocation();
    }

    @Override
    public int getSoupCarrying() {
        return this.robot.getSoupCarrying();
    }

    @Override
    public int getDirtCarrying() {
        return this.robot.getDirtCarrying();
    }

    @Override
    public boolean isCurrentlyHoldingUnit() {
        return this.robot.isCurrentlyHoldingUnit();
    }

    private InternalRobot getRobotByID(int id) {
        if (!gameWorld.getObjectInfo().existsRobot(id))
            return null;
        return this.gameWorld.getObjectInfo().getRobotByID(id);
    }

    // ***********************************
    // ****** GENERAL SENSOR METHODS *****
    // ***********************************

    @Override
    public boolean onTheMap(MapLocation loc) {
        assertNotNull(loc);
        return gameWorld.getGameMap().onTheMap(loc);
    }

    private void assertCanSenseLocation(MapLocation loc) throws GameActionException {
        if(!canSenseLocation(loc)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location not within sensor range");
        }
    }

    @Override
    public boolean canSenseLocation(MapLocation loc) {
        assertNotNull(loc);
        return this.robot.canSenseLocation(loc);
    }

    @Override
    public boolean canSenseRadius(int radius) {
        return this.robot.canSenseRadius(radius);
    }

    @Override
    public boolean isLocationOccupied(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return this.gameWorld.getRobot(loc) != null;
    }

    @Override
    public RobotInfo senseRobotAtLocation(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        InternalRobot bot = gameWorld.getRobot(loc);
        if(bot != null)
            return bot.getRobotInfo();
        return null;
    }

    @Override
    public boolean canSenseRobot(int id) {
        InternalRobot sensedRobot = getRobotByID(id);
        return sensedRobot == null ? false : canSenseLocation(sensedRobot.getLocation());
    }

    @Override
    public RobotInfo senseRobot(int id) throws GameActionException {
        if(!canSenseRobot(id)){
            throw new GameActionException(CANT_SENSE_THAT,
                    "Can't sense given robot; It may not exist anymore");
        }
        return getRobotByID(id).getRobotInfo();
    }

    @Override
    public RobotInfo[] senseNearbyRobots() {
        return senseNearbyRobots(-1);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radius) {
        return senseNearbyRobots(radius, null);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radius, Team team) {
        return senseNearbyRobots(getLocation(), radius, team);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radius, Team team) {
        assertNotNull(center);
        InternalRobot[] allSensedRobots = gameWorld.getAllRobotsWithinRadius(center,
                radius == -1 ? (int) Math.ceil(this.robot.getCurrentSensorRadius()) : radius);
        List<RobotInfo> validSensedRobots = new ArrayList<>();
        for(InternalRobot sensedRobot : allSensedRobots){
            // check if this robot
            if (sensedRobot.equals(this.robot))
                continue;
            // check if can sense
            if (!canSenseLocation(sensedRobot.getLocation()))
                continue;
            // check if right team
            if (team != null && sensedRobot.getTeam() != team)
                continue;
            validSensedRobots.add(sensedRobot.getRobotInfo());
        }
        return validSensedRobots.toArray(new RobotInfo[validSensedRobots.size()]);
    }

    @Override
    public int senseSoup(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.getSoup(loc);
    }

    @Override
    public int sensePollution(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.getPollution(loc);
    }

    @Override
    public int senseElevation(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.getDirt(loc);
    }

    @Override
    public boolean senseFlooding(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.isFlooded(loc);
    }

    @Override
    public MapLocation adjacentLocation(Direction dir) {
        return getLocation().add(dir);
    }

    // ***********************************
    // ****** READINESS METHODS **********
    // ***********************************

    private void assertIsReady() throws GameActionException{
        if(!isReady()){
            throw new GameActionException(NOT_ACTIVE,
                    "This robot's action cooldown has not expired.");
        }
    }

    @Override
    public boolean isReady() {
        return getCooldownTurns() < 1;
    }

    @Override
    public float getCooldownTurns() {
        return this.robot.getCooldownTurns();
    }

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    private void assertCanMove(MapLocation loc) throws GameActionException{
        if(!canMove(loc))
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to the target location " + loc +".");
    }

    @Override
    public boolean canMove(Direction dir) {
        assertNotNull(dir);
        return canMove(adjacentLocation(dir));
    }

    @Override
    public boolean canMove(MapLocation center) {
        try {
            assertNotNull(center);
            return getType().canMove() && getLocation().distanceTo(center) <= 1 &&
                onTheMap(center) && !isLocationOccupied(center) &&
                (getType().canFly() || gameWorld.getDirtDifference(getLocation(), center)
                 <= GameConstants.MAX_DIRT_DIFFERENCE);
        } catch (GameActionException e) { return false; }
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        assertNotNull(dir);
        MapLocation center = adjacentLocation(dir);
        assertNotNull(center);
        assertIsReady();
        assertCanMove(center);
        this.robot.resetCooldownTurns();
        this.gameWorld.moveRobot(getLocation(), center);
        this.robot.setLocation(center);

        gameWorld.getMatchMaker().addMoved(getID(), getLocation());

        // also move the robot currently being picked up
        if (this.robot.isCurrentlyHoldingUnit())
            movePickedUpUnit(center);
    }

    // ***********************************
    // ****** BUILDING/SPAWNING **********
    // ***********************************

    private void assertCanBuildRobot(RobotType type, Direction dir) throws GameActionException{
        if(!canBuildRobot(type, dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't build desired robot in given direction, possibly due to " +
                            "insufficient currency, this robot can't build, " +
                            "cooldown not expired, or the spawn location is occupied.");
        }
    }

    @Override
    public boolean hasRobotBuildRequirements(RobotType type) {
        assertNotNull(type);
        return getType().canBuild(type) &&
               gameWorld.getTeamInfo().getSoup(getTeam()) >= type.cost;
    }

    @Override
    public boolean canBuildRobot(RobotType type, Direction dir) {
        try {
            assertNotNull(type);
            assertNotNull(dir);
            boolean hasBuildRequirements = hasRobotBuildRequirements(type);
            MapLocation spawnLoc = adjacentLocation(dir);
            boolean isClear = onTheMap(spawnLoc) && !isLocationOccupied(spawnLoc);
            boolean cooldownExpired = isReady();
            return hasBuildRequirements && isClear && cooldownExpired;
        } catch (GameActionException e) { return false; }
    }

    @Override
    public void buildRobot(RobotType type, Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanBuildRobot(type, dir);

        this.robot.resetCooldownTurns();

        int robotID = gameWorld.spawnRobot(type, adjacentLocation(dir), getTeam());

        gameWorld.getMatchMaker().addAction(getID(), Action.SPAWN_UNIT, robotID);
    }

    // ***********************************
    // ****** MINER METHODS **************
    // ***********************************

    /**
     * Asserts that the robot can mine soup in the specified direction.
     *
     * @throws GameActionException
     */
    private void assertCanMineSoup(Direction dir) throws GameActionException{
        if(!canMineSoup(dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't mine soup in given direction, possibly due to " +
                            "cooldown not expired, this robot can't mine, " +
                            "or the mine location doesn't contain soup.");
        }
    }

    /**
     * Returns whether or not the robot can mine soup in a specified direction.
     * Checks if the robot can mine, whether they can carry more soup, whether
     *  the action cooldown is ready, whether the location is on the map,
     *  and whether there is soup to be mined in the target location.
     *
     * @param dir the direction to mine in
     */
    @Override
    public boolean canMineSoup(Direction dir) {
        MapLocation center = adjacentLocation(dir);
        return getType().canMine() && getSoupCarrying() < getType().soupLimit &&
                isReady() && onTheMap(center) && gameWorld.getSoup(center) > 0;
    }

    /**
     * Mines soup in a certain direction.
     *
     * @param dir the direction to mine in
     * @throws GameActionException
     */
    @Override
    public void mineSoup(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanMineSoup(dir);
        this.robot.resetCooldownTurns();
        this.gameWorld.removeSoup(adjacentLocation(dir));
        this.robot.addSoupCarrying(1);

        this.gameWorld.getMatchMaker().addAction(getID(), Action.MINE_SOUP, -1);
    }

    /**
     * Asserts that the robot can give soup in the specified direction.
     *
     * @throws GameActionException
     */
    private void assertCanGiveSoup(Direction dir, int amount) throws GameActionException{
        if(!canGiveSoup(dir, amount)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't give soup in given direction, possibly due to " +
                            "cooldown not expired, this robot can't give, " +
                            "this robot doesn't have enough crude soup, " +
                            "or the location doesn't have a refinery.");
        }
    }

    /**
     * Returns whether or not the robot can give soup in a specified direction.
     * Checks if the robot can give soup, whether they are carring crude soup, whether
     *  the action cooldown is ready, whether the location is on the map,
     *  and whether there is a refinery at the target location.
     *
     * @param dir the direction of the refinery to give soup to
     */
    @Override
    public boolean canGiveSoup(Direction dir, int amount) {
        MapLocation center = adjacentLocation(dir);
        if (!onTheMap(center))
            return false;
        InternalRobot adjacentRobot = this.gameWorld.getRobot(center);
        return getType().canGive() && isReady() && getSoupCarrying() >= amount && amount >= 0 &&
               adjacentRobot != null && adjacentRobot.getType().canRefine();
    }

    /**
     *  Gives soup in a certain direction; gives up to the amount of crude soup
     *  that the robot is carrying.
     *
     * @param dir the direction to mine in
     * @param amount the amount of soup to give
     * @throws GameActionException
     */
    @Override
    public void giveSoup(Direction dir, int amount) throws GameActionException {
        assertNotNull(dir);
        assertCanGiveSoup(dir, amount);
        amount = Math.min(amount, this.getSoupCarrying());
        this.robot.resetCooldownTurns();
        this.robot.removeSoupCarrying(amount);
        InternalRobot refinery = this.gameWorld.getRobot(adjacentLocation(dir));
        refinery.addSoupCarrying(amount);

        this.gameWorld.getMatchMaker().addAction(getID(), Action.GIVE_SOUP, refinery.getID());
    }

    // ***************************************
    // ********* LANDSCAPER METHODS **********
    // ***************************************

    /**
     * Asserts that the robot can dig dirt in the specified direction.
     *
     * @throws GameActionException
     */
    private void assertCanDigDirt(Direction dir) throws GameActionException{
        if(!canDigDirt(dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't dig dirt in given direction, possibly due to " +
                            "cooldown not expired, this robot can't dig, " +
                            "or the robot cannot carry more dirt.");
        }
    }

    /**
     * Returns whether or not the robot can dig dirt in a specified direction.
     * Checks if the robot can dig, whether they can carry more dirt, whether
     *  the action cooldown is ready, and whether the location is on the map.
     *
     * @param dir the direction to dig in
     */
    @Override
    public boolean canDigDirt(Direction dir) {
        MapLocation center = adjacentLocation(dir);
        return getType().canDig() && getDirtCarrying() < getType().dirtLimit &&
                isReady() && onTheMap(center);
    }

    /**
     * Digs dirt in a certain direction. THIS METHOD DOES NOT ADD ACTIONS TO
     *  MATCHMAKER BECAUSE THE TARGET CAN BE EITHER A BUILDING OR THE LOCATION,
     *  WE DON'T KNOW YET.
     *
     * @param dir the direction to dig in
     * @throws GameActionException
     */
    @Override
    public void digDirt(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanDigDirt(dir);
        this.robot.resetCooldownTurns();
        this.gameWorld.removeDirt(getID(), adjacentLocation(dir));
        this.robot.addDirtCarrying(1);
    }

    /**
     * Asserts that the robot can deposit dirt in the specified direction.
     *
     * @throws GameActionException
     */
    private void assertCanDepositDirt(Direction dir) throws GameActionException{
        if(!canDepositDirt(dir)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't deposit dirt in given direction, possibly due to " +
                            "cooldown not expired, this robot can't deposit, " +
                            "or this robot doesn't have dirt.");
        }
    }

    /**
     * Returns whether or not the robot can deposit dirt in a specified direction.
     * Checks if the robot can deposit, whether they are carrying enough dirt, whether
     *  the action cooldown is ready, and whether the location is on the map.
     *
     * @param dir the direction to deposit in
     */
    @Override
    public boolean canDepositDirt(Direction dir) {
        MapLocation center = adjacentLocation(dir);
        return (getType().canDeposit() && isReady() &&
                onTheMap(center) && getDirtCarrying() > 0);
    }

    /**
     * Deposits dirt in a certain direction. THIS METHOD DOES NOT ADD ACTIONS TO
     *  MATCHMAKER BECAUSE THE TARGET CAN BE EITHER A BUILDING OR THE LOCATION,
     *  WE DON'T KNOW YET.
     *
     * @param dir the direction to deposit in
     * @throws GameActionException
     */
    @Override
    public void depositDirt(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertCanDepositDirt(dir);
        this.robot.resetCooldownTurns();
        this.robot.removeDirtCarrying(1);
        this.gameWorld.addDirt(getID(), adjacentLocation(dir), 1);
    }

    // **************************************
    // ********** REFINERY METHODS **********
    // **************************************

    /**
     * Asserts that the robot can refine soup.
     *
     * @throws GameActionException
     */
    private void assertCanRefine(int amount) throws GameActionException{
        if(!canRefine(amount)){
            throw new GameActionException(CANT_DO_THAT,
                    "Can't refine soup, possibly due to cooldown not expired, " + 
                    "this robot can't refine soup, " +
                    "or this robot doesn't have soup.");
        }
    }

    /**
     * Tests whether the robot can refine the given amount of soup
     * Checks cooldown turns remaining, whether the robot can refine,
     * and that the robot has soup.
     *
     * @return whether it is possible to refine soup
     *
     * @battlecode.doc.costlymethod
     */
    @Override
    public boolean canRefine(int amount) {
        return (getType().canRefine() && isReady() &&
                getSoupCarrying() >= amount && amount > 0 &&
                amount <= getType().maxSoupProduced);
    }

    /**
     * Deposits dirt in the given direction (max up to specified amount).
     *
     * @param amount the amount of soup to refine
     * @throws GameActionException if this robot is not a refinery, if
     * the robot is still in cooldown, if there is no soup to refine,
     *
     * @battlecode.doc.costlymethod
     */
    @Override
    public void refine(int amount) throws GameActionException {
        assertCanRefine(amount);
        robot.resetCooldownTurns();
        robot.removeSoupCarrying(amount);
        gameWorld.getTeamInfo().adjustSoup(getTeam(), amount);

        gameWorld.getMatchMaker().addAction(getID(), Action.REFINE, -1);
    }

    // ***************************************
    // ******* DELIVERY DRONE METHODS ********
    // ***************************************

    /**
     * Asserts that the robot can pick up the unit with the specified id.
     *
     * @throws GameActionException
     */
    private void assertCanPickUpUnit(int id) throws GameActionException {
        if(!canPickUpUnit(id))
            throw new GameActionException(CANT_DO_THAT,
                    "Cannot pick up the specified unit, possibly due to not being a delivery drone, " +
                    "is already holding a unit, the unit not within pickup distance, or unit can't be picked up.");
    }

    /**
     * Returns whether or not the robot can pick up the unit with the specified id.
     * Checks if the robot can pick up units, whether they are already carrying a
     *  unit, whether the action cooldown is ready, whether the unit is within pickup
     *  distance, and whether the target unit can be picked up.
     *
     * @param id the id of the unit to be picked up
     */
    @Override
    public boolean canPickUpUnit(int id) {
        InternalRobot targetRobot = getRobotByID(id);
        return this.getType().canPickUpUnits() && !this.robot.isCurrentlyHoldingUnit() &&
               isReady() && targetRobot != null && targetRobot.getType().canBePickedUp() &&
               targetRobot.getLocation().isWithinDistance(getLocation(), GameConstants.DELIVERY_DRONE_PICKUP_RADIUS);
    }

    /**
     * Picks up the unit with the specified id.
     *
     * @param id the id of the unit to be picked up
     * @throws GameActionException
     */
    @Override
    public void pickUpUnit(int id) throws GameActionException {
        assertCanPickUpUnit(id);
        InternalRobot pickedUpRobot = getRobotByID(id);
        pickedUpRobot.blockUnit();
        gameWorld.removeRobot(pickedUpRobot.getLocation());
        this.robot.pickUpUnit(id);

        gameWorld.getMatchMaker().addAction(getID(), Action.PICK_UNIT, id);
    }

    /**
     * Asserts that the robot can drop off a unit in the specified direction.
     *
     * @throws GameActionException
     */
    private void assertCanDropUnit(Direction dir) throws GameActionException {
        if(!canDropUnit(dir))
            throw new GameActionException(CANT_DO_THAT,
                    "Cannot drop a unit, possibly due to not being a delivery drone, " +
                    "not currently holding a unit, or the target location is invalid or occupied.");
    }

    /**
     * Returns whether or not the robot can drop off a unit in the specified direction.
     * Checks if the robot can drop off units, whether they are carrying a unit, and
     *  whether the action cooldown is ready.
     *
     * @param dir the direction to drop off a unit
     */
    @Override
    public boolean canDropUnit(Direction dir) {
        MapLocation loc = adjacentLocation(dir);
        return isReady() && onTheMap(loc) && this.getType().canDropOffUnits() &&
            this.robot.isCurrentlyHoldingUnit();
    }

    /**
     * Drops off a unit in the specified direction.
     *
     * @param dir the direction to drop off a unit
     * @throws GameActionException
     */
    @Override
    public void dropUnit(Direction dir) throws GameActionException {
        assertNotNull(dir);
        dropUnit(dir, true);
    }

    /**
     * Drops off a unit in the specified direction.
     *
     * @param dir the direction to drop off a unit, or null if current location
     * @param checkConditions whether or not to assert if the robot can drop
     *        a unit; only false if a drone dies and automatically drops
     * @throws GameActionException
     */
    public void dropUnit(Direction dir, boolean checkConditions) throws GameActionException {
        if (checkConditions)
            assertCanDropUnit(dir);

        int id = this.robot.getIdOfUnitCurrentlyHeld();
        InternalRobot droppedRobot = getRobotByID(id);
        MapLocation targetLocation = dir == null ? getLocation() : adjacentLocation(dir);

        droppedRobot.unblockUnit();
        this.robot.dropUnit();
        this.gameWorld.addRobot(targetLocation, droppedRobot);

        gameWorld.getMatchMaker().addAction(getID(), Action.DROP_UNIT, id);

        // unit is destroyed if dropped in Ocean, onto a building, or onto another unit
        if (isLocationOccupied(targetLocation) || this.gameWorld.isFlooded(targetLocation))
            this.gameWorld.destroyRobot(id);
    }

    /**
     * Moves the picked up unit with the drone.
     *
     * @param center the new location of the drone
     * @throws GameActionException
     */
    private void movePickedUpUnit(MapLocation center) throws GameActionException {
        int id = this.robot.getIdOfUnitCurrentlyHeld();
        getRobotByID(id).setLocation(center);

        this.gameWorld.getMatchMaker().addMoved(id, getLocation());
    }

    // ***************************************
    // ******* NET GUN METHODS ***************
    // ***************************************

    /**
     * Asserts that the robot can shoot down the unit with the specified id.
     *
     * @throws GameActionException
     */
    private void assertCanShootUnit(int id) throws GameActionException {
        if(!canShootUnit(id))
            throw new GameActionException(CANT_DO_THAT,
                    "Cannot shoot down the specified unit, possibly due to not being a net gun, " +
                    "action cooldown not ready, the unit not within pickup distance, or unit can't be shot down.");
    }

    /**
     * Returns whether or not the robot can shoot down the unit with the specified id.
     * Checks if the robot can shoot down units, whether the action cooldown is ready,
     *  whether the unit is within pickup distance, and whether the target unit can be shot down.
     *
     * @param id the id of the unit to be shot down
     */
    @Override
    public boolean canShootUnit(int id) {
        InternalRobot targetRobot = getRobotByID(id);
        return this.getType().canShoot() && isReady() && targetRobot != null &&
               targetRobot.getType().canBeShot() &&
               targetRobot.getLocation().isWithinDistance(getLocation(), GameConstants.NET_GUN_SHOOT_RADIUS);
    }

    /**
     * Shoots down the unit with the specified id.
     *
     * @param id the id of the unit to be shot down
     * @throws GameActionException
     */
    @Override
    public void shootUnit(int id) throws GameActionException {
        assertCanShootUnit(id);
        this.gameWorld.destroyRobot(id);

        gameWorld.getMatchMaker().addAction(getID(), Action.SHOOT, id);
    }

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    @Override
    public void disintegrate(){
        throw new RobotDeathException();
    }

    @Override
    public void resign(){
        gameWorld.getObjectInfo().eachRobot((robot) -> {
            if(robot.getTeam() == getTeam()){
                gameWorld.destroyRobot(robot.getID());
            }
            return true;
        });
    }

    // ***********************************
    // ****** BLOCKCHAINNNNNNNNNNN *******
    // ***********************************

    /**
     * Sends a message to the blockchain at the indicated cost.
     * 
     * @param message the message to send.
     * @param proofOfStake the price that the unit is willing to pay for the message. If
     * the team does not have that much soup, the message will not be sent.
     * 
     */
    @Override
    public void sendMessage(int[] messageArray, int cost) throws GameActionException {
        if (messageArray.length > GameConstants.MAX_BLOCKCHAIN_MESSAGE_LENGTH) {
            throw new GameActionException(TOO_LONG_BLOCKCHAIN_MESSAGE,
                    "Can only send " + Integer.toString(GameConstants.MAX_BLOCKCHAIN_MESSAGE_LENGTH) + " integers in one message.");
        }
        int teamSoup = gameWorld.getTeamInfo().getSoup(getTeam());
        if (gameWorld.getTeamInfo().getSoup(getTeam()) < cost) {
            throw new GameActionException(NOT_ENOUGH_RESOURCE, 
                    "Tried to pay " + Integer.toString(cost) + " units of soup for a message, only has " + Integer.toString(teamSoup) + ".");
        }
        // pay!
        gameWorld.getTeamInfo().adjustSoup(getTeam(), -cost);
        // create a block chain entry
        BlockchainEntry bcentry = new BlockchainEntry(cost, messageArray);
        // add
        gameWorld.addNewMessage(bcentry);
    }

    /**
     * Gets all messages that were sent at a given round.
     *
     * @param roundNumber the round index.
     * @throws GameActionException
     */
    @Override
    public String getRoundMessages(int roundNumber) throws GameActionException {
        if (roundNumber < 0) {
            throw new GameActionException(ROUND_OUT_OF_RANGE, "You cannot get the messages sent at round " + Integer.toString(roundNumber)
                + "; in fact, no negative round numbers are allowed at all.");
        }
        if (roundNumber >= gameWorld.currentRound) {
            throw new GameActionException(ROUND_OUT_OF_RANGE, "You cannot get the messages sent at round " + Integer.toString(roundNumber)
                + "; you can only query previous rounds, and this is round " + Integer.toString(roundNumber) + ".");
        }
        // just get it!
        ArrayList<BlockchainEntry> d = gameWorld.blockchain.get(roundNumber);
        // System.out.println(d);
        BlockchainEntry[] d2 = d.toArray(new BlockchainEntry[d.size()]);
        String[] stringMessageArray = new String[d2.length];
        for (int i = 0; i < d2.length; i++) {
            stringMessageArray[i] = d2[i].serializedMessage;
        }
        String serializedMessage = String.join(" ", stringMessageArray);
        return serializedMessage;
    }

    // ***********************************
    // **** INDICATOR STRING METHODS *****
    // ***********************************

    @Override
    public void setIndicatorDot(MapLocation loc, int red, int green, int blue) {
        assertNotNull(loc);
        gameWorld.getMatchMaker().addIndicatorDot(getID(), loc, red, green, blue);
    }

    @Override
    public void setIndicatorLine(MapLocation startLoc, MapLocation endLoc, int red, int green, int blue) {
        assertNotNull(startLoc);
        assertNotNull(endLoc);
        gameWorld.getMatchMaker().addIndicatorLine(getID(), startLoc, endLoc, red, green, blue);
    }

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    @Override
    public long getControlBits() {
        return robot.getControlBits();
    }
}
