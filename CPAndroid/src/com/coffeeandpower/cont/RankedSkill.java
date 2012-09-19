package com.coffeeandpower.cont;

public class RankedSkill {
    private String name="";
    private int recs; //times reccomended.  make int?
    private String top=""; //ie their rank, #1,#2, Top 10%, Top 20% etc
    
    /*
     * NOTE: This is set in HttpUtil.getUserResume from the JSON object
     */
    public RankedSkill(String name, int love, String rank) {
        this.name=name;
        this.recs=love;
        this.top=rank;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getRecs() {
        return recs;
    }
    public void setRecs(int recs) {
        this.recs = recs;
    }
    public String getTop() {  //TODO: Clean this part up
        if (top.trim().equalsIgnoreCase("null")) {
            return "";
        } else {
            return top;
                      
        }
    }
    public void setTop(String top) {
        if (top==null) {
            return;
        } 
        if (top.trim().equalsIgnoreCase("null")) {
            return;
        }
        this.top = top;
    }
    
}
