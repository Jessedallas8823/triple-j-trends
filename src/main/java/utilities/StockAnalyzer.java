package utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StockAnalyzer {
    private final String tickerSymbol;
    private final Map<LocalDate, BigDecimal> dailyOpeningPrices;
    private final Map<LocalDate, BigDecimal> dailyClosingPrices;
    private final List<String> stockNews; // List to store stock news articles

    // Constructor
    public StockAnalyzer(String tickerSymbol, Map<LocalDate, BigDecimal> dailyOpeningPrices,
                         Map<LocalDate, BigDecimal> dailyClosingPrices, List<String> stockNews) {
        this.tickerSymbol = tickerSymbol;
        this.dailyOpeningPrices = dailyOpeningPrices;
        this.dailyClosingPrices = dailyClosingPrices;
        this.stockNews = stockNews; // Store stock news
    }

    // Updated method to return analysis summary as a String
    public String printAnalysisSummary(LocalDate startDate, LocalDate endDate) {
        StringBuilder summary = new StringBuilder();
        summary.append("Stock Analysis Summary:\n");
        summary.append("Stock Symbol: ").append(tickerSymbol).append("\n");
        summary.append("Start Date: ").append(startDate).append("\n");
        summary.append("End Date: ").append(endDate).append("\n");
        summary.append("Daily Opening and Closing Prices:\n");

        dailyClosingPrices.forEach((date, closingPrice) -> {
            BigDecimal openingPrice = dailyOpeningPrices.getOrDefault(date, BigDecimal.ZERO);
            summary.append("Date: ").append(date)
                    .append(" | Opening Price: ").append(openingPrice)
                    .append(" | Closing Price: ").append(closingPrice).append("\n");
        });

        // Percentage change and recommendation
        BigDecimal percentageChange = calculatePercentageChange();
        summary.append("Percentage Change Over Period: ").append(percentageChange.setScale(2, RoundingMode.HALF_UP)).append("%\n");
        summary.append("Recommendation: ").append(generateRecommendation()).append("\n");

        // Highs and lows
        LocalDate highestDate = dailyClosingPrices.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        LocalDate lowestDate = dailyClosingPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        summary.append("Highest Closing Price: ")
                .append(dailyClosingPrices.getOrDefault(highestDate, BigDecimal.ZERO))
                .append(" on ").append(highestDate).append("\n");

        summary.append("Lowest Closing Price: ")
                .append(dailyClosingPrices.getOrDefault(lowestDate, BigDecimal.ZERO))
                .append(" on ").append(lowestDate).append("\n");

        // Add stock news to the summary
        summary.append("\nLatest News Articles:\n");
        if (stockNews != null && !stockNews.isEmpty()) {
            stockNews.forEach(news -> summary.append("- ").append(news).append("\n"));
        } else {
            summary.append("No news available for this stock.\n");
        }

        return summary.toString(); // Return the summary as a String
    }

    // Helper to calculate percentage change
    private BigDecimal calculatePercentageChange() {
        LocalDate startDate = dailyClosingPrices.keySet().stream().findFirst().orElse(null);
        LocalDate endDate = dailyClosingPrices.keySet().stream().reduce((first, second) -> second).orElse(null);

        if (startDate == null || endDate == null) return BigDecimal.ZERO;

        BigDecimal startPrice = dailyClosingPrices.get(startDate);
        BigDecimal endPrice = dailyClosingPrices.get(endDate);

        if (startPrice == null || endPrice == null) return BigDecimal.ZERO;

        return endPrice.subtract(startPrice)
                .divide(startPrice, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Generate recommendation
    private String generateRecommendation() {
        BigDecimal percentageChange = calculatePercentageChange();

        if (percentageChange.compareTo(BigDecimal.valueOf(5)) > 0) {
            return "Buy (positive trend)";
        } else if (percentageChange.compareTo(BigDecimal.valueOf(-5)) < 0) {
            return "Sell (negative trend)";
        } else {
            return "Hold (neutral trend)";
        }
    }
}
