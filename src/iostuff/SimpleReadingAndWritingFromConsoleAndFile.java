package iostuff;

import java.io.*;
import java.util.Locale;

public class SimpleReadingAndWritingFromConsoleAndFile {

    public static void main(String[] args) {
        readFromConsoleUsingBufferedReader();
        readFromFileUsingInputStream();
        writeToFile();
    }

    private static void readFromConsoleUsingBufferedReader() {
        BufferedReader reader = new BufferedReader
                (new InputStreamReader(System.in));
        try {
            String readData = reader.readLine();
            String localizedData = String.format(Locale.getDefault(),
                    "%s",readData);
            System.out.println(localizedData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void readFromFileUsingInputStream() {
        try {
            InputStreamReader readFromFile = new InputStreamReader(new FileInputStream(new File("C:\\Users\\Echo01\\Desktop" +
                    "\\Lecture3_2K19\\Dummy.txt")));
            char[] buffer = new char[1024];
            int rawData;
            StringBuilder data = new StringBuilder();

            for (;;) {
                rawData = readFromFile
                        .read(buffer,0,buffer.length);
                if (rawData < 0) break;
                data.append(buffer,0,rawData);
            }
            String toPrint = data.toString();
            System.out.println(toPrint);

            readFromFile.close();
 /*           while ((rawData = readFromFile
                    .read(buffer,0,buffer.length)) < 0) {
                data.append(buffer,0,rawData);
            }
            String forPrinting = data.toString();
            System.out.println(forPrinting);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile() {
        File file =
                new File("C:\\Users\\Echo01\\Desktop" +
                "\\Lecture3_2K19\\Dummy.txt");
        try(FileWriter fileWriterAppend =
                    new FileWriter(file, true);
            BufferedWriter bufferedWriterAppend =
                    new BufferedWriter(fileWriterAppend);
            PrintWriter printWriterAppend =
                    new PrintWriter(bufferedWriterAppend)
                ) {
            String dummyText = " adasdasdasdasdasdasd";

            printWriterAppend.write(dummyText);

            printWriterAppend.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}