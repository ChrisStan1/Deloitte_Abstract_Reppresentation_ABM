/**************************
 * Consultant Interface
 * By cas220
 **************************/

package models.consultant;

import models.SimpleFirmModel.Messages;

public interface Consultant {

  void registerWithFirmMethod();

  void assignConsultant(Messages.ConsultantRequest msg);

  void releaseConsultant(Messages.ConsultantReleased msg);

  void quitConsultant();

  void revenueSalaryMessage();
}
