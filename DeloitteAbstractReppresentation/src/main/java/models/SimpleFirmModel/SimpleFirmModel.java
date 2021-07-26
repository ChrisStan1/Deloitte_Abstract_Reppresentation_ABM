package models.SimpleFirmModel; /* ABM Initialization By CAS220 */

import models.SimpleFirmModel.parameters.ConsultantStatus;
import models.SimpleFirmModel.parameters.Globals;
import models.client_company.DefaultClientCompany;
import models.consultant.JrConsultant;
import models.consultant.SrConsultant;
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
    registerAgentTypes(
        Deloitte.class, DefaultClientCompany.class, JrConsultant.class, SrConsultant.class);

    // Register Links (Messages):
    registerLinkTypes(Links.DeloitteClientLink.class);

    // Debugging Variable:
    createDoubleAccumulator("srEmploymentRate");
    createDoubleAccumulator("jrEmploymentRate");
  }

  // Constructor for Network and Agents:
  @Override
  public void setup() {

    // Deloitte Firm
    Group<Deloitte> deloitteGroup =
        generateGroup(
            Deloitte.class,
            getGlobals().deloitteAgent,
            a -> {
              a.name = "Deloitte";
            });

    // SrConsultant
    Group<SrConsultant> srConsultantGroup =
            generateGroup(
                    SrConsultant.class,
                    getGlobals().nbSrConsultants,
                    a -> {
                      // Todo: Allow the user to specify the ratio between consultants:
                      a.specialization = a.assignAgentSpecialization();
                      a.status = ConsultantStatus.SENIOR;
                      a.generateAllowedOverlappedProjects();

                      // Debugging
                      a.dbAgentSpecialization = a.specialization.toString();
                    });

    // JrConsultant
    Group<JrConsultant> jrConsultantGroup =
            generateGroup(
                    JrConsultant.class,
                    getGlobals().nbJrConsultants,
                    a -> {
                      // Todo: Allow the user to specify the ratio between consultants:
                      a.specialization = a.assignAgentSpecialization();
                      a.generateAllowedOverlappedProjects();

                      // Debugging
                      a.dbAgentSpecialization = a.specialization.toString();
                    });

    // Companies List
    Group<DefaultClientCompany> clientCompanyGroup =
        generateGroup(
            DefaultClientCompany.class,
            getGlobals().nbCompanies,
            a -> {
              // Company Characteristics Setup;
              a.name = "Company # " + a.getID();
            });

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
