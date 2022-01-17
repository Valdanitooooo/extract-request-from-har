package cn.valdanito;

import com.jayway.jsonpath.JsonPath;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {
    private static final Options options = new Options();

    static {
        options.addOption("h", "help", false, "The command help");
        options.addOption("i", "in", true, "Location of HAR file.");
        options.addOption("o", "out", true, "Location of urls file.");
        options.addOption("j", "jmx", false, "Output JMX format.");
    }

    private static void help() {
        HelpFormatter hf = new HelpFormatter();
        hf.printHelp("help info", options);
    }

    private static String format(List<String> flows) {
        StringBuilder urls = new StringBuilder();
        for (String flow : flows) {
            flow = flow.replaceAll("&", "&amp;");
            urls.append(flow);
            urls.append("\n");
        }
        return urls.toString();
    }

    private static String formatJMX(List<String> flows) {
        StringBuilder urls = new StringBuilder();
        for (String flow : flows) {
            flow = flow.replaceAll("&", "&amp;");
            urls.append("<collectionProp name=\"1357392467\">\n   <stringProp name=\"726316312\">"
                    + flow + "</stringProp>\n</collectionProp>\n");
        }
        return urls.toString();
    }


    public static void main(String[] args) {
        if (null == args || args.length == 0) {
            help();
            return;
        }
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('h')) {
                help();
                return;
            }
            if (!cmd.hasOption("i")) {
                System.out.println("Missing HAR file path.");
                help();
                return;
            }
            if (!cmd.hasOption("o")) {
                System.out.println("Missing output file path.");
                help();
                return;
            }
            String input_path = cmd.getOptionValue("i");
            String output_path = cmd.getOptionValue("o");
            String jmx = cmd.getOptionValue("j");

            FileInputStream fis = new FileInputStream(input_path);
            final String str = IOUtils.toString(fis, "UTF-8");
            List<String> flows = JsonPath.parse(str).read("$.log.entries[*].request.url");
            if (flows.isEmpty()) {
                System.out.println("No URLs");
            }
            String urls = "";
            if (cmd.hasOption("j")) {
                urls = formatJMX(flows);
            } else {
                urls = format(flows);
            }
            File file = new File(output_path);
            FileOutputStream outputStream = new FileOutputStream(file);
            InputStream is = new ByteArrayInputStream(
                    urls.getBytes("utf8"));
            IOUtils.copy(is, outputStream);
            System.out.println("Success");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

}
