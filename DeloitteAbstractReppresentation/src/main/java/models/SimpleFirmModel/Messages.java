package models.SimpleFirmModel;

import models.SimpleFirmModel.parameters.ConsultantStatus;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.graph.Message;

public class Messages {

  public static class RegistrationMessage extends Message {
    public Specialization specialization;
    public int overlappedProjects;
    public ConsultantStatus isSrConsultant;
  }
}
