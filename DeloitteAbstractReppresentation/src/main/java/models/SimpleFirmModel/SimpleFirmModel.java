package models.SimpleFirmModel; /* ABM Initialization By CAS220 */

import models.SimpleFirmModel.parameters.ConsultantStatus;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.DefaultClientCompany;
import models.client_contract.DefaultContractGenerationStrategy;
import models.consultant.JrConsultant;
import models.consultant.SrConsultant;
import models.home_company.Deloitte;
import simudyne.core.abm.AgentBasedModel;
import simudyne.core.abm.Group;
import simudyne.core.abm.Split;
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
    registerLinkTypes(
        Links.DeloitteClientLink.class,
        Links.DeloitteConsultantLink.class,
        Links.ConsultantLink.class);

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
              a.status = ConsultantStatus.SENIOR;
              a.generateAllowedOverlappedProjects();

              // Todo: Ability to select min of agents in each discipline
              a.specialization = Specialization.generateNewRandomSpecialization();
              //a.specialization = a.assignAgentSpecialization();

              // Debugging
              a.dbAgentSpecialization = a.specialization.toString();
              a.dbAgentStatus = a.status.toString();
            });

    // JrConsultant
    Group<JrConsultant> jrConsultantGroup =
        generateGroup(
            JrConsultant.class,
            getGlobals().nbJrConsultants,
            a -> {
              a.status = ConsultantStatus.JUNIOR;
              a.generateAllowedOverlappedProjects();

              // Todo: Ability to select min of agents in each discipline
              a.specialization = Specialization.generateNewRandomSpecialization();
              //a.specialization = a.assignAgentSpecialization();

              // Debugging
              a.dbAgentSpecialization = a.specialization.toString();
              a.dbAgentStatus = a.status.toString();
            });

    // Companies List
    Group<DefaultClientCompany> clientCompanyGroup =
        generateGroup(
            DefaultClientCompany.class,
            getGlobals().nbCompanies,
            a -> {
              // Company Characteristics Setup;
              a.name = "Company # " + a.getID();
              a.compSpecialization = Specialization.generateNewRandomSpecialization();

              // Contract Characteristics Setup
              a.contractGenerationStrategy = new DefaultContractGenerationStrategy();

              /*
              // Selecting ContractGeneration Strategy;
              // a.newContractStrategy = new DefaultNeedNewContractStrategy();
              a.newContractStrategy = new AlwaysNeedNewContractStrategy();
               */

              // Debugging: (Note only showing the first generated Contract)
              a.dbCompSpecialization = a.compSpecialization.toString();
            });

    // Deloitte - ClientCompany links
    deloitteGroup.fullyConnected(clientCompanyGroup, Links.DeloitteClientLink.class);
    clientCompanyGroup.fullyConnected(deloitteGroup, Links.DeloitteClientLink.class);

    // Deloitte - SrConsultant links
    deloitteGroup.fullyConnected(srConsultantGroup, Links.DeloitteConsultantLink.class);
    srConsultantGroup.fullyConnected(deloitteGroup, Links.DeloitteConsultantLink.class);

    // Deloitte - JrConsultant links
    deloitteGroup.fullyConnected(jrConsultantGroup, Links.DeloitteConsultantLink.class);
    jrConsultantGroup.fullyConnected(deloitteGroup, Links.DeloitteConsultantLink.class);

    // SrConsultants - SrConsultants links
    srConsultantGroup.fullyConnected(srConsultantGroup, Links.ConsultantLink.class);
    jrConsultantGroup.fullyConnected(jrConsultantGroup, Links.ConsultantLink.class);

    // JrConsultants - JrConsultants links
    jrConsultantGroup.fullyConnected(srConsultantGroup, Links.ConsultantLink.class);
    srConsultantGroup.fullyConnected(jrConsultantGroup, Links.ConsultantLink.class);

    super.setup();
  }

  @Override
  public void step() {
    super.step();
    // Setup Step
    if (getContext().getTick() == 0) {
      run(
              Split.create(SrConsultant.registerWithFirm, JrConsultant.registerWithFirm),
              Deloitte.registerConsultants);
    }
  }
}
