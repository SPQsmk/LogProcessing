import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LogProcessing {
    public static void main(String[] args) {
//        LogsGenerator.generate("src//Resources//log.log", 15);

        Map<Integer, Date> dateMap = new HashMap<>();
        int average = getAverageTime("src//Resources//log.log", dateMap);

        if (average == -1) {
            return;
        }

        if (args.length == 0) {
            int sigmaAmount = 0;

            for (int index : dateMap.keySet()) {
                sigmaAmount += Math.pow(dateMap.get(index).getTime() / 1000 - average, 2);
            }

            double sigma = Math.sqrt(sigmaAmount / dateMap.keySet().size());

            System.out.println("Average: " + average);
            System.out.println("Sigma amount: " + sigmaAmount);
            System.out.println("Sigma: " + sigma);
            System.out.println();

            printProcesses(dateMap, 3 * sigma, 0);
        } else {
            try {
                double enterNum = Double.parseDouble(args[0]);

                System.out.println("Average: " + average);
                System.out.println("Number: " + enterNum);

                printProcesses(dateMap, enterNum, average);
            } catch (NumberFormatException ex) {
                System.out.println("Incorrect number format");
                return;
            }
        }
    }

    private static int getAverageTime(String fileName, Map<Integer, Date> map) {
        int totalCount = getTotalLines(fileName);

        if (totalCount == -1)
            return -1;

        int amount = 0;
        int count = 0;
        int numOfLines = 0;

        try (Scanner scanner = new Scanner(new FileReader(fileName))) {
            while (scanner.hasNextLine()) {
                String[] split = scanner.nextLine().split(" - INFO - ");
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

                Date date = dateFormat.parse(split[0]);
                String[] queryInfo = split[1].split(" = ");

                int id = Integer.parseInt(queryInfo[1]);

                if (queryInfo[0].startsWith("RESULT")) {
                    map.put(id, new Date(date.getTime() - map.getOrDefault(id, date).getTime()));
                    amount += map.get(id).getTime() / 1000;
                } else {
                    map.put(id, date);
                    ++count;
                }

                ++numOfLines;

                System.out.println("Rows processed: " + numOfLines);
                System.out.println("Left to process: " + (totalCount - numOfLines));
            }
            System.out.println();

            return amount / count;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static int getTotalLines(String fileName) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[2048];
            int count = 0;
            int readChars;
            boolean isEmpty = true;

            while ((readChars = is.read(c)) != -1) {
                isEmpty = false;

                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }

            return (count == 0 && !isEmpty) ? 1 : count;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void printProcesses(Map<Integer, Date> dateMap, double num, int average) {
        for (int index : dateMap.keySet()) {
            if (Math.abs(dateMap.get(index).getTime() / 1000 - average) > num) {
                System.out.println("QUERY FOR ID " + index);
            }
        }
    }
}
