package com.coffeeandpower.cont;

/**
 * @author Desktop1
 *
 */
public class Work {

    private String title;
    private String company;
    private String startDate;
    private String endDate;
    
    public Work(String title, String company, String startDate, String endDate) {
	this.title = title;
	this.company = company;
	this.startDate = startDate;
	this.endDate = endDate;
    }
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    
    
}
