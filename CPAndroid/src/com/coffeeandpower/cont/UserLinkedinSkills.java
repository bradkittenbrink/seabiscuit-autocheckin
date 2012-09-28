package com.coffeeandpower.cont;

import java.util.ArrayList;

public class UserLinkedinSkills {
    private String name;
    private int id; 
    private boolean visible; 
    
    public UserLinkedinSkills(int id, String name, boolean visible) {
        this.name=name;
        this.setId(id);
        this.setVisible(visible);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public int countVisible(ArrayList<UserLinkedinSkills> skills) {
        int count = 0;
        for (UserLinkedinSkills us : skills) {
            if (us.isVisible()) {
                count++;
            }
        }
        return count;
    }
    public String getVisible(ArrayList<UserLinkedinSkills> skills) {
        String visible = "";
        for (UserLinkedinSkills us : skills) {
            if (us.isVisible()) {
                if (!visible.contentEquals("")) {
                    visible += ", ";
                }
                visible += us.getName();
            }
        }
        return visible;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.name;
    }

    
}
