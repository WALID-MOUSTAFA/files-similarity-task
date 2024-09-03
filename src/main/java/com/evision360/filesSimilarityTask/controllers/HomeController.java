package com.evision360.filesSimilarityTask.controllers;


import java.util.Comparator;
import java.util.Map;
import com.evision360.filesSimilarityTask.services.MeasureFileDistanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.io.IOException;
import java.util.TreeMap;

@Controller
public class HomeController {

    @Value("${targetPath}")
    private String targetFile;

    @Value("${poolPath}")
    private String poolPath;

    private final Logger logger;

    public HomeController(MeasureFileDistanceService measureFileDistanceService) {
        logger = LoggerFactory.getLogger(HomeController.class);
    }


    @GetMapping("/")
    public String index(Model model) throws IOException {
        Map<String, Double> distance;
        Map<String, Double> orderedDistance;
        MeasureFileDistanceService measureFileDistanceService;

        logger.info("loading target: {} and pool path:  {}", this.targetFile, this.poolPath);

        measureFileDistanceService = getMeasureFileDistanceService();
        distance = measureFileDistanceService.calculateDistance(targetFile, poolPath);
        orderedDistance = new TreeMap<>(Comparator.reverseOrder());
        orderedDistance.putAll(distance);

        logger.info("distance is: {} ", orderedDistance.toString());

        model.addAttribute("result", orderedDistance);
        return "index";
    }


    @ExceptionHandler({IOException.class})
    public ResponseEntity<Object> handleStudentAlreadyExistsException(IOException exception) {
        exception.printStackTrace();
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body("<div style='width: 50%; margin: 50px auto; padding: 10px; color:white; background-color: red'> error reading file or folder: " +  exception.getMessage() + "</div>");
    }
    
    //dynamically look up the bean because it's a prototype scoped.
    @Lookup
    protected MeasureFileDistanceService getMeasureFileDistanceService() {
        return new MeasureFileDistanceService();
    }

}
