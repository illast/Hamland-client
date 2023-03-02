package com.mygdx.game.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.GameClient;
import com.mygdx.game.objects.Player;
import com.mygdx.game.objects.Teammate;
import com.mygdx.game.packets.*;
import com.mygdx.game.screens.NicknameScreen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class KryoClient extends Listener {

    public static final int MED_KIT_HP_HEAL_AMOUNT = 3;  //NB! If changing this value also change it on the server!!!

    private final Client client;  // Client object.
    public static boolean isNicknameUnique = false;
    public boolean isToServerConnected = false;
    public static String nickname;
    public static Player player = null;
    public static int serverTimerGameBeginCurrent;
    public static int serverTimerGameBeginStopValue;
    public static Map<String, Teammate> teammates = new HashMap<>();
    public static Map<String, Boolean> teammatesReady = new HashMap<>();
    public static Map<Integer, float[]> enemiesData = new HashMap<>();
    public static Map<String, Boolean>  teammatesShots = new HashMap<>();
    public static Map<Integer, float[]> lootPositions = new HashMap<>();
    public static boolean isGameEnd = false;
    public static String statisticsString;
    public static String inscription = "";

    // Ports to connect on.
//    static int tcpPort = 27960;
//    static int udpPort = 27960;
//    static String ip = "localhost";

    static int udpPort = 8080;
    static int tcpPort = 8081;
    static String ip = "193.40.156.122";

    public KryoClient() {
        client = new Client();  // Create the client.

        // Register all the packets here
        client.getKryo().register(PacketMessage.class);  // Register the packet object.
        client.getKryo().register(PacketCheckPlayerNicknameUnique.class);
        client.getKryo().register(PacketSendPlayerMovement.class);
        client.getKryo().register(PacketUpdatePlayers.class);
        client.getKryo().register(PacketRequestConnectedPlayers.class);
        client.getKryo().register(java.util.ArrayList.class);
        client.getKryo().register(PacketPlayerConnected.class);
        client.getKryo().register(PacketPlayerDisconnected.class);
        client.getKryo().register(PacketUpdateMobsPos.class);
        client.getKryo().register(java.util.HashMap.class);
        client.getKryo().register(float[].class);
        client.getKryo().register(PacketBulletShot.class);
        client.getKryo().register(PacketMobHit.class);
        client.getKryo().register(PacketPlayerHit.class);
        client.getKryo().register(PacketPlayerReady.class);
        client.getKryo().register(PacketGameBeginTimer.class);
        client.getKryo().register(PacketLootSpawn.class);
        client.getKryo().register(PacketLootCollected.class);
        client.getKryo().register(PacketSendStatistics.class);
        client.getKryo().register(PacketGameIsOngoing.class);
    }

    public void setPlayer(Player newPlayer) {
        player = newPlayer;
    }

    public Map<String, Teammate> getTeammates() {
        return teammates;
    }

    public Map<String, Boolean> getTeammatesReady() {
        return teammatesReady;
    }

    public Map<Integer, float[]> getEnemiesData() {
        return enemiesData;
    }

    public Map<String, Boolean> getTeammatesShot() {
        return teammatesShots;
    }

    public Map<Integer, float[]> getLootPositions() {
        return lootPositions;
    }

    public int getServerTimerGameBeginCurrent() {
        return serverTimerGameBeginCurrent;
    }

    public int getServerTimerGameBeginStopValue() {
        return serverTimerGameBeginStopValue;
    }

    public boolean getIsGameEnd() {
        return isGameEnd;
    }

    public String getStatisticsString() {
        return statisticsString;
    }

    /**
     * Connect the client to specified server.
     *
     */
    public void connectToServer() {
        client.start();  // Start the client.

        try {
            client.connect(5000, ip, tcpPort, udpPort);  // 5000 (ms) - connection timeout.
        } catch (IOException e) {
            System.out.println("Could not connect to the server :(");
            e.printStackTrace();
        }

        isToServerConnected = true;

        client.addListener(new KryoClient());  // Add new listener.

        // For debug
        System.out.println("Connection successful!\nI am now connected to the server.\nServer IP is: " + ip);
    }

    public void sendPacketCheckNickname(String playerNickname) {
        PacketCheckPlayerNicknameUnique packetCheckNickname = new PacketCheckPlayerNicknameUnique();
        packetCheckNickname.playerNickname = playerNickname;
        client.sendTCP(packetCheckNickname);
    }

    public void sendPlayerMovementInformation(float posX, float posY, float rotation) {
        PacketSendPlayerMovement packetSendPlayerMovement = new PacketSendPlayerMovement();
        packetSendPlayerMovement.playerNickname = nickname;
        packetSendPlayerMovement.playerCurrentPositionX = posX;
        packetSendPlayerMovement.playerCurrentPositionY = posY;
        packetSendPlayerMovement.playerCurrentRotation = rotation;
        client.sendUDP(packetSendPlayerMovement);
    }

    public void sendPacketRequestAllPlayersConnected() {
        PacketRequestConnectedPlayers packetRequestConnectedPlayers = new PacketRequestConnectedPlayers();
        packetRequestConnectedPlayers.allPlayers = new ArrayList<>();
        client.sendTCP(packetRequestConnectedPlayers);
    }

    public void sendPacketBulletShot() {
        PacketBulletShot packetBulletShot = new PacketBulletShot();
        packetBulletShot.playerWhoShot = nickname;
        client.sendTCP(packetBulletShot);
    }

    public void sendPacketMobHit(int mobId) {
        PacketMobHit packetMobHit = new PacketMobHit();
        packetMobHit.mobId = mobId;
        packetMobHit.playerNickname = nickname;
        client.sendTCP(packetMobHit);
    }

    public void sendPacketPlayerReady(boolean isPlayerReady) {
        PacketPlayerReady packetPlayerReady = new PacketPlayerReady();
        packetPlayerReady.playerNickname = nickname;
        packetPlayerReady.isPlayerReady = isPlayerReady;
        client.sendTCP(packetPlayerReady);
    }

    public void sendPacketLootCollected(int lootPositionIndex, boolean isLootMedkit) {
        PacketLootCollected packetLootCollected = new PacketLootCollected();
        packetLootCollected.playerNickname = nickname;
        packetLootCollected.collectedLootIndex = lootPositionIndex;
        packetLootCollected.isLootMedKit = isLootMedkit;
        client.sendTCP(packetLootCollected);
    }

    // Run this method when client receives any packet from the server.
    public void received(Connection c, Object p) {
        if (p instanceof PacketMessage) {
            // Cast the received packet object to receive its message.
            PacketMessage packet = (PacketMessage) p;

            // Also for debug
            System.out.println("Server reply: " + packet.message);
        }

        // Server response if player's nickname is unique.
        if (p instanceof PacketCheckPlayerNicknameUnique) {
            PacketCheckPlayerNicknameUnique packet = (PacketCheckPlayerNicknameUnique) p;
            if (packet.isNicknameUnique) {
                isNicknameUnique = true;
                nickname = packet.playerNickname;
            }
            if (!packet.isNicknameUnique) {
                inscription = "THIS USERNAME IS ALREADY TAKEN";
                isNicknameUnique = false;
                NicknameScreen.isWindowOpened = false;
            }
        }

        if (p instanceof PacketGameIsOngoing) {
            inscription = "THE GAME HAS ALREADY STARTED";
            NicknameScreen.isWindowOpened = false;
        }

        // Update players' position packet.
        if (p instanceof PacketUpdatePlayers) {
            PacketUpdatePlayers packet = (PacketUpdatePlayers) p;

            // Takes only other player's coordinates.
            if (!packet.playerNickname.equals(nickname)) {
                if (teammates.get(packet.playerNickname) != null) {
                    teammates.get(packet.playerNickname).polygon.setPosition(packet.playerPositionX, packet.playerPositionY);
                    teammates.get(packet.playerNickname).polygon.setRotation(packet.playerRotation);
                }
            }
        }

        // Receive all players that are connected to the server. We use this packet to discover all players.
        if (p instanceof PacketRequestConnectedPlayers) {
            PacketRequestConnectedPlayers packet = (PacketRequestConnectedPlayers) p;

            for (String teammateNickname : packet.allPlayers) {
                if (!teammateNickname.equals(nickname)) {
                    // If players nickname is not the same as this player's nickname (we cannot be a teammate of ourselves :D)
                    addTeammate(teammateNickname);
                }
            }
        }

        // Receive if someone connects to the server after us. We use this packet for others to discover us.
        if (p instanceof PacketPlayerConnected) {
            PacketPlayerConnected packet = (PacketPlayerConnected) p;

            if (!packet.teammateNickname.equals(nickname)) {
                addTeammate(packet.teammateNickname);
            }
        }

        // Receive if someone disconnects from the server.
        if (p instanceof PacketPlayerDisconnected) {
            PacketPlayerDisconnected packet = (PacketPlayerDisconnected) p;
            removeTeammate(packet.disconnectedPlayerNickname);
        }

        // Receive packet that updates all mobs' positions.
        if (p instanceof PacketUpdateMobsPos) {
            PacketUpdateMobsPos packet = (PacketUpdateMobsPos) p;

            // Iterate through updated mobs received from the server.
            for (int mobId : packet.allEnemies.keySet()) {
                float mobPosX = packet.allEnemies.get(mobId)[0];
                float mobPosY = packet.allEnemies.get(mobId)[1];
                float mobType = packet.allEnemies.get(mobId)[2];
                float mobHp = packet.allEnemies.get(mobId)[3];
                if (!enemiesData.containsKey(mobId)) {
                    // If there is no mob with this ID added yet -> create new key and new float array.
                    enemiesData.put(mobId, new float[]{mobPosX, mobPosY, mobType, mobHp});
                }
                else {
                    // If mob is already added -> simply update it's position data.
                    float[] mobNewData = enemiesData.get(mobId);
                    mobNewData[0] = mobPosX;
                    mobNewData[1] = mobPosY;
                    enemiesData.put(mobId, mobNewData);
                }
            }
        }

        // Receive this packet if any teammate has made a shot. One shot bullet = one packet.
        if (p instanceof PacketBulletShot) {
            PacketBulletShot packet = (PacketBulletShot) p;
            teammatesShots.put(packet.playerWhoShot, true);
        }

        // Receive this packet if any teammate has hit any mob.
        if (p instanceof PacketMobHit) {
            PacketMobHit packet = (PacketMobHit) p;

            // Update mob hp data.
            float[] existingMobData = enemiesData.get(packet.mobId);  // Get current mob data.
            float currentHp = existingMobData[3];  // Get hp old hp value.
            existingMobData[3] = --currentHp;  // Decrease hp by 1.
//            System.out.println("Received packet hit: mob hit id is " + packet.mobId + " HP is now " + existingMobData[3]);
        }

        // Receive this packet if any player was hit by a mob.
        if (p instanceof PacketPlayerHit) {
            PacketPlayerHit packet = (PacketPlayerHit) p;

            if (!packet.playerNickname.equals(nickname)) {
                Teammate teammate = teammates.get(packet.playerNickname);
                teammate.setHp(teammate.getHp() - 1);
                teammate.setDamaged(true);
                GameClient.soundDamageTaken.play();
            }

            else {
                player.setHp(player.getHp() - 1);
                player.setDamaged(true);
                GameClient.soundDamageTaken.play();
            }
        }

        // Receive this packet if any of the teammates has pressed the "Ready" button.
        if (p instanceof PacketPlayerReady) {
            PacketPlayerReady packet = (PacketPlayerReady) p;
            teammatesReady.put(packet.playerNickname, packet.isPlayerReady);
        }

        // Receive this packet when Server starts the countdown to begin the game.
        if (p instanceof PacketGameBeginTimer) {
            PacketGameBeginTimer packet = (PacketGameBeginTimer) p;
            serverTimerGameBeginCurrent = packet.timerValueCurrent;
            serverTimerGameBeginStopValue = packet.timerStopValue;
        }

        // Receive this packet if server spawns any loot object.
        if (p instanceof PacketLootSpawn) {
            PacketLootSpawn packet = (PacketLootSpawn) p;
            lootPositions.put(packet.spawnPosIndex, new float[]{packet.spawnCoordinateX, packet.spawnCoordinateY, packet.lootType});

//            System.out.println("Spawning loot of type: " + packet.lootType);
        }

        if (p instanceof PacketLootCollected) {
            PacketLootCollected packet = (PacketLootCollected) p;

            if (packet.isLootMedKit) {
                // If collected loot was a med kit.

                // Heal the teammate.
                teammates.get(packet.playerNickname).setHp(MED_KIT_HP_HEAL_AMOUNT);
                teammates.get(packet.playerNickname).setHealTaken(true);
                GameClient.soundHeal.play();
            }

            else {
                // If collected loot was an ammo.
                teammates.get(packet.playerNickname).setAmmoTaken(true);
                GameClient.soundAmmo.play();
            }

            // Remove the loot from hashmap.
            this.removeLootPosition(packet.collectedLootIndex);
//            System.out.println("Player: " + packet.playerNickname + " has collected loot.");
        }

        if (p instanceof PacketSendStatistics) {
            PacketSendStatistics packet = (PacketSendStatistics) p;
            statisticsString = packet.statisticsString;
            isGameEnd = true;

            System.out.println(statisticsString);
        }
    }

    /**
     * Add teammate to teammates hashmap.
     *
     * @param teammateNickname nickname of the teammate
     */
    public void addTeammate(String teammateNickname) {
        if (!teammates.containsKey(teammateNickname)) {
            teammates.put(teammateNickname, null);
            teammatesReady.put(teammateNickname, false); // By default player is not ready (to play the game).
        }
    }

    /**
     * Remove teammate from player's teammates.
     *
     * @param teammateNickname teammate to remove
     */
    public void removeTeammate(String teammateNickname) {
        teammates.remove(teammateNickname);
        teammatesReady.remove(teammateNickname);
    }

    public void removeLootPosition(int lootIndex) {
        lootPositions.remove(lootIndex);
    }
}
