package com.alphatica.genotick.ui;

import java.io.IOException;

public class UserInputOutputFactory {
    private static final String INPUT_STRING = "input";
    private static final String OUTPUT_STRING = "output";
    private static final String OPTION_FILE = "file" + FileInput.delimiter;
    private static final String OPTION_DEFAULT = "default";
    private static final String OPTION_RANDOM = "random";
    private static final String OPTION_CONSOLE = "console";
    private static final String OPTION_FILE_SAMPLE = String.format("%s=%spath\\to\\file", INPUT_STRING, OPTION_FILE);
    private static final String OPTION_DEFAULT_SAMPLE = String.format("%s=%s", INPUT_STRING, OPTION_DEFAULT);
    private static final String OPTION_RANDOM_SAMPLE = String.format("%s=%s", INPUT_STRING, OPTION_RANDOM);
    private static final String OPTION_CONSOLE_SAMPLE = String.format("%s=%s", INPUT_STRING, OPTION_CONSOLE);
    private static UserOutput userOutput;

    public static UserInput getUserInput(Parameters parameters) {
        String input = parameters.getValue(INPUT_STRING);
        parameters.removeKey(INPUT_STRING);
        if(input == null)
            return tryConsoleInput();
        if(input.startsWith(OPTION_FILE)) {
            return new FileInput(input);
        }
        switch(input) {
            case OPTION_DEFAULT: return new DefaultInputs();
            case OPTION_RANDOM: return new RandomParametersInput();
            case OPTION_CONSOLE: return tryConsoleInput();
        }
        System.out.println(String.format("'%s=%s' is not a valid option.", INPUT_STRING, input));
        System.out.println("Options are:");
        System.out.println(OPTION_FILE_SAMPLE);
        System.out.println(OPTION_DEFAULT_SAMPLE);
        System.out.println(OPTION_RANDOM_SAMPLE);
        System.out.println(OPTION_CONSOLE_SAMPLE);
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

    public static UserOutput getUserOutput(Parameters parameters) throws IOException {
        String output = parameters.getValue(OUTPUT_STRING);
        parameters.removeKey(OUTPUT_STRING);
        if (output != null && output.equals("csv"))
        	userOutput = new CsvOutput();
        else if(userOutput == null)
            userOutput = new ConsoleOutput();
        return userOutput;
    }
    
    public static UserOutput getUserOutput() {
    	if(userOutput == null) {
    		userOutput = new ConsoleOutput();
    	}    		
    	return userOutput;
    }
}
