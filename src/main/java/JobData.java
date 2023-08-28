import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LaunchCode
 */
public class JobData {

    private static final String DATA_FILE = "src/main/resources/job_data.csv";
    private static boolean isDataLoaded = false;

    private static ArrayList<HashMap<String, String>> allJobs;

    /**
     * Fetch list of all values from loaded data,
     * without duplicates, for a given column.
     *
     * @param field The column to retrieve values from
     * @return List of all of the values of the given field
     */
    public static ArrayList<String> findAll(String field) {

        // load data, if not already loaded
        loadData();

        ArrayList<String> values = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            String aValue = row.get(field);

            if (!values.contains(aValue)) {
                values.add(aValue);
            }
        }

        // Bonus mission: sort the results
        Collections.sort(values);

        return values;
    }

    public static ArrayList<HashMap<String, String>> findAll() {

        // load data, if not already loaded
        loadData();

        // Bonus mission; normal version returns allJobs
        return new ArrayList<>(allJobs);
    }

    /**
     * Returns results of search the jobs data by key/value, using
     * inclusion of the search term.
     *
     * For example, searching for employer "Enterprise" will include results
     * with "Enterprise Holdings, Inc".
     *
     * @param column   Column that should be searched.
     * @param value Value of teh field to search for
     * @return List of all jobs matching the criteria
     */
    public static ArrayList<HashMap<String, String>> findByColumnAndValue(String column, String value) {

        // load data, if not already loaded
        loadData();

        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();

        for (HashMap<String, String> row : allJobs) {
            // Make value of key lowercase for case-insensitive comparison
            String aValue = row.get(column).toLowerCase();
            // Make value entered by user lowercase for case-insensitive comparison
            if (aValue.contains(value.toLowerCase())) {
                jobs.add(row);
            }
        }

        return jobs;
    }

    /**
     * Search all columns for the given term
     *
     * @param value The search term to look for
     * @return      List of all jobs with at least one field containing the value
     */
    public static ArrayList<HashMap<String, String>> findByValue(String value) {

        // load data, if not already loaded
        loadData();

        // TODO - implement this method
        ArrayList<HashMap<String, String>> jobs = new ArrayList<>();
        boolean matchFound = false;
        // For each HashMap in the allJobs ArrayList
        for (HashMap<String, String> row : allJobs) {

           // For each key in the HashMap
           for (String key : row.keySet()) {

               // We don't want duplicate job listings to the jobs ArrayList, so we set boolean matchFound to true
               // if the user's search value is found in "position type".
               if (key.equals("position type") && row.get(key).toLowerCase().contains(value.toLowerCase())) {

                   matchFound = true;
                   jobs.add(row);

                 // "name" and "position type" might contain the same value, so if the value was already found in
                 // "position type" above, we break to avoid adding the same job listing to jobs more than once.
                 // Else, position type did not contain the search value, and we can set matchFound to true and
                 // add the listing to the jobs ArrayList.
               } else if (key.equals("name") && row.get(key).toLowerCase().contains(value.toLowerCase())) {

                   if (matchFound) {

                       break;

                   } else {

                       matchFound = true;
                       jobs.add(row);

                   }

                 // Technically, the employer name could be present in name or position type (i.e. if the employer's
                 // name is Data Scientist Group LLC or IT Analyst Corp.) So, we check if the user's search value was
                 // already contained in "position type" or "name" above by checking matchFound. If matchFound is true,
                 // break to avoid adding the same job listing more than once in the jobs ArrayList. Else, set
                 // matchFound to true and add the listing to jobs.
               } else if (key.equals("employer") && row.get(key).toLowerCase().contains(value.toLowerCase())) {

                   if (matchFound) {

                       break;

                   } else {

                       matchFound = true;
                       jobs.add(row);

                   }

                   // technically, the location could be present in "employer"
                   // (i.e. if the employer's name is Philadelphia Data Scientists LLC or Saint Louis IT Analysts Corp.)
                   // So, we check if the user's search value was already contained in
                   // "employer" above by checking matchFound. If matchFound is true, break to avoid adding the
                   // same job listing more than once in the jobs ArrayList. Else, set matchFound to true
                   // and add the listing to jobs.
               } else if (key.equals("location") && row.get(key).toLowerCase().contains(value.toLowerCase())) {

                   if (matchFound) {

                       break;

                   } else {

                       matchFound = true;
                       jobs.add(row);

                   }

                 // The skill entered by the user could be present in "position type", "name", or even "employer" (i.e.
                 // Web Development LLC. or Java Developers LLC.). So, we check if matchFound is true. If so, break
                 // to avoid adding the same job listing more than once to the jobs ArrayList. Else, there is no need to
                 // set matchFound to true (as, we aren't testing anything else after this) so, we just add the listing
                 // to the jobs ArrayList
               } else if (key.equals("core competency") && row.get(key).toLowerCase().contains(value.toLowerCase())) {

                   if (matchFound) {

                       break;

                   } else {

                       jobs.add(row);

                   }

               }

           }

           // Set matchFound to false before beginning the next HashMap row in allJobs
           matchFound = false;

        }

        // Return the jobs ArrayList
        return jobs;

    }

    /**
     * Read in data from a CSV file and store it in a list
     */
    private static void loadData() {

        // Only load data once
        if (isDataLoaded) {
            return;
        }

        try {

            // Open the CSV file and set up pull out column header info and records
            Reader in = new FileReader(DATA_FILE);
            CSVParser parser = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            List<CSVRecord> records = parser.getRecords();
            Integer numberOfColumns = records.get(0).size();
            String[] headers = parser.getHeaderMap().keySet().toArray(new String[numberOfColumns]);

            allJobs = new ArrayList<>();

            // Put the records into a more friendly format
            for (CSVRecord record : records) {
                HashMap<String, String> newJob = new HashMap<>();

                for (String headerLabel : headers) {
                    newJob.put(headerLabel, record.get(headerLabel));
                }

                allJobs.add(newJob);
            }

            // flag the data as loaded, so we don't do it twice
            isDataLoaded = true;

        } catch (IOException e) {
            System.out.println("Failed to load job data");
            e.printStackTrace();
        }
    }

}
