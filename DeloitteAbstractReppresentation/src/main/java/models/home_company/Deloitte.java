package models.home_company;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

public class Deloitte extends SuperHomeCompany implements HomeCompany {

  /*******************************
   * Action Implementations:
   *******************************/

  public static Action<Deloitte> registerWithMarket =
      Action.create(Deloitte.class, SuperHomeCompany::registerWithMarketMethod);

  public static Action<Deloitte> registerConsultants =
      Action.create(
          Deloitte.class,
          a -> {
            a.getMessagesOfType(Messages.RegistrationConsultant.class).forEach(a::consultantSetup);
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

  public static Action<Deloitte> profitNloss =
      Action.create(
          Deloitte.class,
          a -> {
            a.getMessagesOfType(Messages.PNL.class).forEach(a::calculatePNLEachConsultant);
            a.netProfit();
          });
}
