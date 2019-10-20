package battlecode.common;

/**
 * Contains details on various attributes of the different robots. All of this information is in the specs in a more organized form.
 */
public enum RobotType {

    // spawnSource, buildCooldown, soupCost, dirtLimit, soupLimit, movementCooldown, digCooldown, dropCooldown, mineCooldown, shootCooldown, sensorRadius, pollutionRadius, pollutionAmount, maxSoupProduced, bytecodeLimit
    /**
     * Miners extract crude soup and bring it to the refineries.
     *
     * @battlecode.doc.robottype
     */
    MINER                   (BASE,  20,  10,  0,  40,  2,  0,  0,  5,  0,  8,  0,  0,  0,  15000), // chef?
    //                       SS     BUC  SC   DL  SL   MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Landscapers take dirt from adjacent (decreasing the elevation)
     * squares or deposit dirt onto adjacent squares, including
     * into water (increasing the elevation).
     * @battlecode.doc.robottype
     */
    LANDSCAPER              (DESIGN_SCHOOL,  20,  10,  40,  0,  4,  4,  8,  0,  0,  4,  0,  0,  0,  15000),
    //                       SS              BUC  SC   DL   SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Drones pick up any unit and drop them somewhere else.
     * @battlecode.doc.robottype
     */
    DRONE                   (FULFILLMENT_CENTER,  20,  10,  0,  0,  8,  0,  0,  0,  0,  4,  0,  0,  0,  15000),
    //                       SS                   BUC  SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Cows produce pollution (and they moo).
     * @battlecode.doc.robottype
     */
    COW                     (null,  0,  0,  0,  0,  6,  0,  0,  0,  0,  0,  0,  0,  0,  0),
    //                       SS     BUC SC  DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Net guns shoot down drones.
     * @battlecode.doc.robottype
     */
    NET_GUN                 (MINER,  1,  7,  0,  0,  0,  0,  0,  0,  5,  6,  0,  0,  0,  15000),
    //                       SS      BUC SC  DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Refineries turn crude soup into refined soup, and produce pollution.
     * @battlecode.doc.robottype
     */
    REFINERY                (MINER,  5,  20,  0,  0,  0,  0,  0,  0,  0,  0,  4,  1,  10,  15000),
    //                        SS      BUC SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Vaporators reduce pollution.
     * @battlecode.doc.robottype
     */
    VAPORATOR               (MINER,  5,  20,  0,  0,  0,  0,  0,  0,  0,  0,  4,  -1,  5,  15000),
    //                       SS      BUC SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA   MS  BL
    /**
     * The base produces miners, is also a net gun and a refinery.
     * @battlecode.doc.robottype
     */
    HQ                      (null,  5,  20,  0,  0,  0,  0,  0,  0,  0,  7,  4,  1,  10,  15000),
    //                       SS      BUC SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Design schools create landscapers.
     * @battlecode.doc.robottype
     */
    DESIGN_SCHOOL           (MINER,  5,  20,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  15000),
    //                       SS      BUC SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    /**
     * Fulfillment centers create drones.
     * @battlecode.doc.robottype
     */
    FULFILLMENT_CENTER      (MINER,  5,  20,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  15000),
    //                       SS      BUC SC   DL  SL  MOC DIC DRC MIC SHC SR  PR  PA  MS  BL
    ;
    
    /**
     * For units, this is the structure that spawns it. For non-spawnable robots, this is null.
     */
    public final RobotType spawnSource;

    /**
     * Cooldown turns for structure that spawns robot.
     */
    public final int buildCooldown;

    /**
     * Cost for creating the robot.
     */
    public final int soupCost;

     /**
     * Limit for amount of dirt robot can hold.
     */
    public final int dirtLimit;

     /**
     * Limit for amount of crude soup robot can hold.
     */
    public final int soupLimit;

     /**
     * Cooldown turns for how long before a robot can move again.
     */
    public final int movementCooldown;

     /**
     * Cooldown turns for how long before a robot can dig again.
     */
    public final int digCooldown;

     /**
     * Cooldown turns for how long before a robot can drop dirt again.
     */
    public final int dropCooldown;

    /**
     * Cooldown turns for how long before a robot can mine again.
     */
    public final int mineCooldown;

    /**
     * Cooldown turns for how long before a robot can shoot again.
     */
    public final int shootCooldown;

    /**
     * Range for sensing robots and trees.
     */
    public final int sensorRadius;

    /**
     * How many units a cow pollutes.
     */
    public final int pollutionRadius;

    /**
     * Amount of pollution created when refining soup.
     */
    public final int pollutionAmount;

    /**
     * Maximum amount of soup to be refined per turn.
     */
    public final int maxSoupProduced;

    /**
     * Base bytecode limit of this robot.
     */
    public final int bytecodeLimit;


    /**
     * Returns whether the robot can build buildings.
     *
     * @return whether the robot can build.
     */
    public boolean canBuild(RobotType type) {
        return this == type.spawnSource;
    }

    /**
     * Returns whether the robot can move.
     *
     * @return whether the robot can move.
     */
    public boolean canMove() {
        return this == MINER || this == LANDSCAPER || this == DRONE || this == COW;
    }

    /**
     * Returns whether the robot can dig.
     *
     * @return whether the robot can dig.
     */
    public boolean canDig() {
        return this == LANDSCAPER;
    }

    /**
     * Returns whether the robot can mine.
     *
     * @return whether the robot can mine.
     */
    public boolean canMine() {
        return this == MINER;
    }

    /**
     * Returns whether the robot can shoot drones.
     *
     * @return whether the robot can shoot.
     */
    public boolean canShoot() {
        return this == NET_GUN;
    }

    /**
     * Returns whether the robot can pick up units.
     *
     * @return whether the robot can pick up units.
     */
    public boolean canPickUpUnits() {
        return this == DRONE;
    }

    RobotType(RobotType spawnSource, int buildCooldown, int soupCost, int dirtLimit, int soupLimit, 
              int movementCooldown, int digCooldown, int dropCooldown, int mineCooldown, int shootCooldown, 
              int sensorRadius, int pollutionRadius, int pollutionAmount, int maxSoupProduced, int bytecodeLimit) {
        this.spawnSource        = spawnSource;
        this.buildCooldown      = buildCooldown;
        this.soupCost           = soupCost;
        this.dirtLimit          = dirtLimit;
        this.soupLimit          = soupLimit;
        this.movementCooldown   = movementCooldown;
        this.digCooldown        = digCooldown;
        this.dropCooldown       = dropCooldown;
        this.mineCooldown       = mineCooldown;
        this.shootCooldown      = shootCooldown;
        this.sensorRadius       = sensorRadius;
        this.pollutionRadius    = pollutionRadius;
        this.pollutionAmount    = pollutionAmount;
        this.maxSoupProduced    = maxSoupProduced;
        this.bytecodeLimit      = bytecodeLimit;
    }
}
