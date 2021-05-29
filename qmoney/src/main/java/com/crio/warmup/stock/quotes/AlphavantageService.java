
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.crio.warmup.stock.exception.StockQuoteServiceException;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the StockQuoteService interface as per the contracts. Call Alphavantage service
  //  to fetch daily adjusted data for last 20 years.
  //  Refer to documentation here: https://www.alphavantage.co/documentation/
  //  --
  //  The implementation of this functions will be doing following tasks:
  //    1. Build the appropriate url to communicate with third-party.
  //       The url should consider startDate and endDate if it is supported by the provider.
  //    2. Perform third-party communication with the url prepared in step#1
  //    3. Map the response and convert the same to List<Candle>
  //    4. If the provider does not support startDate and endDate, then the implementation
  //       should also filter the dates based on startDate and endDate. Make sure that
  //       result contains the records for for startDate and endDate after filtering.
  //    5. Return a sorted List<Candle> sorted ascending based on Candle#getDate
  // Note:
  // 1. Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  // 2. Run the tests using command below and make sure it passes:
  //    ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  1. Write a method to create appropriate url to call Alphavantage service. The method should
  //     be using configurations provided in the {@link @application.properties}.
  //  2. Use this method in #getStockQuote.

  RestTemplate restTemplate;

  protected AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
  throws StockQuoteServiceException {
    String response = null;
    AlphavantageDailyResponse result = null;
    try {
      response = restTemplate.getForObject(buildUri(symbol), String.class);
      ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      objectMapper.registerModule(new JavaTimeModule());
      result = objectMapper.readValue(response, AlphavantageDailyResponse.class);
      //System.out.println("Response: " + response + "\nResult: " + result);
      if (result.getCandles() == null) throw new StockQuoteServiceException("Cannot get response or it cannot be mapped.");
    } catch (Exception e) {
      //System.out.println("Cannot get response or it cannot be mapped.");
      throw new StockQuoteServiceException("Cannot get response or it cannot be mapped.");
      //return new ArrayList<>();
      //throw e;
    }
      
    Map<LocalDate,AlphavantageCandle> map =  result.getCandles();
    ArrayList<Candle> filteredResult = new ArrayList<Candle>();
    for (Map.Entry<LocalDate, AlphavantageCandle> entry : map.entrySet()){
      LocalDate date = entry.getKey();
      AlphavantageCandle candle = entry.getValue();
      candle.setDate(date);
      if (date.isAfter(from) && date.isBefore(to) || date.isEqual(from) || date.isEqual(to))
          filteredResult.add(candle);
    }
    Collections.reverse(filteredResult);
    return filteredResult;
  }

  protected String buildUri(String symbol) {
    return "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + symbol + "&apikey=OVTLM25R2AM2RTW0";  
  }
}

