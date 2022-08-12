package edu.yu.cs.intro.doomGame;
import java.util.*;
import java.util.SortedSet;

/**
 * Plays through a given game scenario. i.e. tries to kill all the monsters in all the rooms and thus complete the game, using the given set of players
 */
public class GameBot {
    SortedSet<Room> rooms;
    SortedSet<Player> players;
    SortedSet<Player> livePlayers;
    Set<Room> roomsCompleted;
    /**
     * Create a new "GameBot", i.e. a program that automatically "plays the game"
     * @param rooms the set of rooms in this game
     * @param players the set of players the bot can use to try to complete all rooms
     */
    public GameBot(SortedSet<Room> rooms, SortedSet<Player> players) {
        this.rooms = rooms;
        this.players = players;
        this.livePlayers=new TreeSet<>();
        this.livePlayers.addAll(players);
        this.roomsCompleted=new HashSet<>();
    }

    /**
     * Try to complete killing all monsters in all rooms using the given set of players.
     * It could take multiple passes through the set of rooms to complete the task of killing every monster in every room.
     * This method should call #passThroughRooms in some loop that tracks whether all the rooms have been completed OR we
     * have reached a point at which no progress can be made. If we are "stuck", i.e. we haven't completed all rooms but
     * calls to #passThroughRooms are no longer increasing the number of rooms that have been completed, return false to
     * indicate that we can't complete the game. As long as the number of completed rooms continues to rise, keep calling
     * #passThroughRooms.
     *
     * Throughout our attempt/logic to play the game, we rely on and take advantage of the fact that Room, Monster,
     * and Player all implement Comparable, and the sets we work with are all SortedSets
     *
     * @return true if all rooms were completed, false if not
     */
    public boolean play() {
        int numberOfIncompleteRoomsBeforeLastPass=0;
        Set<Room>roomsLeft=new HashSet<>();
        roomsLeft.addAll(this.rooms);
        while(numberOfIncompleteRoomsBeforeLastPass==0||(roomsLeft.size()>0&&roomsLeft.size()<numberOfIncompleteRoomsBeforeLastPass)) {
            numberOfIncompleteRoomsBeforeLastPass=roomsLeft.size();
            Set<Room>roomsCompletedInThisPass=this.passThroughRooms();
            roomsLeft.removeAll(roomsCompletedInThisPass);
        }
        if(roomsLeft.size()==0) {
            return true;
        }
        return false;
    }

    /**
     * Pass through the rooms, killing any monsters that can be killed, and thus attempt to complete the rooms
     * @return the set of rooms that were completed in this pass
     */
    protected Set<Room> passThroughRooms() {
        Set<Room> roomsJustCompleted=new HashSet<>();
        //for every room that is not completed,
        //create the set of incomplete rooms and iterate over it
        Set<Room> roomsNotYetCompleted=new HashSet<>();
        for(Room room:this.rooms) {
            if(!room.isCompleted()) {
                roomsNotYetCompleted.add(room);
            }
        }
        for(Room r:roomsNotYetCompleted) {
            System.out.println("passthrou is attempting to complete room: "+r.getName());
            //for every living monster in that room
            //make a copy of live monsters so when change live monsters, no concurrent modification exception
            SortedSet<Monster>copyOfLiveMonsters=new TreeSet<>();
            copyOfLiveMonsters.addAll(r.getLiveMonsters());
            for(Monster m: copyOfLiveMonsters) {
                if(!r.getLiveMonsters().contains(m)) {
                    continue;
                }
                if(!r.isCompleted()) {
                    System.out.println("attemting to kill monster: "+m.getMonsterType());
                    //See if any of your players can kill the monster. If so, have the capable player kill it.
                    for(Player p:this.livePlayers) {
                        System.out.println("pass through rooms is attemtping to use player: "+p.getName());
                        if(!m.isDead()) {
                            boolean canKill=false;
                            try{
                                System.out.println("pass through rooms proceeded into canKill");
                                canKill=this.canKill(p,m,r);
                            }catch(IllegalArgumentException e) {
                                System.out.println("monster doesn't need to be killed: "+m);
                            }
                            if(canKill) {
                                System.out.println("program decided player: "+p.getName()+" can kill monster: " + m.getMonsterType()+" in room: "+r.getName());
                                this.killMonster(p,r,m);
                                if(r.isCompleted()) {
                                    roomsJustCompleted.add(r);
                                    this.reapCompletionRewards(p,r);
                                }
                            }
                            else {System.out.println("program decided it can't kill monster: "+m.getMonsterType());}
                        }
                        else {
                            break;
                        }
                    }
                }
                else {
                    break;
                }
            }
            //The player that causes the room to be completed by killing a monster reaps the rewards for completing that room.
        }
        return roomsJustCompleted;
        //Return the set of completed rooms.
    }

    /**
     * give the player the weapons, ammunition, and health that come from completing the given room
     * @param player
     * @param room
     */
    protected void reapCompletionRewards(Player player, Room room) {
        player.changeHealth(room.getHealthWonUponCompletion());
        for(Weapon weapon: room.getWeaponsWonUponCompletion()) {
            player.addWeapon(weapon);
        }
        for(Weapon w: room.ammoWonUponCompletion.keySet()) {
            player.addAmmunition(w,room.ammoWonUponCompletion.get(w));
        }

    }

    /**
     * Have the given player kill the given monster in the given room.
     * Assume that #canKill was already called to confirm that player's ability to kill the monster
     * @param player
     * @param room
     * @param monsterToKill
     */
    protected void killMonster(Player player, Room room, Monster monsterToKill) {
        System.out.println("\n killMonster call for: "+monsterToKill.getMonsterType());
        //Call getAllProtectorsInRoom to get a sorted set of all the monster's protectors in this room
        SortedSet<Monster> protectors=this.getAllProtectorsInRoom(monsterToKill,room);
        System.out.println("Kill monster for monster: "+monsterToKill.getMonsterType()+" found the following protectors");
        for(Monster monsterrrr:protectors) {
            System.out.println(monsterrrr.getMonsterType());
        }
        //Player must kill the protectors before it can kill the monster, so kill all the protectors
        //first via a recursive call to killMonster on each one.
        if(monsterToKill.getProtectedBy()==null) {
            System.out.println("Killing monster: "+monsterToKill.getMonsterType());
            System.out.println("old player health: "+player.getHealth());
            player.changeHealth(0-room.getPlayerHealthLostPerEncounter());
            System.out.println("new player health: "+player.getHealth());
            Weapon weaponToUse=null;
            Weapon weaponNeeded=monsterToKill.getMonsterType().weaponNeededToKill;
            for(Weapon w: Weapon.values()) {
                if(w.ordinal()>=weaponNeeded.ordinal()&&player.hasWeapon(w)) {
                    weaponToUse=w;
                    break;
                }
            }
            System.out.println("Killing monster using following weapon: "+weaponToUse);
            monsterToKill.attack(weaponToUse,monsterToKill.getMonsterType().ammunitionCountNeededToKill);
            //update player's ammo
            player.changeAmmunitionRoundsForWeapon(weaponToUse,0-monsterToKill.getMonsterType().ammunitionCountNeededToKill);
            room.monsterKilled(monsterToKill);
        }
        else {
            System.out.println("recursively called kill on all protectors");
            for(Monster m:protectors) {
                this.killMonster(player,room,m);
            }
            System.out.println("Killing monster: "+monsterToKill.getMonsterType());
            System.out.println("old player health: "+player.getHealth());
            player.changeHealth(0-room.getPlayerHealthLostPerEncounter());
            System.out.println("new player health: "+player.getHealth());
            Weapon weaponToUse=null;
            Weapon weaponNeeded=monsterToKill.getMonsterType().weaponNeededToKill;
            for(Weapon w: Weapon.values()) {
                if(w.ordinal()>=weaponNeeded.ordinal()&&player.hasWeapon(w)) {
                    weaponToUse=w;
                    break;
                }
            }
            System.out.println("Killing monster using following weapon: "+weaponToUse);
            monsterToKill.attack(weaponToUse,monsterToKill.getMonsterType().ammunitionCountNeededToKill);
            //update player's ammo
            player.changeAmmunitionRoundsForWeapon(weaponToUse,0-monsterToKill.getMonsterType().ammunitionCountNeededToKill);
            room.monsterKilled(monsterToKill);
        }
        //Reduce the player's health by the amount given by room.getPlayerHealthLostPerEncounter().
        //Attack (and thus kill) the monster with the kind of weapon, and amount of ammunition, needed to kill it.
    }

    /**
     * @return a set of all the rooms that have been completed
     */
    public Set<Room> getCompletedRooms() {
        return this.roomsCompleted;
    }

    /**
     * @return an unmodifiable collection of all the rooms in the came
     * @see java.util.Collections#unmodifiableSortedSet(SortedSet)
     */
    public SortedSet<Room> getAllRooms() {
        return Collections.unmodifiableSortedSet(rooms);
    }

    /**
     * @return a sorted set of all the live players in the game
     */
    protected SortedSet<Player> getLivePlayers() {
        return this.livePlayers;
    }

    /**
     * @param weapon
     * @param ammunition
     * @return a sorted set of all the players that have the given wepoan with the given amount of ammunition for it
     */
    protected SortedSet<Player> getLivePlayersWithWeaponAndAmmunition(Weapon weapon, int ammunition) {
        SortedSet<Player> playersWithGivenAmmo=new TreeSet<>();
        for(Player p: this.livePlayers) {
            if(p.hasWeapon(weapon)&&p.getAmmunitionRoundsForWeapon(weapon)==ammunition) {
                playersWithGivenAmmo.add(p);
            }
        }
        return playersWithGivenAmmo;
    }

    /**
     * Get the set of all monsters that would need to be killed first before you could kill this one.
     * Remember that a protector may itself be protected by other monsters, so you will have to recursively check for protectors
     * @param monster
     * @param room
     * @return
     */
    protected static SortedSet<Monster> getAllProtectorsInRoom(Monster monster, Room room) {
        return getAllProtectorsInRoom(new TreeSet<Monster>(), monster, room); //this is a hint about how to handle canKill as well
    }

    /**
     * @param protectors
     * @param monster
     * @param room
     * @return protectors in room
     */
    private static SortedSet<Monster> getAllProtectorsInRoom(SortedSet<Monster> protectors, Monster monster, Room room) {
        if(monster.getProtectedBy()==null) {
            return protectors;
        }
        else {
            for(Monster m: room.getLiveMonsters()) {
                if(m.getMonsterType().equals(monster.getProtectedBy())) {
                    protectors.add(m);
                    protectors.addAll(getAllProtectorsInRoom(protectors,m,room));
                }
            }
            return protectors;
        }
    }

    /**
     * Can the given player kill the given monster in the given room?
     *
     * @param player
     * @param monster
     * @param room
     * @return
     * @throws IllegalArgumentException if the monster is not located in the room or is dead
     */
    protected static boolean canKill(Player player, Monster monster, Room room) throws IllegalArgumentException {
        //need to deal with throwing the exception
        if(monster.isDead()||!room.getMonsters().contains(monster)) {
            throw new IllegalArgumentException("monster is dead or not in room");
        }
        //Going into the room exposes the player to all the monsters in the room. If the player's health is
        //not > room.getPlayerHealthLostPerEncounter(), you can return immediately.
        if(!(player.getHealth()>room.getPlayerHealthLostPerEncounter())) {
            System.out.println("player health too low to kill monster so can kill returns false");
            return false;
        }
        int originalHealth=player.getHealth();
        //Call the private canKill method, to determine if this player can kill this monster.
        Boolean canKill=canKill(player,monster,room, new TreeMap<Weapon,Integer>(), new TreeSet<Monster>());
        //Before returning from this method, reset the player's health to what it was before you called the private canKill
        player.setHealth(originalHealth);
        return canKill;
    }

    /**
     *
     * @param player
     * @param monster
     * @param room
     * @param roundsUsedPerWeapon
     * @return
     */
    private static boolean canKill(Player player, Monster monster, Room room, SortedMap<Weapon, Integer> roundsUsedPerWeapon, Set<Monster> alreadyMarkedByCanKill) throws IllegalArgumentException {
        System.out.println("canKill has been called on monster: "+monster.getMonsterType());
        if (monster.isDead()) {
            System.out.println("cankill is returning false bc monster was already dead");
            //what do i do here
            return false;
        }
        //checking if was already killed by a recursive call to protector of it and is now being called by the original method
        if(alreadyMarkedByCanKill.contains(monster)) {
            return true;
        }
        SortedSet<Monster>monstersStillAlive=new TreeSet<>(room.getLiveMonsters());
        //checking weapon
        Weapon weaponNeeded=monster.getMonsterType().weaponNeededToKill;
        Weapon weaponToUse=null;
        for(Weapon w: Weapon.values()) {
            if(w.ordinal()>=weaponNeeded.ordinal()&&player.hasWeapon(w)) {
                weaponToUse=w;
                break;
            }
        }
        System.out.println("weapon canKill is using to try to kill the monster: "+weaponToUse);
        if(weaponToUse==null) {
            return false;
        }
        if(monster.getProtectedBy()==null) {
            System.out.println("line 374- hit base case in canKILL");
            monstersStillAlive.removeAll(alreadyMarkedByCanKill);
            //mark amount of ammo needed and check
            int myRoundsUsed=0;
            if(roundsUsedPerWeapon.get(weaponToUse)!=null) {
                myRoundsUsed=roundsUsedPerWeapon.get(weaponToUse);
            }
            roundsUsedPerWeapon.put(weaponToUse,myRoundsUsed+monster.getMonsterType().ammunitionCountNeededToKill);
            System.out.println("rounds needed to kill: "+monster.getMonsterType().ammunitionCountNeededToKill);
            System.out.println("rounds available to kill: "+player.firePower.get(weaponToUse));
            System.out.println("rounds system has totalled to use throu all recursive calls: "+roundsUsedPerWeapon.get(weaponToUse));
            if(roundsUsedPerWeapon.get(weaponToUse)>player.firePower.get(weaponToUse)) {
                System.out.println("can kill thinks the player doesn't have enough ammo to kill the monster");
                return false;
            }
            //deal with health by looping throu live m's and adding health lost and comparing it to player
            int healthLost=0;
            for(Monster m: monstersStillAlive) {
                healthLost+=m.getMonsterType().playerHealthLostPerExposure;
            }
            if(player.getHealth()<healthLost) {
                return false;
            }
            else {
                player.changeHealth(healthLost);
            }
            alreadyMarkedByCanKill.add(monster);
        }
        else {
            SortedSet<Monster> protectors=getAllProtectorsInRoom(monster,room);
            protectors.removeAll(alreadyMarkedByCanKill);
            for(Monster m: protectors) {
                Boolean canItKillProtector=canKill(player,m,room,roundsUsedPerWeapon,alreadyMarkedByCanKill);
                if(!canItKillProtector) {
                    return false;
                }
            }
            monstersStillAlive.removeAll(alreadyMarkedByCanKill);
            //mark amount of ammo needed and check
            int myRoundsUsed=0;
            if(roundsUsedPerWeapon.get(weaponToUse)!=null) {
                myRoundsUsed=roundsUsedPerWeapon.get(weaponToUse);
            }
            roundsUsedPerWeapon.put(weaponToUse,myRoundsUsed+monster.getMonsterType().ammunitionCountNeededToKill);
            System.out.println("rounds needed to kill: "+monster.getMonsterType().ammunitionCountNeededToKill);
            System.out.println("rounds available to kill: "+player.firePower.get(weaponToUse));
            System.out.println("rounds system has totalled to use throu all recursive calls: "+roundsUsedPerWeapon.get(weaponToUse));
            if(roundsUsedPerWeapon.get(weaponToUse)>player.firePower.get(weaponToUse)) {
                return false;
            }
            //deal with health by looping throu live m's and adding health lost and comparing it to player
            int healthLost=0;
            for(Monster m: monstersStillAlive) {
                healthLost+=m.getMonsterType().playerHealthLostPerExposure;
            }
            if(player.getHealth()<healthLost) {
                return false;
            }
            else {
                player.changeHealth(healthLost);
            }
            alreadyMarkedByCanKill.add(monster);
        }
        return true;
        //Remove all the monsters already marked / looked at by this series of recursive calls to canKill from the set of liveMonsters
        // in the room before you check if the monster is alive and in the room. Be sure to NOT alter the actual set of live monsters in your Room object!
        //Check if monster is in the room and alive.
        //Check what weapon is needed to kill the monster, see if player has it. If not, return false.
        //Check what protects the monster. If the monster is protected, the player can only kill this monster if it can kill its protectors as well.
        //Make recursive calls to canKill to see if player can kill its protectors.
        //Be sure to remove all members of alreadyMarkedByCanKill from the set of protectors before you recursively call canKill on the protectors.
        //If all the recursive calls to canKill on all the protectors returned true:
        //Check what amount of ammunition is needed to kill the monster, and see if player has it after we subtract
        //from his total ammunition the number stored in roundsUsedPerWeapon for the given weapon, if any.
        //add how much ammunition will be used up to kill this monster to roundsUsedPerWeapon
        //Add up the playerHealthLostPerExposure of all the live monsters, and see if when that is subtracted from the player if his health is still > 0. If not, return false.
        //If health is still > 0, subtract the above total from the player's health
        //(Note that in the protected canKill method, you must reset the player's health to what it was before canKill was called before you return from that protected method)
        //add this monster to alreadyMarkedByCanKill, and return true.
    }

}
