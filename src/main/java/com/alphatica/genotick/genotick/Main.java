package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataUtils;
import com.alphatica.genotick.data.YahooFixer;
import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

public class Main {
    public static final String DEFAULT_DATA_DIR = "data";
    private static final String VERSION = "Genotick version 0.7 (copyleft 2015)";
    private static UserInput input;
    private static UserOutput output;

    public static void main(String... args) {
        setupDebug();
        setupExceptionHandler();
        Parameters parameters = new Parameters(args);
        checkVersionRequest(parameters);
        getUserIO(parameters);
        checkReverse(parameters);
        checkYahoo(parameters);
        checkSimulation(parameters);
    }

    private static void setupExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(Debug.createExceptionHandler());
    }

    private static void checkVersionRequest(Parameters parameters) {
        if(parameters.getValue("showVersion") != null) {
            System.out.println(Main.VERSION);
            System.exit(0);
        }
    }

    private static void checkYahoo(Parameters parameters) {
        String yahooValue = parameters.getValue("fixYahoo");
        if(yahooValue == null) {
            return;
        }
        YahooFixer yahooFixer = new YahooFixer(yahooValue);
        yahooFixer.fixFiles();
        System.exit(0);
    }

    private static void setupDebug() {
        Debug.setShowTime(true);
        String outFileName = "genotick_" + DataUtils.getDateTimeString() + ".txt";
        Debug.toFile(outFileName);
    }

    private static void getUserIO(Parameters parameters) {
        input = UserInputOutputFactory.getUserInput(parameters);
        if(input == null) {
            exit(ERROR_CODES.NO_INPUT);
        }
        output = UserInputOutputFactory.getUserOutput();
        //noinspection ConstantConditions
        if(output == null) {
            exit(ERROR_CODES.NO_OUTPUT);
        }
    }

    private static void checkReverse(Parameters parameters) {
        String reverseValue = parameters.getValue("reverse");
        if(reverseValue == null)
            return;
        Reversal reversal = new Reversal(reverseValue,output);
        reversal.reverse();
        System.exit(0);
    }

    private static void checkSimulation(Parameters parameters) {
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all program arguments processed: " + parameters.getUnconsumed());
            exit(ERROR_CODES.UNKNOWN_ARGUMENT);
        }
        Application application = new Application(output);
        input.show(application);
    }

    private static void exit(ERROR_CODES code) {
        System.exit(code.getValue());
    }
}

enum ERROR_CODES {
    NO_INPUT(1),
    UNKNOWN_ARGUMENT(2),
    NO_OUTPUT(3);

    private final int code;

    ERROR_CODES(int code) {
        this.code = code;
    }

    public int getValue() {
        return code;
    }
}
