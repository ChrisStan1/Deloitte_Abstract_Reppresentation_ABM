package models.SimpleFirmModel.parameters;

import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;

public class Globals extends GlobalState {

  // Deloitte
  @Constant(name = "Home) Deloitte Agent")
  public int deloitteAgent = 1;

  @Input(name = "Home) Fixed Costs")
  public int deloitteFixedCosts = 1000000;

  @Input(name = "Home) Interest cost")
  public int deloitteInterestCost = 100000;

  @Input(name = "Home) Corporate tax rate")
  public double deloitteCorporateTaxRate = 0.2;


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

  @Input(name = "Salary) Jr AvgSalary")
  public int JrSalary = 200;

  @Input(name = "Salary) Sr AvgSalary")
  public int SrSalary = 750;

  @Input(name = "Revenue) Jr AvgSalary")
  public int JrRevenue = 1500;

  @Input(name = "Revenue) Sr AvgSalary")
  public int SrRevenue = 2500;

  // Companies
  @Input(name = "Comp) Companies #")
  public long nbCompanies = 5;

  @Input(name = "Comp) Contracts #")
  public int nbContracts = 5;

  // Market
  @Input(name = "Market) Market Start")
  public double marketStart = 1;

  @Input(name = "Market) Set to constant Growth) ")
  public boolean setMarketConstantGrowth = false;

  @Input(name = "Market) Constant Growth rate) ")
  public double marketGrowth = 0.02;

  // Hidden Variables
  public boolean hasHiredConsultants = false;
  public int counter = 0;

  // Hidden Functions
  public void incrementCounter() {
    counter++;
  }
}
