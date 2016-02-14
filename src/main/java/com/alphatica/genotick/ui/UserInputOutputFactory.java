package com.alphatica.genotick.ui;

import com.alphatica.genotick.genotick.Debug;

public class UserInputOutputFactory {
    private static final String INPUT_STRING = "input";
    private static final String OUTPUT_STRING = "output";

    public static UserInput getUserInput(Parameters parameters) {
        String input = parameters.getValue(INPUT_STRING);
        parameters.removeKey(INPUT_STRING);
        if(input == null)
            return tryConsoleInput();
        if(input.startsWith("file" + FileInput.delimiter)) {
            return new FileInput(input);
        }
        switch(input) {
            case "default": return new DefaultInputs();
            case "random": return new RandomParametersInput();
            case "console": return tryConsoleInput();
        }
        return null;
    }

    private static UserInput tryConsoleInput() {
        UserInput input;
        try {
            input = new ConsoleInput();
            return input;
        } catch (RuntimeException ex) {
            Debug.d("Unable to get Console Input. Resorting to Default Input");
            return new DefaultInputs();
        }
    }

    public static UserOutput getUserOutput(Parameters parameters) {
        String output = parameters.getValue(OUTPUT_STRING);
        parameters.removeKey(OUTPUT_STRING);
        if (output != null && output.equals("csv"))
            return new CsvOutput();
        else
            return new ConsoleOutput();

    }
}
