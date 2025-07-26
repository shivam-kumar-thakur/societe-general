package com.example.intelligence.dataSource;

import com.example.intelligence.entity.ThreatReport;
import com.example.intelligence.service.rabbitmq.RabbitMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Slf4j
public class TalosIntelligence {

    private final RabbitMQProducer rabbitMQProducer;

    @Value("${vulnerability.talos-intelligence}")
    private String talosIntelligenceUrl;

    public TalosIntelligence(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }


    public void fetchNewVulnerability(){
        try{
            String baseUrl = "https://talosintelligence.com";

            Document doc = Jsoup.connect(baseUrl+"/vulnerability_reports")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get();

            // table rows under the vulnerability report table
            Elements rows = doc.select("#vul-report tbody tr");

            // till where to see, we will use some for now lter we will fix only till a particualr stopper

            int count=0;
            for (Element row : rows) {
                String link = baseUrl + row.attr("data-url");
                Elements tds = row.select("td");
                ThreatReport th=new ThreatReport();
                th.setSource("talosintelligence");
                th.setReportId(tds.get(0).text().trim());
                // skip title we will make easy and show
                th.setReportDate(LocalDate.parse(tds.get(2).text().trim()));
                th.setCveNumber(tds.get(3).text().trim());
                th.setCvssScore(Double.parseDouble(tds.get(4).text().trim()));
                th.setSourceUrl(link);

                // now we will send it to rabbit mq for further processing
                rabbitMQProducer.sendTalosVulnerability(th);

                count++;
                if(count>10) break;

            }

        } catch (IOException e) {
            System.out.println(e);
            log.error("Error in fetching talosintelligience");
        }
    }


    public String talosFullVulnerabilityData(String reportUrl){
        try {

            Document doc = Jsoup.connect(reportUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate")
                    .header("Connection", "keep-alive")
                    .timeout(10_000)
                    .get();

            Element wrapper = doc.selectFirst("div#page_wrapper");

            if (wrapper != null) {
                System.out.println("=== 🔍 Extracted Vulnerability Detail ===");
                return wrapper.text();
            } else {
                System.out.println("❌ Couldn't find the vulnerability content.");
                return null;
            }

        } catch (Exception e) {
            System.err.println("❌ Error while scraping: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
