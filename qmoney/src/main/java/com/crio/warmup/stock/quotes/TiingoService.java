
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.
  
  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  throws StockQuoteServiceException {
    String response = null;
    Candle[] result = null;
    try {
      response = restTemplate.getForObject(buildUri(symbol, from, to), String.class);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      result = objectMapper.readValue(response, TiingoCandle[].class);
      if (result.length == 0) throw new StockQuoteServiceException("Cannot get response or it cannot be mapped.");
    } catch (Exception e) {
      //System.out.println("Cannot get response or it cannot be mapped.");
      throw new StockQuoteServiceException("Cannot get response or it cannot be mapped.");
      //return new ArrayList<>();
    }
    return Arrays.asList(result);
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    if (endDate == null)
      return "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate.toString() + "&token=277efa2a65776a6cd53ee3f4b2b58b7012aa027e";   

    return "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate.toString() + "&endDate=" + endDate.toString() + "&token=277efa2a65776a6cd53ee3f4b2b58b7012aa027e";     
  }


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

}
