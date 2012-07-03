package com.coffeeandpower.cont;

public class Listing {

    private String daysPast;
    private String listing;
    private int authorId;
    private String price;
    private int clientId;
    private String clientNickName;

    public Listing(String daysPast, String listing, int authorId, String price,
            int clientId, String clientNickName) {
        this.daysPast = daysPast;
        this.listing = listing;
        this.authorId = authorId;
        this.price = price;
        this.clientId = clientId;
        this.clientNickName = clientNickName;

    }

    public String getDaysPast() {
        return daysPast;
    }

    public void setDaysPast(String daysPast) {
        this.daysPast = daysPast;
    }

    public String getListing() {
        return listing;
    }

    public void setListing(String listing) {
        this.listing = listing;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientNickName() {
        return clientNickName;
    }

    public void setClientNickName(String clientNickName) {
        this.clientNickName = clientNickName;
    }

}
