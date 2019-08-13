package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.osbot.rs07.api.model.RS2Object;

import static org.osbot.rs07.script.MethodProvider.random;
import static org.osbot.rs07.script.MethodProvider.sleep;

import org.osbot.rs07.script.Script;
import utils.Sleep;

import utils.MouseCamera;
import utils.CachedWidget;
import utils.TabHotkey;

public class AntiBan {

    private Script script;

    MouseCamera cam;

    private int minActionBound = 0; // minimum time between actions
    private int maxActionBound = 0; // maximum time between actions
    private long nextExecuteTime;

    private List<Action> defaultActions = Arrays.asList(Action.values());

    List<Action> actionList = new ArrayList<>();

    /**
     * Creates the object with all default actions to be performed.
     *
     * @param script script reference
     * @param minActionBound minimum delay between anti ban actions
     * @param maxActionBound maximum delay between anti ban actions
     */
    public AntiBan(Script script, int minActionBound, int maxActionBound) {
        this(script, minActionBound, maxActionBound, Arrays.asList(Action.values()));

        cam = new MouseCamera(this.script);
    }

    /**
     * Actions must be added manually.
     *
     * @param script script reference
     * @param minActionBound minimum delay between anti ban actions
     * @param maxActionBound maximum delay between anti ban actions
     * @param actions the actions that should be performed
     */
    public AntiBan(Script script, int minActionBound, int maxActionBound, List<Action> actions) {
        this.script = script;
        this.minActionBound = minActionBound;
        this.maxActionBound = maxActionBound;
        nextExecuteTime = System.currentTimeMillis() + random(minActionBound, maxActionBound);
        actionList = actions;

        cam = new MouseCamera(this.script);
    }

    /**
     * @return true if it's time to perform an action
     */
    public boolean shouldExecute() {
        return (System.currentTimeMillis() > nextExecuteTime);
    }

    public long getNextExecuteTime() {
        return nextExecuteTime;
    }

    /**
     * Performs a random action from the action list based off of the weighted
     * values.
     *
     * How it works: Creates an array the size of the cumulative weight
     * of the action list and then loops through the action list assigning each
     * index the value of the enum value. Then chooses a random number
     * 0-arrayLength to get the action to perform.
     */
    public void execute() {
        if (actionList.size() > 0) {
            // calcualte the cumulative weight of the action list
            int cumulativeWeight = 0;
            for (Action action : actionList) {
                cumulativeWeight += action.getWeight();
            }

            // for every action in the action list, add one slot to wheel per weight
            int[] wheel = new int[cumulativeWeight];
            int index = 0; // keep track of last index

            for (int i = 0; i < actionList.size(); i++) {
                int numSlots = actionList.get(i).getWeight();
                while (numSlots-- > 0) {
                    wheel[index++] = actionList.get(i).ordinal();
                }
            }

            // get an action from a random slot
            int actionOrdinal = wheel[random(0, wheel.length)];
            Action action = Action.values()[actionOrdinal];

            // perform the action
            switch (action) {
                case MOVE_MOUSE:
                    script.logger.info("(AntiBan) - Moving mouse");
                    moveMouseRandomly();
                    break;
                case ROTATE_CAMERA:
                    script.logger.info("(AntiBan) - Rotating camera");
                    rotateCameraRandomly();
                    break;
                case RIGHT_CLICK_RANDOM_OBJECT:
                    script.logger.info("(AntiBan) - Right-clicking an object");
                    rightClickRandomObject();
                    break;
                case CHECK_EXP:
                    script.logger.info("(AntiBan) - Checking combat exp");
                    checkExp();
                    break;
                case CHANGE_WORLD:
                    script.logger.info("(AntiBan) - Changing world");
                    hopWorld();
                    break;
                case DISTRACTION:
                    script.logger.info("(AntiBan) - Distraction");
                    distraction();
                    break;
                case HOTKEY_SPAM:
                    script.logger.info("(AntiBan) - Hotkey spamming");
                    hotkeySpam();
                    break;
            }

            // set the next execute time
            nextExecuteTime = System.currentTimeMillis() + random(minActionBound, maxActionBound);
        }
    }

    public void addAction(Action a) {
        actionList.add(a);
    }

    /**
     * @param a the action to add
     * @param weight likeliness of this action to be performed
     */
    public void addAction(Action a, int weight) {
        Action action = a;
        action.setWeight(weight);
        actionList.add(action);
    }

    /**
     * Adds an occurrence of all Actions to the action list
     */
    public void addAllActions() {
        actionList = defaultActions;
    }

    public void clearActions() {
        actionList.clear();
    }

    /**
     * Each action is weighted. Meaning the higher the weight, the more likely
     * that action is to be performed. If two actions have the same weight, they
     * are equally likely to be performed.<br><br>
     *
     *
     * Default weights<br>
     * ------------------------<br>
     * Move mouse: 3<br>
     * Rotate camera: 7<br>
     * Right click object: 1
     */
    public enum Action {
        MOVE_MOUSE(400),
        DISTRACTION(120),
        HOTKEY_SPAM(60),
        ROTATE_CAMERA(250),
        RIGHT_CLICK_RANDOM_OBJECT(80),
        CHANGE_WORLD(15),
        CHECK_EXP(50);

        int weight;

        Action(int weight) {
            this.weight = weight;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    /////////////////////////////
    //     Helper methods      //
    /////////////////////////////
    private void moveMouse() {
        script.getMouse().move(random(43, 538), random(47, 396));
    }

    /////////////////////////////
    //  Methods for execution  //
    /////////////////////////////
    public void hopWorld() {

        int curWorld = script.getWorlds().getCurrentWorld();

        Sleep.sleepUntil(() -> !script.myPlayer().isHitBarVisible() && !script.myPlayer().isUnderAttack() && !script.myPlayer().isMoving() && !script.myPlayer().isAnimating(), 60_000, 1_000);

        try {
            sleep(10_000);
        } catch (Exception e) {

        }

        Sleep.sleepUntil(() -> script.getWorlds().hopToF2PWorld(), 10_000);

        Sleep.sleepUntil(() -> script.getClient().isLoggedIn() && curWorld != script.getWorlds().getCurrentWorld(), 15_000, 1_500);

    }

    public void checkExp() {

        script.getSkills().open();

        CachedWidget attackBlock = new CachedWidget(320, 1);
        CachedWidget strengthBlock = new CachedWidget(320, 2);
        CachedWidget defenceBlock = new CachedWidget(320, 3);
        CachedWidget guideBlock = new CachedWidget(214, 7);

        if (random(1, 100) > 49) {

            // Get type
            int ran = random(1, 3);

            switch (ran) {
                case 1:
                    attackBlock.get(script.getWidgets()).ifPresent(widget -> widget.interact());
                    break;
                case 2:
                    strengthBlock.get(script.getWidgets()).ifPresent(widget -> widget.interact());
                    break;
                case 3:
                    defenceBlock.get(script.getWidgets()).ifPresent(widget -> widget.interact());
                    break;
            }

            Sleep.sleepUntil(() -> guideBlock.get(script.getWidgets()).isPresent(), 4000);

            guideBlock.get(script.getWidgets()).ifPresent(widget -> widget.hover());

            int x = 1;
            while (x <= random(6, 30))
            {

                script.getMouse().scrollDown();
                x++;

            }

        } else {

            guideBlock.get(script.getWidgets()).ifPresent(widget -> widget.hover());

        }


    }

    public void moveMouseRandomly() {
        script.getMouse().move(random(43, 538), random(47, 396));
    }

    public void rotateCameraRandomly() {
        int ran = random(1, 4);

        switch (ran) {
            case 1:
                cam.moveEast();
                break;
            case 2:
                cam.moveNorth();
                break;
            case 3:
                cam.moveSouth();
                break;
            case 4:
                cam.moveWest();
                break;
        }

        int ran2 = random(1, 2);

        switch (ran2) {
            case 1:
                cam.toBottom();
                break;
            case 2:
                cam.toTop();
                break;
        }
    }

    public void hotkeySpam() {
        int ran = random(6, 20);
        int i = 0;

        while (i < ran) {
            TabHotkey[] hotkeys = TabHotkey.values();

            try {
                hotkeys[random(0, hotkeys.length-1)].openTab(script);
                sleep(random(400, 1_000));
            } catch (Exception e) {

            }

            i++;
        }
    }

    public void distraction() {
    int ran = random(2_000, 45_000);

        try {
            script.getMouse().moveOutsideScreen();
            sleep(ran);
        } catch (Exception e) {

        }
    }

    /**
     * Right clicks a random visible object and then moves mouse to close the
     * menu.
     */
    public void rightClickRandomObject() {
        List<RS2Object> visibleObjs = script.getObjects().getAll().stream().filter(o -> o.isVisible()).collect(Collectors.toList());

        // select a random object
        int index = random(0, visibleObjs.size() - 1);
        RS2Object obj = visibleObjs.get(index);

        if (obj != null) {
            // hover the object and right click
            obj.hover();
            script.getMouse().click(true);
            Sleep.sleepUntil(() -> !script.getMenuAPI().isOpen(), 2_000);

            // while the menu is still open, move the mouse to a new location
            while (script.getMenuAPI().isOpen()) {
                moveMouse();
            }
        }
    }

}