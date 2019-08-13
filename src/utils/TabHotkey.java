package utils;

import java.awt.event.KeyEvent;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;

/**
 * Tabs and their respective hotkey keycodes
 *
 * @author FrostBug
 */
public enum TabHotkey {

    COMBAT(Tab.ATTACK, 0, 1224),
    SKILLS(Tab.SKILLS, 1, 1224),
    QUEST(Tab.QUEST, 2, 1224),
    INVENTORY(Tab.INVENTORY, 3, 1224),
    EQUIPMENT(Tab.EQUIPMENT, 4, 1224),
    PRAYER(Tab.PRAYER, 5, 1224),
    MAGIC(Tab.MAGIC, 0, 1225),
    CLAN(Tab.CLANCHAT, 1, 1225),
    FRIENDS(Tab.FRIENDS, 2, 1225),
    IGNORE(Tab.IGNORES, 3, 1225),
    SETTINGS(Tab.SETTINGS, 4, 1225),
    EMOTES(Tab.EMOTES, 5, 1225),
    LOGOUT(Tab.LOGOUT, 1, 1226);

    private final Tab tab;
    private final int index;
    private final int register;

    private final static int[] KEYCODES = {
            -1,
            KeyEvent.VK_F1,
            KeyEvent.VK_F2,
            KeyEvent.VK_F3,
            KeyEvent.VK_F4,
            KeyEvent.VK_F5,
            KeyEvent.VK_F6,
            KeyEvent.VK_F7,
            KeyEvent.VK_F8,
            KeyEvent.VK_F9,
            KeyEvent.VK_F10,
            KeyEvent.VK_F11,
            KeyEvent.VK_F12,
            KeyEvent.VK_ESCAPE
    };

    private TabHotkey(Tab tab, int index, int register) {
        this.tab = tab;
        this.index = index;
        this.register = register;
    }

    /**
     * Gets the Tab associated with this hotkey
     * @return Associated Tab
     */
    public Tab getTab() {
        return tab;
    }

    /**
     * Gets the hotkey assigned to this tab
     * @param parent Script context
     * @return The hotkey VK KeyCode, or -1 if no hotkey is assigned
     */
    public int getHotkey(Script parent) {
        int config = parent.getConfigs().get(this.register);
        int kcIndex = (config >> (this.index * 5)) & 0b11111;
        return KEYCODES[kcIndex];
    }

    /**
     * Opens the given tab using the assigned hotkey
     * @param parent Script context
     * @return False if no hotkey is assigned to this tab
     * @throws InterruptedException
     */
    public boolean openTab(Script parent) throws InterruptedException {
        if(!isAssigned(parent)) return false;
        int hkey = getHotkey(parent);
        parent.getKeyboard().pressKey(hkey);
        try {
            MethodProvider.sleep(MethodProvider.random(20, 50));
        } finally {
            parent.getKeyboard().releaseKey(hkey);
        }
        return true;
    }

    /**
     * Has this tab been assigned a hotkey?
     * @param parent Script context
     * @return False if there is no hotkey assigned to this tab
     */
    public boolean isAssigned(Script parent) {
        return getHotkey(parent) != -1;
    }

    /**
     * Gets a TabHotkey by its Tab
     * @param tab The associated Tab
     * @return Null if no match is found
     */
    public static TabHotkey forTab(Tab tab) {
        for(TabHotkey thk : values()) {
            if(thk.getTab() == tab) {
                return thk;
            }
        }
        return null;
    }
}
