package com.example.intelligence.repository;

import com.example.intelligence.entity.Ioc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface IocRepository extends MongoRepository<Ioc,String> {
    //
}
