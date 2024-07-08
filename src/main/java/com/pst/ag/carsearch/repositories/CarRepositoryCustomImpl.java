package com.pst.ag.carsearch.repositories;

import com.pst.ag.carsearch.models.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class CarRepositoryCustomImpl implements CarRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Car> findAllCarsPaginated(Long length, Long weight, Long velocity, String color, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        var query =  getQuery( false, length,   weight,   velocity,   color);

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        var cars =  query.getResultList();

        // Get the total number of items
        Query countQuery = getQuery(true,length,   weight,   velocity,   color);
        Long totalItems = (Long) countQuery.getSingleResult();

        return new PageImpl<>(cars, pageable, totalItems);
    }

    @Override
    public List<Car> findAllCars(Long length, Long weight, Long velocity, String color) {
        var query =  getQuery( false, length,   weight,   velocity,   color);
        return query.getResultList();
    }



    private Query getQuery(boolean isCount, Long length, Long weight, Long velocity, String color){

        String mainSql = "SELECT * FROM car i WHERE 1=1";
        if(isCount){
            mainSql = "SELECT COUNT(i.id) from car i WHERE 1=1";
        }

        StringBuilder sql = new StringBuilder(mainSql);

        if (length != null) {
            sql.append(" AND i.length = :length");
        }
        if (weight != null) {
            sql.append(" AND i.weight = :weight");
        }
        if (velocity != null) {
            sql.append(" AND i.velocity = :velocity");
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND i.color LIKE :color");
        }

        Query query;
        if(!isCount) {
            query = entityManager.createNativeQuery(sql.toString(), Car.class);
        }else{
            query = entityManager.createNativeQuery(sql.toString());
        }

        if (length != null) {
            query.setParameter("length", length);
        }
        if (weight != null) {
            query.setParameter("weight", weight);
        }
        if (velocity != null) {
            query.setParameter("velocity", velocity);
        }
        if (color != null && !color.isEmpty()) {
            query.setParameter("color","%" + color + "%");
        }

        return query;
    }
}
