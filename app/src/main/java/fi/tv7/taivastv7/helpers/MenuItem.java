package fi.tv7.taivastv7.helpers;

public class MenuItem {
    private int menuContainerId = 0;
    private int menuTextId = 0;

    public MenuItem(int menuContainerId, int menuTextId) {
        this.menuContainerId = menuContainerId;
        this.menuTextId = menuTextId;
    }

    public int getMenuContainerId() {
        return menuContainerId;
    }

    public int getMenuTextId() {
        return menuTextId;
    }
}
