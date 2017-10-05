package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RootObject
{
  @SerializedName("base")
  public String base;
  @SerializedName("date")
  public String date;
  @SerializedName("rates")
  @Expose
  public Rates rates;
}
