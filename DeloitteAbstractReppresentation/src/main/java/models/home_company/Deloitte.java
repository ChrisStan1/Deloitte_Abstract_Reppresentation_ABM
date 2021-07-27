package models.home_company;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

public class Deloitte extends SuperHomeCompany implements HomeCompany {

  /*******************************
   * Action Implementations:
   *******************************/

  public static Action<Deloitte> registerConsultants =
      Action.create(
          Deloitte.class,
          a -> {
            a.getMessagesOfType(Messages.RegistrationMessage.class).forEach(a::consultantSetup);
          });

  public static Action<Deloitte> contractReview =
      Action.create(
          Deloitte.class,
          a -> {
            a.getMessagesOfType(Messages.contractProposal.class).forEach(a::acceptContract);
          });
}
