package models.market_enviroment;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.DefaultClientCompany;
import models.client_contract.DefaultContractGenerationStrategy;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public abstract class SuperMarket extends Agent<Globals> implements Market {

  @Variable public String name;
  @Variable public double srEmploymentMean = 1.0;
  @Variable public double jrEmploymentMean = 1.0;

  public long agentID;

  // Keeping Track of Basic company Information:
  public Queue<Long> homeCompanyQueue = new LinkedList<>();
  public HashMap<Long, Specialization> compSpecializationMap = new HashMap<>();

  @Override
  public void employmentUpdateRate() {

    updateEmploymentRate(true);
    updateEmploymentRate(false);
  }

  @Override
  public void updateEmploymentRate(boolean isSrCons) {

    double deviation = (new Random().nextGaussian() / 100); // Used for %
    if (isSrCons) {
      srEmploymentMean += deviation;
    } else {
      jrEmploymentMean += deviation;
    }
  }

  // Keep Track of the homeCompanies:
  public void homeCompanySetup(Messages.MarketRegistrationHomeCompany msg) {
    if (!homeCompanyQueue.contains(msg.ID)) {
      homeCompanyQueue.add(msg.ID);
    }
  }

  // Keep Track of the ClientCompanies:
  public void clientCompanySetup(Messages.MarketRegistrationClientCompany msg) {
    if (!compSpecializationMap.containsKey(msg.ID)) {
      compSpecializationMap.put(msg.ID, msg.specialization);
    }
  }

  public void spawnNewClientCompany(int nbSpawns) {

    for (int i = 0; i < nbSpawns; i++) {
      spawn(
          DefaultClientCompany.class,
          a -> {
            // Company Characteristics Setup;
            a.name = "Company # " + a.getID();

            // Company Specialization Setup:
            a.compSpecialization = Specialization.generateNewRandomSpecialization();

            // Contract Characteristics Setup
            // Todo: I dont know how to fix this exact problem...
            a.nbSimultaneousContracts = /*getGlobals().nbContracts +*/ new Random().nextInt(5);
            a.contractGenerationStrategy = new DefaultContractGenerationStrategy();

            // Debugging: (Note only showing the first generated Contract)
            a.dbCompSpecialization = a.compSpecialization.toString();

            a.addLink(agentID, Links.ClientCompanyMarketLink.class);
            for (Long homeComp : homeCompanyQueue) {
              a.addLink(homeComp, Links.DeloitteClientLink.class);
            }
          });
    }
  }

  public void quitClientCompany(int i) {
    // Send message No more contracts to be sent:
    // When deloitte send a message contract completion, stop client company...
  }

  abstract void getMarketValue();
}
