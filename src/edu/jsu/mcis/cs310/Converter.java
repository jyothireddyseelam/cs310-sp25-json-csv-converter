package edu.jsu.mcis.cs310;


import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
             CSVReader reader = new CSVReader(new StringReader(csvString));
            List<String[]> allRows = reader.readAll();

            if (allRows.isEmpty()) {
                return result;
            }

            // Extract column headings
            String[] colHeadings = allRows.get(0);
            JsonArray headingsArray = new JsonArray();
            Collections.addAll(headingsArray, colHeadings);

            JsonArray prodNumsArray = new JsonArray();
            JsonArray dataArray = new JsonArray();

            // Process each row
            for (int i = 1; i < allRows.size(); i++) {
                String[] row = allRows.get(i);

                prodNumsArray.add(row[0]); // First column is ProdNum

                JsonArray rowArray = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    // Try to parse integers; otherwise, keep as strings
                    try {
                        rowArray.add(Integer.parseInt(row[j]));
                    } catch (NumberFormatException e) {
                        rowArray.add(row[j]);
                    }
                }
                dataArray.add(rowArray);
            }

            // Create final JSON object
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("ProdNums", prodNumsArray);
            jsonObject.put("ColHeadings", headingsArray);
            jsonObject.put("Data", dataArray);

            // Convert JSON object to string
            result = Jsoner.serialize(jsonObject);
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);

            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray data = (JsonArray) jsonObject.get("Data");

            StringWriter writer = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(writer);

            // Write the header
            String[] header = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
                header[i] = colHeadings.getString(i);
            }
            csvWriter.writeNext(header);

            // Write data rows
            for (int i = 0; i < prodNums.size(); i++) {
                List<String> row = new ArrayList<>();
                row.add(prodNums.getString(i));

                JsonArray dataRow = (JsonArray) data.get(i);
                for (Object item : dataRow) {
                    row.add(item.toString());
                }

                csvWriter.writeNext(row.toArray(String[]::new));
            }

            csvWriter.close();
            result = writer.toString();
            
            // INSERT YOUR CODE HERE
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
