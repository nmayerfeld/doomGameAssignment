package edu.yu.cs.intro.doomGame;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

public class GameBotDemo {
    public static void main(String[] args) {
        GameBotDemo demo = new GameBotDemo();
        demo.playAndReport(demo.createSinglePlayerSuccess(),"Single Player Success");
        demo.playAndReport(demo.createSinglePlayerFailure(),"Single Player Failure");
        demo.playAndReport(demo.createMultiPlayerSuccess(),"Multi-Player Success");
        demo.playAndReport(demo.createMultiPlayerFailure(),"Multi-Player Failure");
    }

    private void playAndReport(GameBot bot, String gameName){
        System.out.println("******************************************************************************");
        System.out.println("******************************************************************************");
        System.out.println("******************************************************************************");
        System.out.println("******************************************************************************");
        System.out.println("******************************************************************************");
        boolean success = bot.play();
        String message = success ? "was completed successfully" : "could not be completed";
        System.out.println("Game named " + gameName + " " + message);
        for(Room room : bot.getAllRooms()){
            System.out.println("**********************************************");
            System.out.println("Room name: " + room.getName());
            System.out.println("Room completed: " + room.isCompleted());
            System.out.println("State of Monsters in Room:");
            for(Monster m : room.getMonsters()){
                System.out.println(m.getMonsterType().name() + ": is dead - " + m.isDead());
            }
        }
    }

    public GameBot createMultiPlayerFailure(){
        TreeSet<Room> rooms = new TreeSet<>();
        rooms.add(this.createEasyImpsRoom());
        rooms.add(this.createDoubleDemonicRoom());
        rooms.add(this.createFearsomeFoursomeRoom());
        Player player1 = new Player("Player 1",5);
        Player player2 = new Player("Player 2",10);
        player2.addWeapon(Weapon.CHAINSAW);
        player2.addWeapon(Weapon.PISTOL);
        player2.addWeapon(Weapon.SHOTGUN);
        player2.addAmmunition(Weapon.CHAINSAW,1);
        player2.addAmmunition(Weapon.PISTOL,6);

        TreeSet<Player> players = new TreeSet<>();
        players.add(player1);
        players.add(player2);
        return new GameBot(rooms,players);
    }


    public GameBot createSinglePlayerFailure(){
        TreeSet<Room> rooms = new TreeSet<>();
        rooms.add(this.createEasyImpsRoom());
        rooms.add(this.createDoubleDemonicRoom());
        rooms.add(this.createFearsomeFoursomeRoom());
        Player player1 = new Player("Player 1",100);
        TreeSet<Player> players = new TreeSet<>();
        players.add(player1);
        return new GameBot(rooms,players);
    }


    public GameBot createMultiPlayerSuccess(){
        TreeSet<Room> rooms = new TreeSet<>();
        rooms.add(this.createEasyImpsRoom());
        rooms.add(this.createDoubleDemonicRoom());
        rooms.add(this.createFearsomeFoursomeRoom());
        Player player1 = new Player("Player 1",9);
        Player player2 = new Player("Player 2",100);
        player2.addWeapon(Weapon.CHAINSAW);
        player2.addWeapon(Weapon.PISTOL);
        player2.addWeapon(Weapon.SHOTGUN);
        player2.addAmmunition(Weapon.CHAINSAW,2);
        player2.addAmmunition(Weapon.PISTOL,7);
        player2.addAmmunition(Weapon.SHOTGUN,13);

        TreeSet<Player> players = new TreeSet<>();
        players.add(player1);
        players.add(player2);
        return new GameBot(rooms,players);
    }

    public GameBot createSinglePlayerSuccess(){
        TreeSet<Room> rooms = new TreeSet<>();
        rooms.add(this.createEasyImpsRoom());
        rooms.add(this.createDoubleDemonicRoom());
        rooms.add(this.createIDRoom());
        rooms.add(this.createTripleThreatRoom());
        rooms.add(this.createDemonQuadRoom());
        rooms.add(this.createFearsomeFoursomeRoom());
        Player player1 = new Player("Player 1",100);
        TreeSet<Player> players = new TreeSet<>();
        players.add(player1);
        return new GameBot(rooms,players);
    }

    private Room createEasyImpsRoom(){
        Monster imp1 = new Monster(MonsterType.IMP);
        Monster imp2 = new Monster(MonsterType.IMP);
        Monster imp3 = new Monster(MonsterType.IMP);
        TreeSet<Monster> group1 = new TreeSet<>();
        group1.add(imp1);
        group1.add(imp2);
        group1.add(imp3);

        HashSet<Weapon> weapons = new HashSet<>();
        weapons.add(Weapon.CHAINSAW);
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        ammoWonUponCompletion.put(Weapon.CHAINSAW,2);
        return new Room(group1,weapons,ammoWonUponCompletion,10,"Easy Imps");
    }

    private Room createDoubleDemonicRoom(){
        Monster demon1 = new Monster(MonsterType.DEMON);
        Monster demon2 = new Monster(MonsterType.DEMON);
        TreeSet<Monster> group1 = new TreeSet<>();
        group1.add(demon1);
        group1.add(demon2);

        HashSet<Weapon> weapons = new HashSet<>();
        weapons.add(Weapon.PISTOL);
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        ammoWonUponCompletion.put(Weapon.PISTOL,6);
        ammoWonUponCompletion.put(Weapon.CHAINSAW,2);
        return new Room(group1,weapons,ammoWonUponCompletion,0,"Double Demonic");
    }

    private Room createIDRoom(){
        Monster imp1 = new Monster(MonsterType.IMP);
        Monster imp2 = new Monster(MonsterType.IMP);
        Monster imp3 = new Monster(MonsterType.IMP);
        Monster demon = new Monster(MonsterType.DEMON);
        TreeSet<Monster> group1 = new TreeSet<>();
        group1.add(imp1);
        group1.add(imp2);
        group1.add(imp3);
        group1.add(demon);

        HashSet<Weapon> weapons = new HashSet<>();
        weapons.add(Weapon.CHAINSAW);
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        ammoWonUponCompletion.put(Weapon.PISTOL,6);
        return new Room(group1,weapons,ammoWonUponCompletion,0,"I&D");
    }

    private Room createTripleThreatRoom(){
        Monster imp = new Monster(MonsterType.IMP);
        Monster demon = new Monster(MonsterType.DEMON);
        Monster spectre = new Monster(MonsterType.SPECTRE);
        TreeSet<Monster> group = new TreeSet<>();
        group.add(imp);
        group.add(demon);
        group.add(spectre);

        HashSet<Weapon> weapons = new HashSet<>();
        weapons.add(Weapon.SHOTGUN);
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        ammoWonUponCompletion.put(Weapon.PISTOL,6);
        ammoWonUponCompletion.put(Weapon.SHOTGUN,4);
        ammoWonUponCompletion.put(Weapon.CHAINSAW,6);
        return new Room(group,weapons,ammoWonUponCompletion,0,"Triple Threat");
    }

    private Room createDemonQuadRoom(){
        Monster demon = new Monster(MonsterType.DEMON);
        Monster demon1 = new Monster(MonsterType.DEMON);
        Monster demon2 = new Monster(MonsterType.DEMON);
        Monster demon3 = new Monster(MonsterType.DEMON);
        TreeSet<Monster> group = new TreeSet<>();
        group.add(demon);
        group.add(demon1);
        group.add(demon2);
        group.add(demon3);

        HashSet<Weapon> weapons = new HashSet<>();
        weapons.add(Weapon.SHOTGUN);
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        ammoWonUponCompletion.put(Weapon.SHOTGUN,16);
        ammoWonUponCompletion.put(Weapon.CHAINSAW,1);
        return new Room(group,weapons,ammoWonUponCompletion,0,"Demon Quad");
    }


    private Room createFearsomeFoursomeRoom(){
        Monster imp = new Monster(MonsterType.IMP);
        Monster demon = new Monster(MonsterType.DEMON);
        Monster spectre = new Monster(MonsterType.SPECTRE);
        Monster baron = new Monster(MonsterType.BARON_OF_HELL);
        TreeSet<Monster> group = new TreeSet<>();
        group.add(imp);
        group.add(demon);
        group.add(spectre);
        group.add(baron);

        HashSet<Weapon> weapons = new HashSet<>();
        Map<Weapon,Integer> ammoWonUponCompletion = new HashMap<>();
        return new Room(group,weapons,ammoWonUponCompletion,0,"Fearsome Foursome");
    }
}
