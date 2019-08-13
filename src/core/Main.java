package core;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.map.Area;

import utils.*;

import utils.paint.MouseCursor;
import utils.paint.MouseTrail;
import utils.paint.Bob;

import events.FixedModeEvent;
import events.DisableAudioEvent;
import events.ToggleRoofsHiddenEvent;
import events.ToggleShiftDropEvent;
import org.osbot.rs07.api.map.constants.Banks;

@ScriptManifest(author = "Poke", info = "The simplest way to train combat.", logo = "http://i.imgur.com/1Jr7gga.png", name = "Simpl'r Combat", version = 1.8)
public class Main extends Script {

    public static Area[] bankList = {

            Banks.AL_KHARID,
            Banks.ARCEUUS_HOUSE,
            Banks.ARDOUGNE_NORTH,
            Banks.ARDOUGNE_SOUTH,
            Banks.CAMELOT,
            Banks.CANIFIS,
            Banks.CASTLE_WARS,
            Banks.CATHERBY,
            Banks.DRAYNOR,
            Banks.DUEL_ARENA,
            Banks.EDGEVILLE,
            Banks.FALADOR_EAST,
            Banks.FALADOR_WEST,
            Banks.GNOME_STRONGHOLD,
            Banks.GRAND_EXCHANGE,
            Banks.HOSIDIUS_HOUSE,
            Banks.LOVAKENGJ_HOUSE,
            Banks.LOVAKITE_MINE,
            // Banks.LUMBRIDGE_LOWER,
            Banks.LUMBRIDGE_UPPER,
            Banks.PEST_CONTROL,
            Banks.PISCARILIUS_HOUSE,
            Banks.SHAYZIEN_HOUSE,
            Banks.TZHAAR,
            Banks.VARROCK_EAST,
            Banks.VARROCK_WEST,
            Banks.YANILLE,
            new Area(2534, 3576, 2537, 3572), //Barbarian Assault Bank
            new Area(3496, 3213, 3499, 3210), //Burgh de Rott Bank
            new Area(2933, 3284, 2936, 3281), //Crafting Guild Bank
            new Area(2618, 3896, 2620, 3893), //Etceteria Bank
            new Area(2661, 3162, 2665, 3160), //Fishing Trawler Bank
            new Area(2584, 3422, 2588, 3418), //Fishing Guild Bank
            new Area(2440, 3489, 2442, 3487).setPlane(1), //Grand Tree West
            new Area(2448, 3482, 2450, 3479).setPlane(1), //Grand Tree South
            new Area(2415, 3803, 2418, 3801), //Jatiszo Bank
            new Area(1610, 3683, 1613, 3680).setPlane(2), //Kingdom of Great Kourend Bank
            new Area(2350, 3163, 2354, 3162), //Lletya Bank
            new Area(2097, 3919, 2102, 3917), //Lunar Isle Bank
            new Area(1508, 3423, 1511, 3419), //Lands End Bank
            new Area(3424, 2892, 3430, 2889), //Nardah Bank
            new Area(2335, 3808, 2337, 3805), //Neitiznot Bank
            new Area(3686, 3471, 3691, 3463), //Port Phasmatys Bank
            new Area(2327, 3690, 2332, 3687), //Piscatoris Bank
            new Area(2849, 2955, 2855, 2953), //Shilo Village Bank
            new Area(1717, 3466, 1722, 3463), //Sandcrabs Bank
            new Area(3305, 3123, 3308, 3119), //Shantay Pass Bank
            new Area(1453, 3859, 1458, 3856), //Sulphur Mine
            new Area(3120, 3124, 3123, 3120), //Tutorial Island Bank
            new Area(2444, 3427, 2446, 3422).setPlane(1), //Tree Gnome Stronghold Bank
            new Area(1802, 3571, 1808, 3571), //Vinery Bank
            new Area(2843, 3544, 2846, 3539), //Warriors Guild Bank
            new Area(1589, 3480, 1593, 3476), //Woodcutting Guild Bank
            new Area(1653, 3613, 1658, 3607), //Zeah Cooking Bank

    };

    public enum killingNPCList {

        CHICKENS("Chicken", 0, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1)}, 1, new String[]{"Raw chicken", "Feather"}, new String[]{"Feather"}, null),
        SEAGULL("Seagull", 0, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1)}, 1, new String[]{"Bones"}, null, null),

        COWS("Cow", 1, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1)}, 10, new String[]{"Cow hide"}, null, null),
        GIANT_RAT("Giant rat", 1, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1)}, 12, new String[]{"Bones"}, null, null),

        FLESH_CRAWLER("Flesh crawler", 2, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1)}, 40, new String[]{"Body rune", "Coins", "Iron ore"}, new String[]{"Body rune", "Coins"}, null),
        HILL_GIANT("Hill Giant", 2, new Area[]{new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1), new Area(1,1,1,1)}, 35, new String[]{"Big bones", "Steel arrow", "Limpwurt root", "Coins", "Body talisman", "Cosmic rune", "Nature rune", "Law rune", "Chaos rune", "Death rune"}, new String[]{"Coins", "Steel arrow", "Cosmic rune", "Nature rune", "Law rune", "Chaos rune", "Death rune"}, null);

        private String name;
        private int path;
        private Area[] npcAreas;
        private int requiredLevel;
        private String[] sellableLoot;
        private String[] stackableLoot;
        private HashMap<String, Integer>[] requiredEquipment;

        killingNPCList(String name, int path, Area[] npcAreas, int requiredLevel, String[] sellableLoot, String[] stackableLoot, HashMap<String, Integer>[] requiredEquipment) {
            this.name = name;
            this.path = path;
            this.npcAreas = npcAreas;
            this.requiredLevel = requiredLevel;
            this.sellableLoot = sellableLoot;
            this.stackableLoot = stackableLoot;
            this.requiredEquipment = requiredEquipment;
        }

        public String getName() {
            return name;
        }

        public int getPath() {
            return path;
        }

        public Area[] getNpcAreas() {
            return npcAreas;
        }

        public int getRequiredLevel() {
            return requiredLevel;
        }

        public String[] getSellableLoot() {
            return sellableLoot;
        }

        public String[] getStackableLoot() {
            return stackableLoot;
        }

        public HashMap<String, Integer>[] getRequiredEquipment() {
            return requiredEquipment;
        }
    }

    private Area combatArea;
    private NPC targetNPC;
    private String targetString = "Target";
    private Integer lootWhen = null;
    private Integer curInventoryAmount = 0;

    private Integer wealth = null;
    private Integer wealthBank = null;
    private Integer wealthNeeded = 50000;
    private String wealthString = "Wealth: calculating...";
    private String[] sellableItems = {"Raw chicken", "Feather", "Cow hide"};
    private String[] stackableItems = {"Feather"};
    private String[] equipmentNeeded = {"Mithril scimitar"};
    private List<Position> lootPos = new ArrayList<Position>();

    private Predicate<NPC> suitableNPC = n ->
            getMap().canReach(n) &&
                    n.getInteracting() == null &&
                    n.getHealthPercent() > 0 &&
                    n.hasAction("Attack") &&
                    combatArea.contains(n) &&
                    !n.isUnderAttack() &&
                    n.getName().matches("Chicken");

    private MouseCamera cam = new MouseCamera(this);
    private BotFiles botFile = new BotFiles();

    private String levelingPath = null;
    private MouseTrail trail = new MouseTrail(60*8, this);
    private MouseCursor cursor = new MouseCursor(12, 2, this);
    private Bob bob;
    private Font titleFont = new Font("Sans-Serif", Font.BOLD, 10);

    private AntiBan antiban = new AntiBan(this, 30_000, 180_000);

    @SuppressWarnings("deprecation")
    @Override
    public void onStart() {
        logger.info( "Started "+getName()+" v"+getVersion());

        Area area = new Area(
                new int[][]{
                        { 3180, 3288 },
                        { 3180, 3270 },
                        { 3192, 3270 },
                        { 3192, 3277 },
                        { 3195, 3277 },
                        { 3195, 3281 },
                        { 3194, 3282 },
                        { 3194, 3283 },
                        { 3192, 3285 },
                        { 3192, 3288 }
                }
        );

        combatArea = area;

        bob = new Bob();
        bob.exchangeContext(bot);

        botFile.exchangeContext(bot);
        botFile.initializeModule();

        targetNPC = getNpcs().closest(n -> n.isInteracting(myPlayer()));

        // Change display mode to fixed
        if (getSettings().getDisplay().isResizableMode()) {

            logger.info("(FixedModeEvent) - Executing");
            FixedModeEvent fixedModeEvent = new FixedModeEvent();
            execute(fixedModeEvent);

        }

        // Make sure audio is off on the account
        if (getConfigs().get(168) != 4 || getConfigs().get(169) != 4 || getConfigs().get(872) != 4) {

            logger.info("(DisableAudioEvent) - Executing");
            DisableAudioEvent audioEvent = new DisableAudioEvent();
            execute(audioEvent);

        }

        // Disable roofs if they are enabled
        if (!getSettings().areRoofsEnabled()) {

            logger.info("(ToggleRoofsHiddenEvent) - Executing");
            ToggleRoofsHiddenEvent toggleRoofsHiddenEvent = new ToggleRoofsHiddenEvent();
            execute(toggleRoofsHiddenEvent);

        }

        // Turn on shift dropping
        if (!getSettings().isShiftDropActive()) {

            logger.info("(ToggleShiftDropEvent) - Executing");
            ToggleShiftDropEvent toggleShiftDropEvent = new ToggleShiftDropEvent();
            execute(toggleShiftDropEvent);

        }
    }

    @Override
    public int onLoop() throws InterruptedException {

        // Version checking
        if (levelingPath == null && botFile.fileExists("cScript-" + myPlayer().getName())) {
            try {
                String accountInfoFile = botFile.readFromFile("cScript-" + myPlayer().getName());

                String[] lines = accountInfoFile.split(System.getProperty("line.separator"));

                for (int i = 0; i < lines.length; i++) {

                    String line = lines[i];

                    String[] lineInfo = line.split(":");
                    String lineIndex = lineInfo[0];
                    String lineValue = lineInfo[1];

                    if (lineIndex.equals("version")) {
                        if (!lineValue.equals("v" + getVersion())) {
                            botFile.deleteFile("cScript-" + myPlayer().getName());
                        }
                    }

                }

            } catch (RuntimeException | IOException e) {

                logger.error("Failed handeling file, error: " + e);
                stop(false);

            }
        }

        // Creating file if it does not exist
        if (levelingPath == null && !botFile.fileExists("cScript-" + myPlayer().getName())) {
            try {
                botFile.writeToFile("cScript-" + myPlayer().getName(), "version:v" + getVersion());
            } catch (IOException e) {
                logger.error("Failed handeling file, error: " + e);
                stop(false);
            }
        }

        // Settings up leveling path
        if (levelingPath == null && botFile.fileExists("cScript-" + myPlayer().getName())) {
            try {
                String accountInfoFile = botFile.readFromFile("cScript-" + myPlayer().getName());

                String[] lines = accountInfoFile.split(System.getProperty("line.separator"));

                for (int i = 0; i < lines.length; i++) {

                    String line = lines[i];

                    String[] lineInfo = line.split(":");
                    String lineIndex = lineInfo[0];
                    String lineValue = lineInfo[1];

                    if (lineIndex.equals("levelingPath")) {
                        levelingPath = lineValue;
                        break;
                    }

                }

                if (levelingPath == null) {

                    levelingPath = "levelingPath:";
                    for (int i = 0; i < killingNPCList.values().length; i++) {
                        List<killingNPCList> currentPathList = new ArrayList<killingNPCList>();
                        for (killingNPCList killingNPC : killingNPCList.values()) {
                            if (killingNPC.getPath() == i) {
                                currentPathList.add(killingNPC);
                                //logger.info("Added: " + killingNPC.getName());
                            }
                        }

                        if (currentPathList.isEmpty()) {
                            break;
                        }

                        killingNPCList randomPathPicked = currentPathList.get(new Random().nextInt(currentPathList.size()));
                        int randomAreaI = new Random().nextInt(randomPathPicked.getNpcAreas().length);
                        Area randomPathArea = randomPathPicked.getNpcAreas()[randomAreaI];
                        //logger.info("Picked(" + i + "): " + randomPathPicked.getName() + " with area ID " + randomAreaI);
                        levelingPath += randomPathPicked.getName() + "_" + randomAreaI + "->";
                        currentPathList.clear();
                    }

                    levelingPath = levelingPath.substring(0, levelingPath.length() - 2);

                    //logger.info("Final leveling path: " + levelingPath);

                    String writeInfo = accountInfoFile + System.getProperty("line.separator") + levelingPath;
                    botFile.writeToFile("cScript-" + myPlayer().getName(), writeInfo);

                }

            } catch (RuntimeException | IOException e) {

                logger.error("Failed handeling file, error: " + e);
                stop(false);

            }
        }

        if (!combatArea.contains(myPosition()) && getDialogues().isPendingContinuation()) {
            getDialogues().clickContinue();
        }

        // Looting sellable items while next equipment is more than what the bot has

        // Get coins and sellable and turn into wealth
        // Save wealth in bank so we don't have to run to it on each startup
        // Calculate how much is needed to upgrade gear
        // Item to inventory watcher, to update wealth
        if (wealth == null) {
            if (wealthNeeded == null) {
                wealthNeeded = 0;
                return 200;
            }
            wealth = 0;
            if (getInventory().contains("Coins")) {
                wealth += getInventory().getItem("Coins").getAmount();
            }

            Item[] inventoryItems = getInventory().getItems();
            for (int ii = 0; ii < inventoryItems.length; ii++) {
                for (int i = 0; i < sellableItems.length; i++) {
                    if (inventoryItems[ii] != null) {
                        if (sellableItems[i].equals(inventoryItems[ii].getName())) {
                            wealth += inventoryItems[ii].getAmount() * getGrandExchange().getOverallPrice(inventoryItems[ii].getId());
                        }
                    }
                }
            }

            // Wealth in bank
            try {

                String accountInfoFile = botFile.readFromFile("cScript-" + myPlayer().getName());

                String[] lines = accountInfoFile.split(System.getProperty("line.separator"));

                for(int i=0; i<lines.length; i++){

                    String line = lines[i];

                    String[] lineInfo = line.split(":");
                    String lineIndex = lineInfo[0];
                    String lineValue = lineInfo[1];

                    if (lineIndex.equals("bankValue")) {
                        wealthBank = Integer.parseInt(lineValue);
                        wealth += Integer.parseInt(lineValue);
                    }

                }

                if (wealthBank == null) {

                    while (!getBank().isOpen()) {
                        getWalking().webWalk(bankList);
                        getBank().open();
                    }

                    if (getBank().contains("Coins")) {
                        wealth += getBank().getItem("Coins").getAmount();
                    }

                    Item[] bankItems = getBank().getItems();
                    for (int ii = 0; ii<bankItems.length; ii++) {
                        for (int i = 0; i<sellableItems.length; i++) {
                            if (bankItems[ii] != null) {
                                if (sellableItems[i].equals(bankItems[ii].getName())) {
                                    wealth += bankItems[ii].getAmount() * getGrandExchange().getOverallPrice(bankItems[ii].getId());
                                }
                            }
                        }
                    }

                    wealthBank = wealth;

                    String writeInfo = accountInfoFile + System.getProperty("line.separator") + "bankValue:" + wealthBank;
                    botFile.writeToFile("cScript-" + myPlayer().getName(), writeInfo);

                    getBank().depositAll();
                    getBank().depositWornItems();

                }

            } catch (RuntimeException | IOException e) {

                logger.error("Failed handeling file, error: " + e);
                stop(false);

            }
            wealthString = "Wealth: "+wealth+"/"+wealthNeeded;
        }

        // Better antiban
        if (antiban.shouldExecute()) {

            logger.info("(AntiBan) - Executing");
            antiban.execute();

        }

        // Get to fighting area
        if (!combatArea.contains(myPosition()) && targetNPC == null) {
            getWalking().webWalk(combatArea.getRandomPosition());
        }

        // If target is more than 6 tiles away we lose target
        if (targetNPC != null && getMap().realDistance(targetNPC) >= 7 || targetNPC != null && targetNPC.getHealthPercent() > 0 && !targetNPC.isInteracting(myPlayer())) {
            targetNPC = null;
        }

        // Make sure the target is attacking us too
        if (targetNPC != null && !targetNPC.isInteracting(myPlayer())) {
            if (targetNPC.isHitBarVisible() && targetNPC.getHealthPercent() == 0) {
                targetString = "Dying";
            }

            if (!lootPos.contains(targetNPC.getPosition())) {
                lootPos.add(targetNPC.getPosition());
            }

            if (lootWhen == null) {
                if (getInventory().getEmptySlotCount() > 5) {
                    lootWhen = random(2, 5);
                } else {
                    lootWhen = getInventory().getEmptySlotCount();
                }
            }
            lootWhen--;

            Sleep.sleepUntil(() ->  !targetNPC.exists() || targetNPC.isInteracting(myPlayer()), 10_000);

            if (targetNPC == null || targetNPC.exists() && !targetNPC.isInteracting(myPlayer())) {
                targetNPC = null;
            }
        }

        // Search for target and attack it
        if (!myPlayer().isAnimating() && myPlayer().getInteracting() == null) {
            // Loot
            if (lootWhen != null && lootWhen < 1) {
                targetNPC = null;
                getTabs().open(Tab.INVENTORY);
                for (Integer i = 0; i<lootPos.size(); i++) {
                    Position curPostToLoot = lootPos.get(i);
                    List<GroundItem> lootItems = null;
                    if (wealth < wealthNeeded) {
                        lootItems = getGroundItems().filter(groundItem -> groundItem.getPosition().equals(curPostToLoot) && Arrays.stream(sellableItems).anyMatch(groundItem.getName()::equals));
                    } else {
                        lootItems = getGroundItems().filter(groundItem -> groundItem.getPosition().equals(curPostToLoot) && Arrays.stream(stackableItems).anyMatch(groundItem.getName()::equals));
                    }
                    for (Integer gi = 0; gi<lootItems.size(); gi++) {
                        GroundItem itemToLoot = lootItems.get(gi);
                        if (itemToLoot == null || !itemToLoot.exists() || !getMap().canReach(itemToLoot) || getInventory().isFull()) {
                            break;
                        }
                        if (!itemToLoot.isOnScreen()) {
                            if (map.distance(itemToLoot.getPosition()) < 7) {
                                cam.toPosition(itemToLoot.getPosition());
                            } else {
                                getWalking().webWalk(itemToLoot.getPosition());
                            }
                        }
                        curInventoryAmount = 0;
                        if  (getInventory().contains(itemToLoot.getName())) {
                            curInventoryAmount = getInventory().getItem(itemToLoot.getName()).getAmount();
                        }
                        int lastSpaceCount = getInventory().getEmptySlotCount();
                        int itemID = itemToLoot.getId();
                        Position itemPos = itemToLoot.getPosition();
                        String itemName = itemToLoot.getName();
                        itemToLoot.interact("Take");

                        Sleep.sleepUntil(() -> (getInventory().contains(itemName) && curInventoryAmount != getInventory().getItem(itemName).getAmount()) || lastSpaceCount != getInventory().getEmptySlotCount(), 60_000, 1_000);

                        if (curInventoryAmount != 0 && curInventoryAmount != getInventory().getItem(itemName).getAmount()) {
                            wealth += getGrandExchange().getOverallPrice(itemID) * (getInventory().getItem(itemName).getAmount() - curInventoryAmount);
                        } else if (lastSpaceCount != getInventory().getEmptySlotCount()) {
                            wealth += getGrandExchange().getOverallPrice(itemID) * getInventory().getItem(itemName).getAmount();
                        }

                        wealthString = "Wealth: " + wealth + "/" + wealthNeeded;

                        cam.toTop();
                    }
                }
                lootPos.clear();
                lootWhen = null;
            }

            // Inventory is full
            if (getInventory().isFull()) {
                targetNPC = null;

                while (!getBank().isOpen()) {
                    getWalking().webWalk(bankList);
                    getBank().open();
                }

                getBank().depositAllExcept(equipmentNeeded);

                wealthBank = wealth;

                return 1_000;
            }

            java.util.List<NPC> npcs = getNpcs().getAll().stream().filter(suitableNPC).collect(Collectors.toList());
            if (!npcs.isEmpty()) {
                npcs.sort(Comparator.<NPC>comparingInt(a -> getMap().realDistance(a)).thenComparingInt(b -> getMap().realDistance(b)));
                targetNPC = npcs.get(0);
                targetString = "Target";
                if (getDialogues().isPendingContinuation()) {
                    getDialogues().clickContinue();
                    Sleep.sleepUntil(() -> !getDialogues().isPendingContinuation(), 3_000, 1_000);
                }
                if (targetNPC.exists() && !targetNPC.isVisible()) {
                    cam.toEntity(targetNPC);
                    Sleep.sleepUntil(() -> targetNPC.isOnScreen(), 5_000);
                    if (!targetNPC.isOnScreen()) {
                        getWalking().webWalk(combatArea.getRandomPosition());
                    }
                }
                if (targetNPC.exists() && targetNPC.interact("Attack")) {
                    Sleep.sleepUntil(() -> !targetNPC.exists() || targetNPC.isUnderAttack(), 10_000, 500);
                    cam.toTop();
                }
            } else {
                targetNPC = null;
                getWalking().webWalk(combatArea.getRandomPosition());
            }
        }
        return 200;
    }

    @Override
    public void onExit() {

        // Save bank value
        if (wealthBank != null && botFile.fileExists("cScript-" + myPlayer().getName())) {
            try {
                String accountInfoFile = botFile.readFromFile("cScript-" + myPlayer().getName());

                String[] lines = accountInfoFile.split(System.getProperty("line.separator"));

                for (int i = 0; i < lines.length; i++) {

                    String line = lines[i];

                    String[] lineInfo = line.split(":");
                    String lineIndex = lineInfo[0];
                    String lineValue = lineInfo[1];

                    if(lineIndex.equals("bankValue")){
                        lines[i] = "";
                    }

                }

                StringBuilder finalStringBuilder= new StringBuilder("");
                for(String s:lines){
                    if(!s.equals("")){
                        finalStringBuilder.append(s).append(System.getProperty("line.separator"));
                    }
                }
                String finalString = finalStringBuilder.toString();

                finalString = finalString + "bankValue:" + wealthBank;

                botFile.writeToFile("cScript-" + myPlayer().getName(), finalString);

            } catch (RuntimeException | IOException e) {

                logger.error("Failed handeling file, error: " + e);
                stop(false);

            }
        }

        log("Thanks for running "+getName()+" v"+getVersion());
    }

    @Override
    public void onPaint(Graphics2D g) {

        // Target info and paint
        g.setFont(titleFont);
        if (wealth < wealthNeeded) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.GREEN);
        }
        g.setBackground(null);
        for (Integer i = 0; i<lootPos.size(); i++) {
            if (lootPos.get(i) != null) {
                bob.drawTile(g, (Position) lootPos.get(i));
            }
        }
        g.setColor(Color.CYAN);
        g.setBackground(new Color(255, 255, 255, 80));
        bob.drawMinimapArea(g, combatArea);

        g.setBackground(new Color(0, 0, 0, 80));
        if (targetNPC == null) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.GREEN);
        }
        if (targetNPC != null) {
            g.drawString("Target: "+targetNPC.getName(), 15, 325);
            g.setColor(Color.RED);
            bob.drawEntity(g, targetNPC, targetString, false, false, false, true, false, false, true);
        } else {
            g.drawString("Target: searching...", 15, 325);
        }

        if (wealthBank == null) {
            g.setColor(Color.YELLOW);
        } else if (wealthNeeded < wealth) {
            g.setColor(Color.GREEN);
        } else {
            g.setColor(Color.RED);
        }
        g.drawString(wealthString, 300, 325);

        // Cursor paint
        trail.paint(g);
        cursor.paint(g);

    }

}