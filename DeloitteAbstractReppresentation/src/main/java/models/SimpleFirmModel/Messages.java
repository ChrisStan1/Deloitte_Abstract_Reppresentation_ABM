/**************************
 * Messages
 * Responsible to setting up message paths between agents,
 * and the contents of the messages
 * By cas220
 **************************/

package models.SimpleFirmModel;

import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.SuperClientCompany;
import models.client_contract.ClientContract;
import simudyne.core.graph.Message;

public class Messages {

  public static class MarketRegistrationHomeCompany extends Message {
    public long ID;
  }

  public static class MarketRegistrationClientCompany extends Message {
    public Specialization specialization;
    public long ID;
  }

  public static class RegistrationConsultant extends Message {
    public Specialization specialization;
    public int overlappedProjects;
    public Ranking ranking;
  }

  public static class ContractProposal extends Message {
    public long contId;
    public long contSize;
    public long contDuration;
    public Specialization contSpecialization;
    public SuperClientCompany compClient;

    @Override
    public String toString() {
      return "ContractId: "
          + contId
          + " contSize: "
          + contSize
          + " contDuration: "
          + contDuration
          + " contSpecialization: "
          + contSpecialization
          + " compClient: "
          + compClient;
    }
  }

  public static class ContractProposalResponse extends Message {
    public boolean isAccepted;

    public long contId;
    public long contSize;
    public long contDuration;
    public Specialization contSpecialization;

    public ClientContract lastContract;
  }

  public static class ConsultantRequest extends Message {
    public Specialization contSpecialization;
  }

  public static class ConsultantReleased extends Message {
    public Specialization contSpecialization;
  }

  public static class CompletedContract extends Message {
    public long contID;
  }

  public static class PNL extends Message {
    public long revenue;
    public double salary;
  }

  public static class MarketClientCompanyQuit extends Message {}
}
