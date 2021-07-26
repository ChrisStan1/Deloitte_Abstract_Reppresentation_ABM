package models.consultant;

import java.util.Random;

public class SrConsultant extends SuperConsultant implements Consultant {

  @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(5) + 5;
  }
}
