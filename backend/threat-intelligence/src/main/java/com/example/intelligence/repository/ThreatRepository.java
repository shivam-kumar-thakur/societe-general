package com.example.intelligence.repository;

import com.example.intelligence.entity.ThreatReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThreatRepository extends MongoRepository<ThreatReport,String> {
    //
}
