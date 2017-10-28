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

    public static UserInput createUserInput(Parameters parameters, UserOutput userOutput) {
        final String input = parameters.getAndRemoveValue(INPUT_STRING);
        if (input == null) {
            return tryConsoleInput(userOutput);
        }
        else {
            return createUserInputByOption(input, userOutput);
        }
    }

    private static UserInput createUserInputByOption(String input, UserOutput userOutput) {
        if (input.startsWith(INPUT_OPTION_FILE)) {
            return new FileInput(input, userOutput);
        }
        switch (input) {
            case INPUT_OPTION_DEFAULT: return new DefaultInputs(userOutput);
            case INPUT_OPTION_RANDOM: return new RandomParametersInput(userOutput);
            case INPUT_OPTION_CONSOLE: return tryConsoleInput(userOutput);
            case INPUT_OPTION_EXTERNAL: return new ExternalInput(userOutput);
        }
        printOptionInfo(INPUT_STRING, input,
                INPUT_OPTION_FILE,
                INPUT_OPTION_DEFAULT,
                INPUT_OPTION_RANDOM,
                INPUT_OPTION_CONSOLE);
        return null;
    }

    private static UserInput tryConsoleInput(UserOutput userOutput) {
        UserInput input;
        try {
            input = new ConsoleInput(userOutput);
            return input;
        } catch (RuntimeException ex) {
            System.err.println("Unable to get Console Input. Resorting to Default Input");
            return new DefaultInputs(userOutput);
        }
    }

    public static UserOutput createUserOutput(Parameters parameters) throws IOException {
        final String output = parameters.getAndRemoveValue(OUTPUT_STRING);
        final String outdir = parameters.getAndRemoveValue(OUTDIR_STRING);
        if (outdir != null && !outdir.isEmpty()) {
            createDirsThrowable(outdir);
        }
        if (output == null) {
            return new ConsoleOutput(outdir);
        }
        return createUserOutputByOption(output, outdir);
    }

    private static UserOutput createUserOutputByOption(String output, String outdir) throws IOException {
        switch (output) {
            case OUTPUT_OPTION_CONSOLE: return new ConsoleOutput(outdir);
            case OUTPUT_OPTION_CSV: return new CsvOutput(outdir);
            case OUTPUT_OPTION_NONE: return new NoOutput(outdir);
        }
        printOptionInfo(OUTPUT_STRING, output,
                OUTPUT_OPTION_CONSOLE,
                OUTPUT_OPTION_CSV,
                OUTPUT_OPTION_NONE);
        return null;
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
