
package com.crio.warmup.stock.dto;

import java.util.Comparator;

public class TotalReturnsDto {

  private String symbol;
  private Double closingPrice;

  public TotalReturnsDto(String symbol, Double closingPrice) {
    this.symbol = symbol;
    this.closingPrice = closingPrice;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Double getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(Double closingPrice) {
    this.closingPrice = closingPrice;
  }

  public static Comparator<TotalReturnsDto> sorting = new Comparator<TotalReturnsDto>() {
    @Override
    public int compare(TotalReturnsDto t1, TotalReturnsDto t2)
    {
      if (t1.getClosingPrice() < t2.getClosingPrice()) return -1;
      if (t1.getClosingPrice() > t2.getClosingPrice()) return 1;
      return 0;
    }
  };
}
