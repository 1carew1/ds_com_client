package rmi;
/**
 * Copyright (c) 2016 Errigal Inc.
 * <p>
 * This software is the confidential and proprietary information
 * of Errigal, Inc.  You shall not disclose such confidential
 * information and shall use it only in accordance with the
 * license agreement you entered into with Errigal.
 * <p>
 * **************************************************************
 * Created by Colm Carew on 31/10/2016.
 * <p>
 * Created by Colm Carew on 31/10/2016.
 */

/**
 * Created by Colm Carew on 31/10/2016.
 */

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInterface extends Remote {

    public String obtainString(String value) throws RemoteException;

    public ComplextRMIObject createWithId(Long id) throws RemoteException;

}
