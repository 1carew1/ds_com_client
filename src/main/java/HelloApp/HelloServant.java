package HelloApp;
/***************************************************************
 * Copyright (c) 2016 Errigal Inc.
 *
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 *
 *************************************************************** */

import org.omg.CORBA.ORB;

/**
 * Created by Colm Carew on 03/11/2016.
 */
public class HelloServant extends HelloPOA {

    private String message = "{someObj:\"JSON\"}";
    private ORB orb;

    @Override
    public String hellomessage() {
        return message;
    }

    @Override
    public void hellomessage(String newHellomessage) {
        message = newHellomessage;
    }

    public void setOrb(ORB orb) {
        this.orb = orb;
    }
}
