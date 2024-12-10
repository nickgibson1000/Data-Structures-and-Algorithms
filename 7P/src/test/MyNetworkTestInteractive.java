import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Stack;

import com.github.difflib.text.DiffRowGenerator;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRow.Tag;

public class MyNetworkTestInteractive {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @BeforeEach
    public void setup() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @AfterEach
    public void cleanUpStreams() {
        System.setOut(systemOut);
        System.setIn(systemIn);
    }

    private String getFileNamePath(String filename) throws URISyntaxException {
        URI uri = MyNetworkTestInteractive.class.getResource(filename).toURI();
        return Paths.get(uri).toString();
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private List<String> split(String content) {
        return Arrays.asList(content.split("\n"));
    }

    private void print(List<DiffRow> diffRows) {

        for(int r = 0; r < diffRows.size(); r++) {
            DiffRow row = diffRows.get(r);

            if(!row.getTag().equals(DiffRow.Tag.EQUAL)){

                String expected = row.getOldLine();
                String actual = row.getNewLine();

                if(r != 0) {
                    String prevLine = diffRows.get(r-1).getNewLine();
                    System.err.printf("[OK] %d: \"%s\"%n", r-1, prevLine);
                }

                System.err.printf ("[MISMATCH] %d: Expected: \"%s\", Actual: \"%s\"%n",
                                   r, expected, actual);

                if(r+1 < diffRows.size()) {
                    String nextLine = diffRows.get(r+1).getNewLine();
                    System.err.printf("[OK] %d: \"%s\"%n", r+1, nextLine);
                }
                System.err.println();
            }
        }
    }

    @Test
    @Timeout(value = 10, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
    void testRun11() throws IOException {

        try {

            InputStream is = MyNetworkTestInteractive.class.getResourceAsStream("/input11");
            String input = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            is = MyNetworkTestInteractive.class.getResourceAsStream("/output11");
            String expected = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            provideInput(input);
            String filePath = getFileNamePath("data1");
            String[] args = {filePath};
            MyNetwork.main(args);

            String actual = testOut.toString();

            DiffRowGenerator generator = DiffRowGenerator.create()
                .ignoreWhiteSpaces(true)
                .columnWidth(Integer.MAX_VALUE) // do not wrap
                .build();
            List<DiffRow> rows = generator.generateDiffRows(split(expected),
                split(actual));

            List<DiffRow> errors = rows.stream()
                .filter(r -> !r.getTag().equals(DiffRow.Tag.EQUAL))
                .collect(Collectors.toList());

            if(errors.size() != 0) {
                print(rows);
            }
            assertTrue(errors.size() == 0);
        } catch(OutOfMemoryError e) {
            System.err.println(e);
            fail();
        } catch(Exception e) {
            e.printStackTrace();
            fail(e);
        }
    }
}
