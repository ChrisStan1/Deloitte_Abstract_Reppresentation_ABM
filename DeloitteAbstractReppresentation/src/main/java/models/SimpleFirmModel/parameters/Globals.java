package models.SimpleFirmModel.parameters;

import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;

public class Globals extends GlobalState {

  // Deloitte
  @Constant(name = "Home) Deloitte Agent")
  public int deloitteAgent = 1;

  // Companies
  @Input(name = "C) Companies #")
  public long nbCompanies = 5;
}
