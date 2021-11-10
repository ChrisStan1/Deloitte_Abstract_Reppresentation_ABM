/**************************
 * Market interface
 * By cas220
 **************************/

package models.market_enviroment;

import models.SimpleFirmModel.Messages;

public interface Market {

  void employmentUpdateRate();

  void updateEmploymentRate(boolean isSrCons);

  void homeCompanySetup(Messages.MarketRegistrationHomeCompany msg);

  void clientCompanySetup(Messages.MarketRegistrationClientCompany msg);

  void spawnNewClientCompany(int nbSpawns);

  void quitClientCompany(int nbQuits);
}
