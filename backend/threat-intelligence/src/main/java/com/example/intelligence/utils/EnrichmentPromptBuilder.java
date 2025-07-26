package com.example.intelligence.utils;


import com.example.intelligence.entity.GeoInfo;
import com.example.intelligence.entity.Ioc;
import com.example.intelligence.entity.ThreatReport;
import com.example.intelligence.entity.utility.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EnrichmentPromptBuilder {

    public String buildPromptFromIoc(Ioc ioc) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a threat intelligence analyst AI.\n");
        sb.append("Given the following full IOC data from a threat feed, provide a concise analysis.\n\n");

        sb.append("==== IOC METADATA ====\n");
        sb.append("🔖 ID: ").append(ioc.getId()).append("\n");
        sb.append("📚 Source: ").append(ioc.getSource()).append("\n");
        sb.append("🔗 Reference: ").append(ioc.getReference()).append("\n");
        sb.append("🧷 URL: ").append(ioc.getUrl()).append("\n");
        sb.append("🌐 Host: ").append(ioc.getHost()).append("\n");
        sb.append("🔗 URL Status: ").append(ioc.getUrl_status()).append("\n");
        sb.append("🗓️ Date Added: ").append(ioc.getDate_added()).append("\n");
        sb.append("📄 Threat Type: ").append(ioc.getThreat()).append("\n");
        sb.append("🧾 Reporter: ").append(ioc.getReporter()).append("\n");
        sb.append("🚫 Larted: ").append(ioc.isLarted()).append("\n");

        sb.append("\n==== TAGS ====\n");
        if (ioc.getTags() != null) {
            for (String tag : ioc.getTags()) {
                sb.append("🔹 ").append(tag).append("\n");
            }
        }

        sb.append("\n==== BLACKLIST ENTRIES ====\n");
        if (ioc.getBlacklists() != null) {
            for (Map.Entry<String, String> entry : ioc.getBlacklists().entrySet()) {
                sb.append("📛 ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        sb.append("\n==== PAYLOADS ====\n");
        if (ioc.getPayloads() != null && !ioc.getPayloads().isEmpty()) {
            int index = 1;
            for (Payload p : ioc.getPayloads()) {
                sb.append("📦 Payload #").append(index++).append(":\n");
                sb.append("   • First Seen: ").append(p.getFirstseen()).append("\n");
                sb.append("   • Filename: ").append(p.getFilename()).append("\n");
                sb.append("   • File Type: ").append(p.getFile_type()).append("\n");
                sb.append("   • Response Size: ").append(p.getResponse_size()).append("\n");
                sb.append("   • MD5: ").append(p.getResponse_md5()).append("\n");
                sb.append("   • SHA256: ").append(p.getResponse_sha256()).append("\n");
                sb.append("   • Download Link: ").append(p.getUrlhaus_download()).append("\n");
                sb.append("   • Signature: ").append(p.getSignature()).append("\n");

                if (p.getVirustotal() != null) {
                    sb.append("   • 🧪 VirusTotal Result: ").append(p.getVirustotal().getResult()).append("\n");
                    sb.append("   • Detection Rate: ").append(p.getVirustotal().getPercent()).append("\n");
                    sb.append("   • VT Link: ").append(p.getVirustotal().getLink()).append("\n");
                }
            }
        }

        sb.append("\n==== GEO INFO ====\n");
        if (ioc.getGeoInfo() != null) {
            GeoInfo geo = ioc.getGeoInfo();
            sb.append("🌍 IP: ").append(geo.getIp()).append("\n");
            sb.append("🏙️ City: ").append(geo.getCity()).append("\n");
            sb.append("🌎 Region: ").append(geo.getRegion()).append(" (").append(geo.getRegionCode()).append(")\n");
            sb.append("🗺️ Country: ").append(geo.getCountryName()).append(" (").append(geo.getCountryCodeIso3()).append(")\n");
            sb.append("🧭 Postal: ").append(geo.getPostal()).append("\n");
            sb.append("🛰️ Latitude: ").append(geo.getLatitude()).append(", Longitude: ").append(geo.getLongitude()).append("\n");
            sb.append("🕒 Timezone: ").append(geo.getTimezone()).append(" (UTC ").append(geo.getUtcOffset()).append(")\n");
            sb.append("🏢 ASN: ").append(geo.getAsn()).append("\n");
            sb.append("🏢 Org: ").append(geo.getOrg()).append("\n");
            sb.append("💱 Currency: ").append(geo.getCurrency()).append(" - ").append(geo.getCurrencyName()).append("\n");
            sb.append("🗣️ Languages: ").append(geo.getLanguages()).append("\n");
            sb.append("👥 Population: ").append(geo.getCountryPopulation()).append("\n");
            sb.append("📏 Area: ").append(geo.getCountryArea()).append(" sq.km\n");
            sb.append("🌐 TLD: ").append(geo.getCountryTld()).append("\n");
            sb.append("📞 Calling Code: ").append(geo.getCountryCallingCode()).append("\n");
            sb.append("🇪🇺 In EU: ").append(geo.isInEu()).append("\n");
        }

        sb.append("\n==== TASK ====\n");
        sb.append("Please analyze the IOC above and respond with a concise threat enrichment.\n");
        sb.append("The output **must** be a valid JSON object with the following required fields:\n\n");
        sb.append("```\n");
        sb.append("{\n");
        sb.append("  \"summary\": \"<1-2 sentence threat summary>\",\n");
        sb.append("  \"recommendActions\": [\"<action1>\", \"<action2>\", ...],\n");
        sb.append("  \"threatDevices\": [\"<device1>\", \"<OS1>\", ...],\n");
        sb.append("  \"malwareFamily\": \"<detected malware family name>\",\n");
        sb.append("  \"confidenceScore\": <0-100 or 'Low'|'Medium'|'High'>,\n");
        sb.append("  \"threatCategory\": \"<e.g., Botnet, Trojan, Phishing>\",\n");
        sb.append("  \"targetSectors\": [\"<sector1>\", \"<sector2>\", ...],\n");
        sb.append("  \"persistenceMechanism\": \"<if known>\",\n");
        sb.append("  \"exploitVectors\": [\"<e.g., CVEs, open ports>\", ...],\n");
        sb.append("  \"relatedCVE\": [\"<CVE-YYYY-NNNN>\", ...]\n");
        sb.append("}\n");
        sb.append("```\n");
        sb.append("\nIf any field is unknown or not applicable, leave it as null or an empty list.\n");


        return sb.toString();
    }

    public String buildPromptFromThreat(ThreatReport threatReport, String rawReport) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a cybersecurity analyst AI. Given the raw vulnerability report below and some metadata, extract and return structured JSON in this format:\n\n");

        prompt.append("""
{
  "title": "<Short, clear title>",
  "summary": "<Brief description of the vulnerability>",
  "howItWorks": "<Technical explanation of how the vulnerability works>",
  "attackerImpact": [
    "<What the attacker can do 1>",
    "<What the attacker can do 2>"
  ],
  "preventionSteps": [
    "<Mitigation step 1>",
    "<Mitigation step 2>"
  ],
  "riskAlert": "<Short high-level alert message (include CVE and severity)>",
  "severity": "<Low | Medium | High | Critical>"
}
""");

        prompt.append("\n🧾 Metadata (from known fields):\n");

        if (threatReport.getCveNumber() != null && !threatReport.getCveNumber().isBlank()) {
            prompt.append("- CVE Number: ").append(threatReport.getCveNumber()).append("\n");
        }

        if (threatReport.getCvssScore() != null) {
            String severity = deriveSeverity(threatReport.getCvssScore());
            prompt.append("- CVSS Score: ").append(threatReport.getCvssScore()).append("\n");
            prompt.append("- Severity: ").append(severity).append("\n");
        }

        prompt.append("\n📜 Raw Report:\n");
        prompt.append(rawReport);

        prompt.append("\n\nPlease return **only** the JSON shown above (no markdown, no explanations, no wrapping in code blocks). ");
        prompt.append("All field names must match exactly. Ensure all numeric values (like 8.0) are well-formed and not just '8.'\n");

        return prompt.toString();
    }


    private String deriveSeverity(Double score) {
        if (score == null) return "Unknown";
        if (score >= 9.0) return "Critical";
        if (score >= 7.0) return "High";
        if (score >= 4.0) return "Medium";
        if (score > 0.0) return "Low";
        return "None";
    }



}
