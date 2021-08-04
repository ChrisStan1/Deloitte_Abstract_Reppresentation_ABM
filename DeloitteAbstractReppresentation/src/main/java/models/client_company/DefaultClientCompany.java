package models.client_company;

import models.SimpleFirmModel.Messages;
import models.home_company.Deloitte;
import models.home_company.SuperHomeCompany;
import simudyne.core.abm.Action;

public class DefaultClientCompany extends SuperClientCompany {

  /*******************************
   * Action Implementations:
   *******************************/

  public static Action<DefaultClientCompany> registerWithMarket =
      Action.create(DefaultClientCompany.class, SuperClientCompany::registerWithMarketMethod);

  public static Action<DefaultClientCompany> contractProposal =
      Action.create(DefaultClientCompany.class, SuperClientCompany::generateNewContract);

  public static Action<DefaultClientCompany> contractProposalResponse =
      Action.create(
          DefaultClientCompany.class,
          a -> {
            a.getMessagesOfType(Messages.ContractProposalResponse.class)
                .forEach(a::isContractAccepted);
          });

  public static Action<DefaultClientCompany> contractCompleted =
      Action.create(
          DefaultClientCompany.class,
          a -> {
            a.getMessagesOfType(Messages.ContractProposalResponse.class)
                .forEach(a::isContractAccepted);
          });

  // Todo: Add Contract Limit:
  @Override
  protected boolean reachedContractLimit() {
    return false;
  }


}
