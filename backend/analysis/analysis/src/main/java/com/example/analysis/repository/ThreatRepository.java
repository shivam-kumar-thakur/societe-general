package com.example.analysis.repository;

import com.example.analysis.entity.ThreatReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ThreatRepository extends MongoRepository<ThreatReport,String> {
    //
}
