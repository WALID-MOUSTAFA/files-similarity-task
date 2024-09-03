package com.evision360.filesSimilarityTask.services;

import com.evision360.filesSimilarityTask.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Scope("prototype")
public class MeasureFileDistanceService {
    private final Logger logger;
    private Map<String, Long> targetFileWordFrequencyMap;
    private Map<String, Double> score;


    public MeasureFileDistanceService() {
        logger = LoggerFactory.getLogger(MeasureFileDistanceService.class);
    }


    /**
     * this method is the entry point of the algorithm, the algorithm is very simple, it depends on a simple scoring system.
     * the scoring system consists of 3 parts, if the word in a file exists in the target file with the same frequency,
     * then we increase the score for this file by 1, but if the word exists in different but with different frequency,
     * we have increase the score by 0.5 to differentiate between. but if the word doesn't exist at all in the target file
     * then we decrease the score by a number, that number shouldn't be 1, because absense of a word is not at the same
     * value of extra words in comparison, same for 0.5, we chose the decrement to be 0.25.
     *
     * @param targetPath the path of the target file.
     * @param poolPath   the path of the directory of files.
     * @return           a Map of the result, the key is the file path and the value is the percentage of similarity between the specific file and the target file.
     * @throws IOException if target or directory paths are invalid.
     */
    public Map<String, Double> calculateDistance(String targetPath, String poolPath) throws IOException {
        List<String> poolFiles;
        score = new HashMap<>();
        try {
            poolFiles = IOUtils.getDirectoryFiles(poolPath);
        } catch (IOException e) {
            logger.error("provided path {} is not a valid path", poolPath);
            throw e;
        }
        targetFileWordFrequencyMap = calculateWordFrequencyForFile(targetPath);

        //Note: this can be made concurrent using a thread pool, but for large number of files with large words (~10 Million words) the memory usage will be insane.
        for (String filePath : poolFiles) {
            calculateScore(filePath);
        }

        score.entrySet().forEach(e -> e.setValue(e.getValue() / targetFileWordFrequencyMap.size() * 100));
        return score;
    }


    /**
     * calculates the score for specific file.
     *
     * @param filePath    a valid path of a file to calculate score for.
     * @throws IOException if filePath is invalid path.
     */
    private void calculateScore(String filePath) throws IOException {
        Map<String, Long> frequency = calculateWordFrequencyForFile(filePath);
        frequency.keySet().forEach(word -> {
            if (targetFileWordFrequencyMap.containsKey(word)) {
                if (targetFileWordFrequencyMap.get(word).equals(frequency.get(word))) {
                    score.merge(filePath, 1d, Double::sum);
                } else {
                    score.merge(filePath, 0.5, Double::sum);
                }
            } else {
                score.merge(filePath, 0d, (old, n) -> (old - 0.25) >= 0 ? (old - 0.25) : 0);
            }
       });
    }

    /**
     * calculates the frequency of the words of the file.
     * @param filePath a valid path of a file to calculate word frequency for.
     * @return Map of every word in the file with its number of occurrences.
     * @throws IOException if filePath is invalid.
     */
    private Map<String, Long> calculateWordFrequencyForFile(String filePath) throws IOException {
        Map<String, Long> frequency;
        List<String> words = new ArrayList<>();
        frequency = new ConcurrentHashMap<>();
        try {
          String text = IOUtils.readTrimmedFile(filePath);
            Pattern pattern =  Pattern.compile("\\S+");
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()){
                words.add(matcher.group());
            }

        } catch (IOException e) {
            logger.error("provided file path {} is not valid", filePath);
            throw e;
        }

        //that's a classical case of cpu bound operation, parallel streams would be convenient
        words.stream().parallel().forEach((word) -> frequency.merge(word, 1L, (o, n) -> ++o));
        return frequency;
    }
}
