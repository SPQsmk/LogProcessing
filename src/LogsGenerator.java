import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

enum Query {
    QUERY(0), RESULT(1);

    private final int value;

    private Query(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class LogsGenerator {
    public static void generate(String fileName, Integer num) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            List<int[]> list = new ArrayList<>();
            int time = 0;

            for (int i = 0; i <= num; ++i) {
                int[] query = new int[3];
                int[] queryResult = new int[3];

                time += ThreadLocalRandom.current().nextInt(1, 10);

                query[0] = i;
                query[1] = Query.QUERY.getValue();
                query[2] = time;

                queryResult[0] = i;
                queryResult[1] = Query.RESULT.getValue();
                queryResult[2] = time + ThreadLocalRandom.current().nextInt(1, 100);

                list.add(query);
                list.add(queryResult);
            }
            Collections.sort(list, Comparator.comparingInt(a -> a[2]));

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

            String sQuery = " - INFO - QUERY FOR ID = ";
            String sResult = " - INFO - RESULT QUERY FOR ID = ";

            for (int[] arr : list) {
                Date d = new Date(date.getTime() + arr[2] * 1000);

                if (arr[1] == Query.QUERY.getValue())
                    fileWriter.write(String.format("%s%s%d\n", dateFormat.format(d), sQuery, arr[0]));
                else
                    fileWriter.write(String.format("%s%s%d\n", dateFormat.format(d), sResult, arr[0]));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

