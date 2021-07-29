package models.home_company;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;
import simudyne.core.abm.Section;

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
            a.getMessagesOfType(Messages.ContractProposal.class).forEach(a::acceptContract);
          });

  public static Action<Deloitte> contractStep =
      Action.create(Deloitte.class, SuperHomeCompany::stepContract);

    public static Action<Deloitte> terminateContracts =
            Action.create(Deloitte.class, SuperHomeCompany::terminateContract);

    public static Action<Deloitte> hireConsultants =
            Action.create(Deloitte.class, SuperHomeCompany::hireConsultants);
}
