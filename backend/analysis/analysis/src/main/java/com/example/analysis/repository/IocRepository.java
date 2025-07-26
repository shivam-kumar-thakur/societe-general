package com.example.analysis.repository;

import com.example.analysis.entity.Ioc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IocRepository extends MongoRepository<Ioc,String> {
    //
}
