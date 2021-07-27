package models.SimpleFirmModel;

import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.graph.Message;

public class Messages {

  public static class RegistrationMessage extends Message {
    public Specialization specialization;
    public int overlappedProjects;
    public Ranking ranking;
  }

  public static class contractProposal extends Message {
    public long contId;
    public long contSize;
    public long contDuration;
    public Specialization contSpecialization;
  }

  public static class contractProposalResponse extends Message {
    public boolean isAccepted;

    public long contId;
    public long contSize;
    public long contDuration;
    public Specialization contSpecialization;
  }
}
