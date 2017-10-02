package com.alphatica.genotick.ui;

import java.io.File;
import java.io.IOException;

import static java.lang.String.format;

public class UserInputOutputFactory {
    private static final String INPUT_STRING = "input";
    private static final String INPUT_OPTION_FILE = "file" + FileInput.delimiter;
    private static final String INPUT_OPTION_DEFAULT = "default";
    private static final String INPUT_OPTION_RANDOM = "random";
    private static final String INPUT_OPTION_CONSOLE = "console";
    private static final String INPUT_OPTION_EXTERNAL = "external";
    private static final String OUTPUT_STRING = "output";
    private static final String OUTDIR_STRING = "outdir";
    private static final String OUTPUT_OPTION_CONSOLE = "console";
    private static final String OUTPUT_OPTION_CSV = "csv";
    private static final String OUTPUT_OPTION_NONE = "none";
    private static UserOutput userOutput;

    public static UserInput getUserInput(Parameters parameters) {
        final String input = parameters.getAndRemoveValue(INPUT_STRING);
        if (input == null) {
            return tryConsoleInput();
        }
        if (input.startsWith(INPUT_OPTION_FILE)) {
            return new FileInput(input);
        }
        switch (input) {
            case INPUT_OPTION_DEFAULT: return new DefaultInputs();
            case INPUT_OPTION_RANDOM: return new RandomParametersInput();
            case INPUT_OPTION_CONSOLE: return tryConsoleInput();
            case INPUT_OPTION_EXTERNAL: return new ExternalInput();
        }
        printOptionInfo(INPUT_STRING, input,
                INPUT_OPTION_FILE,
                INPUT_OPTION_DEFAULT,
                INPUT_OPTION_RANDOM,
                INPUT_OPTION_CONSOLE);
        return null;
    }

    private static UserInput tryConsoleInput() {
        UserInput input;
        try {
            input = new ConsoleInput();
            return input;
        } catch (RuntimeException ex) {
            System.err.println("Unable to get Console Input. Resorting to Default Input");
            return new DefaultInputs();
        }
    }

    public static UserOutput createUserOutput(Parameters parameters) throws IOException {
        final String output = parameters.getAndRemoveValue(OUTPUT_STRING);
        final String outdir = parameters.getAndRemoveValue(OUTDIR_STRING);
        if (outdir != null && !outdir.isEmpty()) {
            createDirsThrowable(outdir);
        }
        if (output == null) {
            userOutput = new ConsoleOutput(outdir);
        }
        else {
            switch (output) {
                case OUTPUT_OPTION_CONSOLE: userOutput = new ConsoleOutput(outdir); break;
                case OUTPUT_OPTION_CSV: userOutput = new CsvOutput(outdir); break;
                case OUTPUT_OPTION_NONE: userOutput = new NoOutput(outdir); break;
            }
            printOptionInfo(OUTPUT_STRING, output,
                    OUTPUT_OPTION_CONSOLE,
                    OUTPUT_OPTION_CSV,
                    OUTPUT_OPTION_NONE);
            userOutput = null;
        }
        return userOutput;
    }
    
    public static UserOutput getUserOutput() {
    	if(userOutput == null) {
    		userOutput = new ConsoleOutput(null);
    	}
    	return userOutput;
    }
    
    private static void createDirsThrowable(String path) throws IOException {
        if (!createDirs(path)) {
            throw new IOException(format("Unable to create output directory %s", path));
        }
    }
    
    private static boolean createDirs(String path) {
        File dirFile = new File(path);
        return dirFile.exists() || dirFile.mkdirs();
    }
    
    private static void printOptionInfo(String optionName, String optionValue, String... availableOptionValues) {
        System.out.println(format("'%s=%s' is not a valid option.", optionName, optionValue));
        System.out.println("Options are:");
        for (String option : availableOptionValues) {
            System.out.println(format("%s=%s", optionName, option));
        }
    }
}
