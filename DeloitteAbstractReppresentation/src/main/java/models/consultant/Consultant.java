package models.consultant;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;

import java.util.HashMap;

public interface Consultant {

  void registerWithFirmMethod();

  void assignConsultant(Messages.ConsultantRequest msg);

  void releaseConsultant(Messages.ConsultantReleased msg);

  void quitConsultant();

  boolean floatingConsultants();

  void spawnNewConsultant(
      Specialization newSpecialization,
      HashMap<Long, Specialization> consSpecializationMap,
      Ranking ranking,
      long deloitteId);

  void revenueCalibrationNewContract(int agentRevenue);

  void revenueCalibrationContractRelease(int agentRevenue);

  void revenueSalaryMessage();
}
