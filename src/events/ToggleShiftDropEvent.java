package events;

import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.Event;

import utils.Sleep;
import utils.WidgetActionFilter;

public final class ToggleShiftDropEvent extends Event {

    @Override
    public final int execute() throws InterruptedException {
        if (Tab.SETTINGS.isDisabled(getBot())) {
            logger.error("(ToggleShiftDropEvent) - Failed, settings is disabled");
            setFailed();
        } else if (getTabs().getOpen() != Tab.SETTINGS) {
            getTabs().open(Tab.SETTINGS);
        } else if(getShiftDropWidget() == null || !getShiftDropWidget().isVisible()) {
            getGameSettingsWidget().interact();
        } else {
            boolean enabled = getSettings().isShiftDropActive();
            if (getShiftDropWidget().interact()) {
                Sleep.sleepUntil(() -> getSettings().isShiftDropActive() != enabled, 3000);
                logger.info("(ToggleShiftDropEvent) - Finished");
                setFinished();
            }
        }
        return random(100, 200);
    }

	private RS2Widget getShiftDropWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(), new WidgetActionFilter("Toggle shift click to drop"));
    }

	private RS2Widget getGameSettingsWidget() {
        return getWidgets().singleFilter(getWidgets().getAll(), new WidgetActionFilter("Controls"));
    }
}