package models.consultant;

import java.util.Random;

public class JrConsultant extends SuperConsultant implements Consultant {
  @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(3) + 2;
  }
}
