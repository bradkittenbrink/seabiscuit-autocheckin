package com.coffeeandpower.cont;

/**
 * Holder for http responses
 * 
 * @author Desktop1
 * 
 */
public class DataHolder {

    private int handlerCode;
    private int responseCode;
    private String responseMessage;
    private Object object;

    public DataHolder(int handlerCode, String responseMessage, Object object) {
	this.handlerCode = handlerCode;
	this.responseMessage = responseMessage;
	this.object = object;
	this.responseCode = 0;
    }

    public int getHandlerCode() {
	return handlerCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMessage() {
	return responseMessage;
    }

    public Object getObject() {
	return object;
    }

    public void setHandlerCode(int handlerCode) {
	this.handlerCode = handlerCode;
    }

    public void setResponseMessage(String responseMessage) {
	this.responseMessage = responseMessage;
    }

    public void setObject(Object object) {
	this.object = object;
    }

}
