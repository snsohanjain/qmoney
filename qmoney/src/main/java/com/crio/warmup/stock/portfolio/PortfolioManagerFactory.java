package com.crio.warmup.stock.portfolio;

import org.springframework.web.client.RestTemplate;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;

public class PortfolioManagerFactory {

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Remember, pass along the RestTemplate argument that is provided to the new instance.
  @Deprecated
  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {
    return new PortfolioManagerImpl(restTemplate);
  }

  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate, StockQuotesService stockQuotesService) {
    return new PortfolioManagerImpl(restTemplate, stockQuotesService);
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Steps:
  //    1. Create appropriate instance of StoockQuoteService using StockQuoteServiceFactory and then
  //       use the same instance of StockQuoteService to create the instance of PortfolioManager.
  //    2. Mark the earlier constructor of PortfolioManager as @Deprecated.
  //    3. Make sure all of the tests pass by using the gradle command below:
  //       ./gradlew test --tests PortfolioManagerFactory


   public static PortfolioManager getPortfolioManager(String provider,
     RestTemplate restTemplate) {

      StockQuoteServiceFactory instance = StockQuoteServiceFactory.INSTANCE;
      StockQuotesService stockQuotesService = instance.getService(provider, restTemplate);
      return new PortfolioManagerImpl(restTemplate, stockQuotesService);
   }

}
