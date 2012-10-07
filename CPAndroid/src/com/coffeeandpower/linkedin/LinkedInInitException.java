package com.coffeeandpower.linkedin;

import org.scribe.exceptions.OAuthException;

public class LinkedInInitException extends Exception {

    public LinkedInInitException(String msg, OAuthException e) {
       super(msg, e);
    }

    private static final long serialVersionUID = 508884262268754361L;

}
