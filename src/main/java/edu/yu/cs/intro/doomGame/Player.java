package edu.yu.cs.intro.doomGame;
import java.util.*;

/**
 * Represents a player in the game.
 * A player whose health is <= 0 is dead.
 */
public class Player implements Comparable<Player> {
    private String name;
    private int health;
    protected Map<Weapon, Integer> firePower;
    private Set<Weapon> weaponsPacking;

    /**
     * @param name the player's name
     * @param health the player's starting health level
     */
    public Player(String name, int health) {
        this.name=name;
        this.health=health;
        this.firePower=new HashMap<>();
        this.weaponsPacking=new HashSet<>();
        weaponsPacking.add(Weapon.FIST);
        firePower.put(Weapon.FIST,Integer.MAX_VALUE);
        firePower.put(Weapon.CHAINSAW,0);
        firePower.put(Weapon.PISTOL,0);
        firePower.put(Weapon.SHOTGUN,0);
    }

    /**
     * @return name
     */
    public String getName() {
        return this.name;
    }

    /**
     * does this player have the given weapon?
     * @param w
     * @return
     */
    public boolean hasWeapon(Weapon w) {
        return weaponsPacking.contains(w);
    }

    /**
     * how much ammunition does this player have for the given weapon?
     * @param w
     * @return
     */
    public int getAmmunitionRoundsForWeapon(Weapon w) {
        return firePower.get(w);
    }

    /**
     * Change the ammunition amount by a positive or negative amount
     * @param weapon weapon whose ammunition count is to be changed
     * @param change amount to change ammunition count for that weapon by
     * @return the new total amount of ammunition the player has for the weapon.
     */
    public int changeAmmunitionRoundsForWeapon(Weapon weapon, int change) {
        if(weapon==Weapon.FIST) {
            return Integer.MAX_VALUE;
        }
        int ammo=0;
        if(firePower.get(weapon)+change>0) {
            ammo=firePower.get(weapon)+change;
        }
        firePower.put(weapon,ammo);
        return ammo;
    }

    /**
     * A player can have ammunition for a weapon even without having the weapon itself.
     * @param weapon weapon for which we are adding ammunition
     * @param rounds number of rounds of ammunition to add
     * @return the new total amount of ammunition the player has for the weapon
     * @throws IllegalArgumentException if rounds < 0 or weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected int addAmmunition(Weapon weapon, int rounds) throws IllegalArgumentException,IllegalStateException {
        if(rounds<0) {
            throw new IllegalArgumentException("rounds must be greater than 0");
        }
        if(weapon==null) {
            throw new IllegalArgumentException("weapon can't be null");
        }
        if(this.isDead()) {
            throw new IllegalStateException("Player is dead");
        }
        if(weapon==Weapon.FIST) {
            return Integer.MAX_VALUE;
        }
        int ammo=firePower.get(weapon)+rounds;
        firePower.put(weapon,ammo);
        return ammo;
    }

    /**
     * When a weapon is first added to a player, the player should automatically be given 5 rounds of ammunition.
     * If the player already has the weapon before this method is called, this method has no effect at all.
     * @param weapon
     * @return true if the weapon was added, false if the player already had it
     * @throws IllegalArgumentException if weapon is null
     * @throws IllegalStateException if the player is dead
     */
    protected boolean addWeapon(Weapon weapon) throws IllegalArgumentException,IllegalStateException {
        if(weapon==null) {
            throw new IllegalArgumentException("weapon is null");
        }
        if(this.isDead()) {
            throw new IllegalStateException("player is dead");
        }
        if(!weaponsPacking.contains(weapon)) {
            weaponsPacking.add(weapon);
            firePower.put(weapon,firePower.get(weapon)+5);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Change the player's health level
     * @param amount a positive or negative number, to increase or decrease the player's health
     * @return the player's health level after the change
     * @throws IllegalStateException if the player is dead
     */
    public int changeHealth(int amount) throws IllegalStateException {
        if(this.isDead()) {
            throw new IllegalStateException("player is dead");
        }
        this.health=this.health+amount;
        return this.health;
    }

    /**
     * set player's current health level to the given level
     * @param amount
     */
    protected void setHealth(int amount) {
        this.health=amount;
    }

    /**
     * get the player's current health level
     * @return
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * is the player dead?
     * @return
     */
    public boolean isDead() {
        return this.health<0;
    }

    /**
     * Compare criteria, in order:
     * Does one have a greater weapon?
     * If they have the same greatest weapon, who has more ammunition for it?
     * If they are the same on weapon and ammunition, who has more health?
     * If they are the same on greatest weapon, ammunition for it, and health, they are equal.
     * Recall that all enums have a built-in implementation of Comparable, and they compare based on ordinal()
     *
     * @param other
     * @return
     */
    @Override
    public int compareTo(Player other) {
        int thisMaxOrdinal=0;
        Weapon greatestWeapon=null;
        for(Weapon w:this.weaponsPacking) {
            if(w.ordinal()>thisMaxOrdinal) {
                thisMaxOrdinal=w.ordinal();
                greatestWeapon=w;
            }
        }
        int otherMaxOrdinal=0;
        for(Weapon weapon:other.weaponsPacking) {
            if(weapon.ordinal()>otherMaxOrdinal) {
                otherMaxOrdinal=weapon.ordinal();
            }
        }
        if(thisMaxOrdinal!=otherMaxOrdinal) {
            return thisMaxOrdinal-otherMaxOrdinal;
        }
        if(this.firePower.get(greatestWeapon)!=other.firePower.get(greatestWeapon)) {
            return this.firePower.get(greatestWeapon)-other.firePower.get(greatestWeapon);
        }
        return this.getHealth()-other.getHealth();
    }

    /**
     * Only equal if it is literally the same player
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        return this==(Player)(o);
    }

    /**
     * @return the hash code of the player's name
     */
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
