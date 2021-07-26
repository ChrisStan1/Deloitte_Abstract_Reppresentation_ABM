package models.SimpleFirmModel; /* ABM Initialization By CAS220 */

import models.client_company.DefaultClientCompany;
import models.SimpleFirmModel.parameters.Globals;
import models.home_company.Deloitte;
import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.annotations.ModelSettings;

// This Function represents the amount of ticks to progress:
@ModelSettings(macroStep = 100)
public class SimpleFirmModel extends AgentBasedModel<Globals> {

  // ABM Initializer:
  @Override
  public void init() {

    // Accumulators: (MonteCarlo Simulations)
    createLongAccumulator("DelayedContracts");
    createLongAccumulator("TotalRevenue");

    // Register Agents:
    registerAgentTypes(Deloitte.class, DefaultClientCompany.class);

    // Register Links (Messages):
    registerLinkTypes(Links.DeloitteClientLink.class);

    // Debugging Variable:
    createDoubleAccumulator("srEmploymentRate");
    createDoubleAccumulator("jrEmploymentRate");
  }

  @Override
  public void setup() {

    // Deloitte Firm (only Ever going to be 1)
    Group<Deloitte> deloitteGroup =
        generateGroup(
            Deloitte.class,
            getGlobals().deloitteAgent,
            a -> {
              a.name = "Deloitte";
            });

    // SrConsultant

    // JrConsultant

    // Companies List (Constructor)
    Group<DefaultClientCompany> clientCompanyGroup =
        generateGroup(
            DefaultClientCompany.class,
            getGlobals().nbCompanies,
            a -> {
              // Company Characteristics Setup;
              a.name = "Company # " + a.getID();
            });

    /*********************************
     *Setting up Links between Agents
     *********************************/

    // Deloitte - ClientCompany links
    deloitteGroup.fullyConnected(clientCompanyGroup, Links.DeloitteClientLink.class);
    clientCompanyGroup.fullyConnected(deloitteGroup, Links.DeloitteClientLink.class);

    super.setup();
  }

  @Override
  public void step() {
    super.step();
  }
}
