package wang.relish.accumulation;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            System.out.println(r.nextInt(2));
        }
    }

    @Test
    public void f() {
        System.out.println(String.format(Locale.CHINA, "%02d:%02d:%02d", 1, 2, 3));
    }

    @Test
    public void testReadFile() throws IOException, InterruptedException {
        File file = new File("/Users/relish/desktop/1.txt");
        InputStream in = new FileInputStream(file);
        Charset charset = Charset.defaultCharset();
        System.out.println(readLine(in, charset));
        System.out.println(readLine(in, charset));
        in.close();
    }

    public static String readLine(InputStream in, Charset charset) throws IOException {
        InputStreamReader isr = new InputStreamReader(in, charset);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String s = br.readLine();
        while (s != null) {
            sb.append(s);
            sb.append("\n");
            s = br.readLine();
        }
        return sb.toString();
    }


    @Test
    public void writeTest() throws IOException {
        File file = new File("/Users/relish/desktop/2.txt");
        OutputStream outputStream = new FileOutputStream(file);
        List<String> stringList = new ArrayList<String>() {
            {
                for (int i = 0; i < 10; i++) {
                    add(i + "");
                }
            }
        };
        Iterator<String> iterator = stringList.iterator();
        Charset charset = Charset.defaultCharset();
        writeLines(outputStream, iterator, charset);
    }

    public static void writeLines(OutputStream out, Iterator<String> iterator, Charset charset) throws IOException {
        Writer writer = new OutputStreamWriter(out, charset);
        while (iterator.hasNext()) {
            writer.write(iterator.next());
            writer.write("\n");
        }
    }
}