/**************************
 * Globals
 * Set of variables allowing the user to initialize the ABM model.
 * These variables are accessible by all agents in the model.
 * By cas220
 **************************/

package models.SimpleFirmModel.parameters;

import simudyne.core.abm.GlobalState;
import simudyne.core.annotations.Constant;
import simudyne.core.annotations.Input;

public class Globals extends GlobalState {

  // Deloitte
  @Constant(name = "Home) Deloitte Agent")
  public int deloitteAgent = 1;

  @Input(name = "Home) Fixed Costs")
  public int deloitteFixedCosts = 25000000;

  @Input(name = "Home) Interest cost")
  public int deloitteInterestCost = 0;

  @Input(name = "Home) Corporate tax rate")
  public double deloitteCorporateTaxRate = 0.2;

  // Consultants
  @Input(name = "Cons) SrConsultants #")
  public long nbSrConsultants = 250;

  @Input(name = "Cons) nbSrConsultant per projectSize")
  public double nbSrCPerProjectSize = 10000000.0;

  @Input(name = "Cons) JrConsultants #")
  public long nbJrConsultants = 5000;

  @Input(name = "Cons) nbJrConsultant per projectSize")
  public double nbJrCPerProjectSize = 5000000.0;

  @Input(name = "eff) Drop in efficiency in %")
  public double inputEfficiency = 0.20;

  public double dropInEfficiency = 1 - inputEfficiency;

  @Input(name = "Quit) Efficiency quitting edge")
  public double effQuittingEdge = 0.6;

  @Input(name = "Quit) Max nbMonths benched edge")
  public int nbDaysBenched = 25;

  @Input(name = "Hire) SrConsultant Unemployment %")
  public double srEmploymentMean = 0.75;

  @Input(name = "Hire) JrConsultant Unemployment %")
  public double jrEmploymentMean = 0.95;

  @Input(name = "Hire) Allowed missed contracts")
  public double allowedMissedContracts = 1;

  @Input(name = "Salary) Jr AvgSalary")
  public int JrSalary = 270;

  @Input(name = "Salary) Sr AvgSalary")
  public int SrSalary = 1040;

  @Input(name = "Revenue) Jr AvgRevenue")
  public int JrRevenue = 1000;

  @Input(name = "Revenue) Sr AvgRevenue")
  public int SrRevenue = 2750;

  // Companies
  @Input(name = "Comp) Companies #")
  public long nbCompanies = 250;

  @Input(name = "Comp) Contracts #")
  public int nbContracts = 10;

  // Market
  @Input(name = "Market) Market Start")
  public double marketStart = 1;

  @Input(name = "Market) Set to constant Growth) ")
  public boolean setMarketConstantGrowth = false;

  @Input(name = "Market) Constant Growth rate) ")
  public double marketGrowth = 0.002; // Yearly 0.02 //This is a month 0.002

  @Input(name = "Market) Upper level probability")
  public double upperLevelMarketProbability = 0.15;

  @Input(name = "Market) Lower level probability")
  public double lowerLevelMarketProbability = 0.50;

  // Hidden Variables
  public boolean hasHiredConsultants = false;
  public int counter = 0;

  // Hidden Functions
  public void incrementCounter() {
    counter++;
  }
}
