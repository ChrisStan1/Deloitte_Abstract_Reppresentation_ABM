/**************************
 * Deloitte
 * Deloitte Interface
 * By cas220
 **************************/

package models.home_company;

import models.SimpleFirmModel.Messages;
import models.client_contract.ClientContract;

public interface HomeCompany {

  void registerWithMarketMethod();

  void consultantSetup(Messages.RegistrationConsultant msg);

  void acceptContract(Messages.ContractProposal msg);

  void stepContract();

  void contractsToBeTerminated(ClientContract clientContract);

  void terminateContract();

  void hireConsultants();

  void spawnJrConsultant();

  void spawnSrConsultant();

  void calculatePNLEachConsultant(Messages.PNL PNL);

  void netProfit();
}
