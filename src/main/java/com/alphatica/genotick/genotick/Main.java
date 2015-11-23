package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataUtils;
import com.alphatica.genotick.data.YahooFixer;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAOFileSystem;
import com.alphatica.genotick.population.Program;
import com.alphatica.genotick.population.ProgramInfo;
import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

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
        checkShowPopulation(parameters);
        checkShowProgram(parameters);
        getUserIO(parameters);
        checkReverse(parameters);
        checkYahoo(parameters);
        checkSimulation(parameters);
    }

    private static void checkShowProgram(Parameters parameters) {
        String value = parameters.getValue("showProgram");
        if(value != null) {
            try {
                showProgram(value);
            } catch (IllegalAccessException e) {
                Debug.d(e);
            }
            System.exit(0);
        }
    }

    private static void showProgram(String value) throws IllegalAccessException {
        String programString = getProgramString(value);
        System.out.println(programString);
    }

    private static String getProgramString(String path) throws IllegalAccessException {
        File file = new File(path);
        Program program = PopulationDAOFileSystem.getProgramFromFile(file);
        return program.showProgram();
    }

    private static void checkShowPopulation(Parameters parameters) {
        String value = parameters.getValue("showPopulation");
        if(value != null) {
            try {
                showPopulation(value);
            } catch (IllegalAccessException e) {
                Debug.d(e);
            }
            System.exit(0);
        }
    }

    private static void showPopulation(String value) throws IllegalAccessException {
        PopulationDAOFileSystem dao = new PopulationDAOFileSystem();
        dao.setSettings(value);
        Population population = PopulationFactory.getDefaultPopulation(dao);
        showHeader();
        showPrograms(population);
    }

    private static void showPrograms(Population population) throws IllegalAccessException {
        for(ProgramInfo programInfo: population.getProgramInfoList()) {
            String info = getProgramInfoString(programInfo);
            System.out.println(info);
        }
    }

    private static String getProgramInfoString(ProgramInfo programInfo) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Field [] fields = programInfo.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            if(!Modifier.isStatic(field.getModifiers())) {
                Object object = field.get(programInfo);
                if(sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(object.toString());
            }
        }
        return sb.toString();
    }

    private static void showHeader() {
        Class infoClass = ProgramInfo.class;
        List<Field> fields = buildFields(infoClass);
        String line = buildLine(fields);
        System.out.println(line);
    }

    private static List<Field> buildFields(Class infoClass) {
        List<Field> fields = new ArrayList<>();
        for(Field field: infoClass.getDeclaredFields()) {
            if(!Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }

    private static String buildLine(List<Field> fields) {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(field.getName());
        }
        return sb.toString();
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
