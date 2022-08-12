package edu.yu.cs.intro.doomGame;
/**
 * Describes the types of monsters that can exist in our game
 * (Hat tip to Doom, 1993, by is Software)
 */
public enum MonsterType {
    IMP(Weapon.FIST,1,1),
    DEMON(Weapon.CHAINSAW,1,1),
    SPECTRE(Weapon.PISTOL,6,2),
    BARON_OF_HELL(Weapon.SHOTGUN,12,3);

    /**what weapon is needed to kill this monster?*/
    protected final Weapon weaponNeededToKill;
    /**how many rounds of ammunition must be fired at the monster from the required weapon to kill it?*/
    protected final int ammunitionCountNeededToKill;
    /**how much health does a player lose when he is in the same room as this type of monster?*/
    protected final int playerHealthLostPerExposure;

    /**
     *
     * @param weaponNeededToKill
     * @param ammunitionCountNeededToKill
     * @param playerHealthLostPerExposure
     */
    MonsterType(Weapon weaponNeededToKill, int ammunitionCountNeededToKill, int playerHealthLostPerExposure) {
        this.weaponNeededToKill = weaponNeededToKill;
        this.ammunitionCountNeededToKill = ammunitionCountNeededToKill;
        this.playerHealthLostPerExposure = playerHealthLostPerExposure;
    }

    /**
     * if this monster is in the same room as other monsters, what type of other monster would protected this one?
     * @return
     */
    public MonsterType getProtectedBy(){
        switch (this){
            case IMP:
                return null;
            case DEMON:
                return BARON_OF_HELL;
            case SPECTRE:
                return null;
            case BARON_OF_HELL:
                return SPECTRE;
        }
        return null;
    }
}