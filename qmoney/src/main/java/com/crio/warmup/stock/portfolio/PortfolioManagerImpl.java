package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.web.client.RestTemplate;
import java.util.Collections;

public class PortfolioManagerImpl implements PortfolioManager {

  RestTemplate restTemplate;
  StockQuotesService stockQuotesService;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  protected PortfolioManagerImpl(RestTemplate restTemplate, StockQuotesService stockQuotesService) {
    this.restTemplate = restTemplate;
    this.stockQuotesService = stockQuotesService;
  }

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuotesService = stockQuotesService;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF

  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate)
      throws StockQuoteServiceException {
    
    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
    List<Candle> quotes = new ArrayList<Candle>();

    for (PortfolioTrade stock: portfolioTrades){
      LocalDate startDate = stock.getPurchaseDate();
      String symbol = stock.getSymbol();
      try{
        quotes = getStockQuote(symbol, startDate, endDate);
      }
      catch (Exception e){
        throw e;
      }

      if (quotes != null){
        // buy price = stock open price on purchase date (start date)
        // sell price = stock close price on end date
        Double buyPrice = quotes.get(0).getOpen();
        Double sellPrice = quotes.get(quotes.size()-1).getClose();
        returns.add(calculateAnnualizedReturns(endDate, stock, buyPrice, sellPrice));
      } 
    }

    Collections.sort(returns, AnnualizedReturn.comparing);
    return returns;
  }

  public List<AnnualizedReturn> calculateAnnualizedReturnParallel(List<PortfolioTrade> portfolioTrades, LocalDate endDate, int numThreads) 
    throws InterruptedException, StockQuoteServiceException {

      List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();
      final ExecutorService pool = Executors.newFixedThreadPool(numThreads);
      List<Future<AnnualizedReturn>> futureReturnsList = new ArrayList<Future<AnnualizedReturn>>();

      for (PortfolioTrade stock: portfolioTrades){
        Callable<AnnualizedReturn> callableTask = () -> {
          return getAnnualizedReturn(stock, endDate);
        };
        Future<AnnualizedReturn> futureReturn = pool.submit(callableTask);
        futureReturnsList.add(futureReturn);
      }

      for (Future<AnnualizedReturn> futureReturn: futureReturnsList){
        try{
          AnnualizedReturn annReturn = futureReturn.get();
          returns.add(annReturn);
        } catch (ExecutionException e){
          throw new StockQuoteServiceException("Error when calling the API", e);
        }
      }  

      Collections.sort(returns, AnnualizedReturn.comparing);
      return returns;
  }


  // Wrapper function for parallel execution
  public AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endDate)
    throws StockQuoteServiceException {
      LocalDate startDate = trade.getPurchaseDate();
      String symbol = trade.getSymbol();
      Double buyPrice, sellPrice = 0.0;
      List<Candle> quotes = new ArrayList<Candle>(); 
    
      try {
        quotes = getStockQuote(symbol, startDate, endDate);
        Collections.sort(quotes, (candle1, candle2) -> { 
          return candle1.getDate().compareTo(candle2.getDate()); 
        });

        buyPrice = quotes.get(0).getOpen();
        sellPrice = quotes.get(quotes.size()-1).getClose();
        endDate = quotes.get(quotes.size()-1).getDate();
      
      } catch (Exception e) {
        throw new StockQuoteServiceException("Error");
      } 

      return calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice);
  } 

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {

      Double days = (double)ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      Double total_num_years = days/365;

      Double totalReturn = (sellPrice - buyPrice)/buyPrice;
      Double annualized_returns = Math.pow(1+ totalReturn, 1/total_num_years) - 1;

      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }


  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.

  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) throws
     StockQuoteServiceException {

    return stockQuotesService.getStockQuote(symbol, from, to); 
  
  }
}
