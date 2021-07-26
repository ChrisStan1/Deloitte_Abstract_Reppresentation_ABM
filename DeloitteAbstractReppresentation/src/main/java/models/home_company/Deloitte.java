package models.home_company;

import models.SimpleFirmModel.parameters.Globals;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class Deloitte extends Agent<Globals> implements HomeCompany {


    /*******************************
     * Setting Up Agent parameters:
     *******************************/

    @Variable public String name;

}
