package com.coffeeandpower.cont;

/**
 * Holder for http responses
 * 
 * @author Desktop1
 * 
 */
public class DataHolder
{

	private int responseCode;
	private String responseMessage;
	private Object object;

	public DataHolder(int responseCode, String responseMessage, Object object)
	{
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
		this.object = object;
	}

	public int getResponseCode()
	{
		return responseCode;
	}

	public String getResponseMessage()
	{
		return responseMessage;
	}

	public Object getObject()
	{
		return object;
	}

	public void setResponseCode(int responseCode)
	{
		this.responseCode = responseCode;
	}

	public void setResponseMessage(String responseMessage)
	{
		this.responseMessage = responseMessage;
	}

	public void setObject(Object object)
	{
		this.object = object;
	}

}
