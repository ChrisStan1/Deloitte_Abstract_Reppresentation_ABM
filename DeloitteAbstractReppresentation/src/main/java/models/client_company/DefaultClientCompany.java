package models.client_company;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class DefaultClientCompany extends Agent<Globals> implements ClientCompany {

    /*****
     * Setting Up Agent parameters:
     *****/

    @Variable
    public String name;
}
