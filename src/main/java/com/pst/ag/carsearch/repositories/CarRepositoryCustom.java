package com.pst.ag.carsearch.repositories;

import com.pst.ag.carsearch.models.Car;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarRepositoryCustom {

    Page<Car> findAllCarsPaginated(Long length, Long weight, Long velocity, String color, int page, int size);

    List<Car> findAllCars(Long length, Long weight, Long velocity, String color);
}
