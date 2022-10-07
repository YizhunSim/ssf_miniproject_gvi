package vttp2022.ssf.ssf_miniproject.models;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;

@Data
public class Position {
  @NotNull
  private String description;

  @NotNull
  private float score;

  @NotNull
  private double latitude;

  @NotNull
  private double longitude;

  @NotNull
  private String googleMapInitialiseURl;

  public Position(String description, float score, double latitude, double longtitude, String url){
    this.description = description;
    this.score = score;
    this.latitude = latitude;
    this.longitude = longtitude;
    this.googleMapInitialiseURl = url;
  } 


}
