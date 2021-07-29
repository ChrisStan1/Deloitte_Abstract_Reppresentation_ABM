package models.SimpleFirmModel; /* ABM Initialization By CAS220 */

import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.DefaultClientCompany;
import models.client_contract.DefaultContractGenerationStrategy;
import models.client_contract.DefaultContractVisualization;
import models.consultant.JrConsultant;
import models.consultant.SrConsultant;
import models.home_company.Deloitte;
import models.market.DefaultMarket;
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
        Deloitte.class,
        DefaultClientCompany.class,
        JrConsultant.class,
        SrConsultant.class,
        DefaultContractVisualization.class);

    // Register Links (Messages):
    registerLinkTypes(
        Links.DeloitteClientLink.class,
        Links.DeloitteConsultantLink.class,
        Links.ConsultantLink.class,
        Links.ContractToClient.class);

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
              a.market = new DefaultMarket();
            });

    // SrConsultant
    Group<SrConsultant> srConsultantGroup =
        generateGroup(
            SrConsultant.class,
            getGlobals().nbSrConsultants,
            a -> {
              a.ranking = Ranking.SENIOR;
              a.generateAllowedOverlappedProjects();

              // Todo: Ability to select min of agents in each discipline
              a.specialization = Specialization.generateNewRandomSpecialization();
              // a.specialization = a.assignAgentSpecialization();

              // Debugging
              a.dbAgentSpecialization = a.specialization.toString();
              a.dbAgentStatus = a.ranking.toString();
            });

    // JrConsultant
    Group<JrConsultant> jrConsultantGroup =
        generateGroup(
            JrConsultant.class,
            getGlobals().nbJrConsultants,
            a -> {
              a.ranking = Ranking.JUNIOR;
              a.generateAllowedOverlappedProjects();

              // Todo: Ability to select min of agents in each discipline
              a.specialization = Specialization.generateNewRandomSpecialization();
              // a.specialization = a.assignAgentSpecialization();

              // Debugging
              a.dbAgentSpecialization = a.specialization.toString();
              a.dbAgentStatus = a.ranking.toString();
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

    // Contract Acquisition:
    run(
        DefaultClientCompany.contractProposal,
        Deloitte.contractReview,
        Split.create(
            DefaultClientCompany.contractProposalResponse,
            SrConsultant.consultantRequest,
            JrConsultant.consultantRequest));

    // Step Though Contracts:
    run(Deloitte.contractStep);
    run(
        DefaultContractVisualization
            .stepContract); // Commented out can be used to debug code easier.

    // Terminate Contracts:
    run(
        Deloitte.terminateContracts,
        Split.create(
            DefaultClientCompany.contractCompleted,
            SrConsultant.consultantReleased,
            JrConsultant.consultantReleased));

    // Consultants Quitting:
    // Todo: Make Sure They don't quit when they still have a job:
    // Todo: Make an action of resignation to deloitte to have them removed from the Queue
    run(Split.create(SrConsultant.consultantQuit, JrConsultant.consultantQuit));

    // Hiring More consultants
    run(Deloitte.hireConsultants);
    // Todo: Need to register the new hires!!!

    /*
    if (getGlobals().missingSrAgents > getGlobals().allowedMissedContracts
            || getGlobals().missingJrAgents > getGlobals().allowedMissedContracts) {
        run(Deloitte.hireConsultants);
        run(
                Split.create(SrConsultant.registerWithFirm, JrConsultant.registerWithFirm),
                Deloitte.registerConsultants);
    }
    getGlobals().missingSrAgents = 0;
    getGlobals().missingJrAgents = 0;
     */

    // Iteration Global Updates:
    // Updating market rate for consultants:
    // FIXME: should be more natural not hardcoded defaultMarket
    DefaultMarket.updateEmploymentRate(getGlobals().srEmploymentMean, true);
    DefaultMarket.updateEmploymentRate(getGlobals().jrEmploymentMean, false);
    getDoubleAccumulator("srEmploymentRate").add(DefaultMarket.srEmploymentRate);
    getDoubleAccumulator("jrEmploymentRate").add(DefaultMarket.jrEmploymentRate);
  }
}
