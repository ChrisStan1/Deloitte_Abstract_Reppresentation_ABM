package models.SimpleFirmModel.parameters;

import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;

public class Globals extends GlobalState {

  // Deloitte
  @Constant(name = "Home) Deloitte Agent")
  public int deloitteAgent = 1;

  // Consultants
  @Input(name = "Cons) SrConsultants #")
  public long nbSrConsultants = 5;

  @Input(name = "Cons) nbSrConsultant per projectSize")
  public double nbSrCPerProjectSize = 7500000.0;

  @Input(name = "Cons) JrConsultants #")
  public long nbJrConsultants = 10;

  @Input(name = "Cons) nbJrConsultant per projectSize")
  public double nbJrCPerProjectSize = 2500000.0;

  @Input(name = "eff) Drop in efficiency in %")
  public double inputEfficiency = 0.20;

  public double dropInEfficiency = 1 - inputEfficiency;

  @Input(name = "Quit) Efficiency quitting edge")
  public double effQuittingEdge = 0.6;

  @Input(name = "Quit) Max nbMonths benched edge")
  public int nbDaysBenched = 25; // This could become a random parameter for each agent...

  // Todo: // Current Unemployment rate = 4.8.. how to integrate this number...
  @Input(name = "Hire) SrConsultant Unemployment %")
  public double srEmploymentMean = 0.75; // Current Unemployment rate...

  @Input(name = "Hire) JrConsultant Unemployment %")
  public double jrEmploymentMean = 0.95; // Current Unemployment rate...

  @Input(name = "Hire) Allowed missed contracts")
  public double allowedMissedContracts = 1;

  // Companies
  @Input(name = "C) Companies #")
  public long nbCompanies = 5;

}
