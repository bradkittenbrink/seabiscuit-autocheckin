package com.coffeeandpower.cont;

import java.util.ArrayList;

public class SkillCategory implements java.lang.Comparable {
    private String name;
    private ArrayList<UserSmart> arrayUsersHereNowWithThisSkill = new ArrayList<UserSmart>();
    
    public SkillCategory(String name) {
        this.setName(name);
    }
    
    public void addUser(UserSmart user) {
        arrayUsersHereNowWithThisSkill.add(user);
    }
    
    static public SkillCategory getOrAddSkillCategory(String name, ArrayList<SkillCategory> arraySkillCategory) {
        for (SkillCategory us : arraySkillCategory) {
            if (us.getName().contentEquals(name)) {
                return us;
            }
        }
        SkillCategory newSkillCat = new SkillCategory(name);
        arraySkillCategory.add(newSkillCat);
        return newSkillCat;
    }
    
    
    static public void addNewUserInTheList(UserSmart user, ArrayList<SkillCategory> arraySkillCategory) {
        String major = user.getMajorJobCategory();
        String minor = user.getMinorJobCategory();
        if (!major.contentEquals("")) {
            SkillCategory skill1 = SkillCategory.getOrAddSkillCategory(major, arraySkillCategory);
            skill1.addUser(user);
        }
        if (!minor.contentEquals("") && !major.contentEquals(minor)) {
            SkillCategory skill2 = SkillCategory.getOrAddSkillCategory(minor, arraySkillCategory);
            skill2.addUser(user);
        }
        
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<UserSmart> getUsers() {
        return arrayUsersHereNowWithThisSkill;
    }
    public int compareTo(Object other) { 
        int nombre1 = ((SkillCategory) other).arrayUsersHereNowWithThisSkill.size(); 
        int nombre2 = this.arrayUsersHereNowWithThisSkill.size(); 
        if (nombre1 > nombre2) {
        	return -1;
        }
        else if(nombre1 == nombre2) {
        	return 0;
        }
        else {
        	return 1; 
        }
     } 

}

