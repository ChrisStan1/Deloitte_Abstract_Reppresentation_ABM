/**************************
 * Links
 * Responsible for setting up the links between agents
 * By cas220
 **************************/

package models.SimpleFirmModel;

import simudyne.core.graph.Link;

public class Links {
  public static class DeloitteClientLink extends Link {}

  public static class DeloitteConsultantLink extends Link {}

  public static class ConsultantLink extends Link {}

  public static class ContractToClient extends Link {}

  public static class DeloitteMarketLink extends Link {}

  public static class ClientCompanyMarketLink extends Link {}
}
