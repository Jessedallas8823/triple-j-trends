package api;

import model.StockWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.StockService;
import utilities.StockAnalyzer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchStockSymbols(@RequestParam String query) {
        try {
            List<String> symbols = stockService.searchStockSymbols(query);
            return ResponseEntity.ok(symbols);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeStock(@RequestBody StockWrapper request) {
        try {
            String ticker = request.getSymbols().get(0);
            LocalDate startDate = LocalDate.parse(request.getStartDate());
            String provider = request.getProvider(); // Added field
            String interval = request.getInterval(); // Added field

            Map<LocalDate, BigDecimal> closingPrices = stockService.getDailyClosingPrices(ticker);
            Map<LocalDate, BigDecimal> openingPrices = stockService.getDailyOpeningPrices(ticker);
            List<String> news = stockService.getStockNews(ticker);

            StockAnalyzer analyzer = new StockAnalyzer(ticker, openingPrices, closingPrices, news);
            String summary = analyzer.printAnalysisSummary(startDate, LocalDate.now());

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error analyzing stock.");
        }
    }
}
