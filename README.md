# Files Similarity Task

## Overview

This application compares a target file with multiple files in a specified directory and calculates a similarity score for each file. The similarity is based on word frequency, following these rules:

- **Exact Match:** If a word in a file exists in the target file with the same frequency, the score for that file is increased by 1.
- **Partial Match:** If a word exists in both the file and the target file but with different frequencies, the score is increased by 0.5.
- **No Match:** If a word in the file does not exist in the target file, the score is decreased by 0.25.

The final score is expressed as a percentage, indicating the overall similarity between the target file and each file in the directory.

## Project Structure

- **Controllers:**
    - `HomeController`: Handles HTTP requests, loads the target file and directory paths, and calls `MeasureFileDistanceService` to calculate similarity scores. The results are ordered by similarity and displayed on the front-end.

- **Services:**
    - `MeasureFileDistanceService`: A prototype-scoped service that calculates the similarity scores between the target file and each file in the directory.

- **Utils:**
    - `IOUtils`: Provides utility methods for file operations, such as reading files and listing files in a directory.


## Requirements

- **Java 17+**
- **Maven 3.6+**

## Setup Instructions
make sure the application.properties has those two keys:
1. **application.properties**
    ```properties
    targetPath=/path/to/your/target/file
    poolPath=/path/to/your/pool/directory/

2. **Clone the Repository:**
   ```bash
   git clone https://github.com/yourusername/files-similarity-task.git
   cd files-similarity-task

3. **Run Application:**
   ``` bash
   mvn spring-boot:run

4. The result will appear in the / path in the browser