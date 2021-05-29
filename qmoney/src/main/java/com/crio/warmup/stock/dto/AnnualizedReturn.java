
package com.crio.warmup.stock.dto;

import java.util.Comparator;

public class AnnualizedReturn {

  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  public static Comparator<AnnualizedReturn> comparing = new Comparator<AnnualizedReturn>(){
    @Override
    public int compare(AnnualizedReturn r1, AnnualizedReturn r2){
      if(r2.getAnnualizedReturn()<r1.getAnnualizedReturn()) return -1;
      else if (r2.getAnnualizedReturn()==r1.getAnnualizedReturn()) return 0;
      return 1;
    }
  };
}
