package com.techgod.triple.j.trends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import services.MarketData;
import utilities.StockAnalyzer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class TripleJTrendsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripleJTrendsApplication.class, args);
		{


			MarketData marketData = new MarketData();

			String ticker = "DNA"; // Example stock ticker
			LocalDate startDate = LocalDate.now().minusWeeks(2);

			// Fetch stock data
			Map<LocalDate, BigDecimal> closingPrices = marketData.getDailyClosingPrices(ticker);
			Map<LocalDate, BigDecimal> openingPrices = marketData.getDailyOpeningPrices(ticker);
			List<String> newsArticles = marketData.getStockNews(ticker); // Fetch news articles

			if (closingPrices.isEmpty() || openingPrices.isEmpty()) {
				System.err.println("Unable to retrieve stock data. Please check your API configuration or internet connection.");
				return;
			}

			// Create StockAnalyzer instance with news articles
			StockAnalyzer analyzer = new StockAnalyzer(ticker, openingPrices, closingPrices, newsArticles);

			// Print analysis summary
			String analysisSummary = analyzer.printAnalysisSummary(startDate, LocalDate.now());
			System.out.println(analysisSummary);
		}
	}
}
