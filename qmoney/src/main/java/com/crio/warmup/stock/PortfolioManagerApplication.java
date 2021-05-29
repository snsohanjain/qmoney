package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Read the json file provided in the argument[0]. The file will be available in the classpath.
  //    1. Use #resolveFileFromResources to get actual file from classpath.
  //    2. Extract stock symbols from the json file with ObjectMapper provided by #getObjectMapper.
  //    3. Return the list of all symbols in the same order as provided in json.

  //  Note:
  //  1. There can be few unused imports, you will need to fix them to make the build pass.
  //  2. You can use "./gradlew build" to check if your code builds successfully.

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    
    ObjectMapper mapper = getObjectMapper();
    File file = resolveFileFromResources(args[0]);

    PortfolioTrade[] objects = mapper.readValue(file, PortfolioTrade[].class);

    List<String> list = new ArrayList<String>();
    for (PortfolioTrade obj : objects) {
          list.add(obj.getSymbol());
    }
    
    return list;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

     String valueOfArgument0 = "assessments/trades.json";
     String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/champ121991-ME_QMONEY/qmoney/bin/test/assessments/trades.json";
     String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@26794848";
     String functionNameFromTestFileInStackTrace = "mainReadFile()";
     String lineNumberFromTestFileInStackTrace = "19";

    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  //  Find out the closing price of each stock on the end_date and return the list
  //  of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  //    and deserialize the results in List<Candle>

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    
    ObjectMapper mapper = getObjectMapper();
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] stocks = mapper.readValue(file, PortfolioTrade[].class);
    String endDate = args[1];

    RestTemplate restTemplate = new RestTemplate();

    ArrayList<TotalReturnsDto> data = new ArrayList<TotalReturnsDto>();
    List<String> sortedSymbols = new ArrayList<String>();
 
    for (PortfolioTrade stock: stocks){
      String startDate = stock.getPurchaseDate().toString();
      String symbol = stock.getSymbol();
      String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate + "&endDate=" + endDate + "&token=277efa2a65776a6cd53ee3f4b2b58b7012aa027e";
      
      TiingoCandle[] obj = restTemplate.getForObject(url, TiingoCandle[].class);
      if (obj != null){
        data.add(new TotalReturnsDto(symbol, obj[obj.length-1].getClose()));
      }
      
    }

    Collections.sort(data, TotalReturnsDto.sorting);

    for (TotalReturnsDto s: data){
      sortedSymbols.add(s.getSymbol());
    }

    return sortedSymbols;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {

    File file = resolveFileFromResources(args[0]);
    String endDate = args[1];
    ObjectMapper mapper = getObjectMapper();
    PortfolioTrade[] stocks = mapper.readValue(file, PortfolioTrade[].class);
    
    RestTemplate restTemplate = new RestTemplate();

    List<AnnualizedReturn> returns = new ArrayList<AnnualizedReturn>();

    for (PortfolioTrade stock: stocks){
      String startDate = stock.getPurchaseDate().toString();
      String symbol = stock.getSymbol();
      String url = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?startDate=" + startDate + "&endDate=" + endDate + "&token=277efa2a65776a6cd53ee3f4b2b58b7012aa027e";
      
      TiingoCandle[] obj = restTemplate.getForObject(url, TiingoCandle[].class);
      if (obj != null){
        // buy price = stock open price on purchase date (start date)
        // sell price = stock close price on end date
        Double buyPrice = obj[0].getOpen();
        Double sellPrice = obj[obj.length-1].getClose();
        returns.add(calculateAnnualizedReturns(LocalDate.parse(endDate),stock, buyPrice, sellPrice));
      } 
    }
    Collections.sort(returns, AnnualizedReturn.comparing);
    return returns;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {

      Double days = (double)ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
      Double total_num_years = days/365;

      Double totalReturn = (sellPrice - buyPrice)/buyPrice;
      Double annualized_returns = Math.pow(1+ totalReturn, 1/total_num_years) - 1;

      return new AnnualizedReturn(trade.getSymbol(), annualized_returns, totalReturn);
  }

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();

       RestTemplate restTemplate = new RestTemplate();
       PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);
       PortfolioTrade[] portfolioTrades = objectMapper.readValue(file, PortfolioTrade[].class);
      
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }

  private static String readFileAsString(String filename) throws URISyntaxException, IOException {
    return new String(Files.readAllBytes(resolveFileFromResources(filename).toPath()), "UTF-8");
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainReadFile(args));
    printJsonObject(mainReadQuotes(args));
    printJsonObject(mainCalculateSingleReturn(args));
    printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

