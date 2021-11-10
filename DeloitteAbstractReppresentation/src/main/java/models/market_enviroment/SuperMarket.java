/**************************
 * SuperMarket
 * Super class for the market
 * By cas220
 **************************/

package models.market_enviroment;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.DefaultClientCompany;
import models.client_contract.DefaultContractGenerationStrategy;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.*;

public abstract class SuperMarket extends Agent<Globals> implements Market {

  // Printed
  @Variable public String name;
  @Variable public double srEmploymentMean = 1.0;
  @Variable public double jrEmploymentMean = 1.0;

  @Variable public long nBClientCompanies = 0;
  @Variable public long nBHomeCompanies = 0;

  // Hidden
  public long agentID;

  // Keeping Track of Basic company Information:
  public Queue<Long> homeCompanyQueue = new LinkedList<>();
  public List<Long> clientCompanyQueue = new ArrayList<>();
  public HashMap<Long, Specialization> compSpecializationMap = new HashMap<>();

  // Updating the probability of a consultant getting hired
  @Override
  public void employmentUpdateRate() {

    updateEmploymentRate(true);
    updateEmploymentRate(false);
  }

  // Randomizing the probability of a consultant getting hired
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
  @Override
  public void homeCompanySetup(Messages.MarketRegistrationHomeCompany msg) {
    if (!homeCompanyQueue.contains(msg.ID)) {
      homeCompanyQueue.add(msg.ID);
      nBHomeCompanies++;
    }
  }

  // Keep Track of the ClientCompanies:
  @Override
  public void clientCompanySetup(Messages.MarketRegistrationClientCompany msg) {
    if (!compSpecializationMap.containsKey(msg.ID)) {
      compSpecializationMap.put(msg.ID, msg.specialization);
      clientCompanyQueue.add(msg.ID);
      nBClientCompanies++;
    }
  }

  // Spawning new client companies, if the market call for it
  @Override
  public void spawnNewClientCompany(int nbSpawns) {

    int inputSimulationsContracts = getGlobals().nbContracts;

    for (int i = 0; i < nbSpawns; i++) {
      spawn(
          DefaultClientCompany.class,
          a -> {
            // Company Characteristics Setup;
            a.name = "Company # " + a.getID();

            // Company Specialization Setup:
            a.compSpecialization = Specialization.generateNewRandomSpecialization();

            // Contract Characteristics Setup
            a.nbSimultaneousContracts = inputSimulationsContracts + new Random().nextInt(5);
            a.contractGenerationStrategy = new DefaultContractGenerationStrategy();

            // Debugging: (Note only showing the first generated Contract)
            a.dbCompSpecialization = a.compSpecialization.toString();

            a.addLink(agentID, Links.ClientCompanyMarketLink.class);
            for (Long homeComp : homeCompanyQueue) {
              a.addLink(homeComp, Links.DeloitteClientLink.class);
            }
          });
      nBClientCompanies++;
    }
  }

  // Method for picking a ClientCompany to quit HomeCompany if the market call for it
  @Override
  public void quitClientCompany(int nbQuits) {
    if (clientCompanyQueue.size() >= 1) {
      for (int i = 0; i < nbQuits; i++) {
        int compQuit = new Random().nextInt(clientCompanyQueue.size());
        send(Messages.MarketClientCompanyQuit.class).to(clientCompanyQueue.get(compQuit));
        clientCompanyQueue.remove(compQuit);
        nBClientCompanies--;
      }
    }
  }

  // Abstract function of the child classes to get the current market value
  abstract void getMarketValue();
}
