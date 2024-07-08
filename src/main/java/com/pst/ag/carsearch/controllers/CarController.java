package com.pst.ag.carsearch.controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.pst.ag.carsearch.models.Car;
import com.pst.ag.carsearch.repositories.CarRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/carsearch")
public class CarController {

    @Autowired
    private CarRepositoryCustom carRepositoryCustom;

    @GetMapping()
    public String get(@RequestParam(name = "length", required = false) Long length,
                      @RequestParam(name = "weight", required = false) Long weight,
                      @RequestParam(name = "velocity",required = false) Long velocity,
                      @RequestParam(name = "color",required = false) String color,
                      @RequestParam(defaultValue = "0") int page,
                      @RequestParam(defaultValue = "10") int size,
                      Model model) {

        var search = Car.builder()
                .length(length)
                .weight(weight)
                .velocity(velocity)
                .color(color)
                .build();

        var cars = carRepositoryCustom.findAllCarsPaginated(length,weight,velocity,color,page,size);

        model.addAttribute("search",search);
        model.addAttribute("cars", cars);
        int totalPages = cars.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("currentPage",page);

        return "index.html";
    }

    @GetMapping("/downloadXml")
    public ResponseEntity<byte[]> downloadXml(@RequestParam(name = "length", required = false) Long length,
                                              @RequestParam(name = "weight", required = false) Long weight,
                                              @RequestParam(name = "velocity",required = false) Long velocity,
                                              @RequestParam(name = "color",required = false) String color) {

        var cars = carRepositoryCustom.findAllCars(length,weight,velocity,color);

        try {
            XmlMapper xmlMapper = new XmlMapper();
            String xmlContent = xmlMapper.writeValueAsString(cars);
            byte[] xmlBytes = xmlContent.getBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.setContentDispositionFormData("attachment", "data.xml");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(xmlBytes);

        } catch (Exception e) {
            throw new RuntimeException("Error converting list to XML: " + e.getMessage(), e);
        }
    }
}
