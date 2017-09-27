package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.*;
import com.alphatica.genotick.population.Population;
import com.alphatica.genotick.population.PopulationDAOFileSystem;
import com.alphatica.genotick.population.Robot;
import com.alphatica.genotick.population.RobotInfo;
import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;

public class Main {
    public static final String DEFAULT_DATA_DIR = "data";
    private static final String VERSION_STRING = "Genotick version 0.10.7 (copyleft 2017)";
    private static ErrorCode error = ErrorCode.CONTINUE;
    private static UserInput input;
    private static UserOutput output;

    public static void main(String[] args) throws IOException, IllegalAccessException {
        init(args);
    }

    public static ErrorCode init(String[] args) throws IOException, IllegalAccessException {
        Parameters parameters = new Parameters(args);
        if (canContinue()) {
            initHelp(parameters);
        }
        if (canContinue()) {
            initVersionRequest(parameters);
        }
        if (canContinue()) {
            initShowPopulation(parameters);
        }
        if (canContinue()) {
            initShowRobot(parameters);
        }
        if (canContinue()) {
            initUserIO(parameters);
        }
        if (canContinue()) {
            initReverse(parameters);
        }
        if (canContinue()) {
            initYahoo(parameters);
        }
        if (canContinue()) {
            initSimulation(parameters);
        }
        onExit();
        return error;
    }

    private static void exit(ErrorCode error) {
        Main.error = error;
    }

    private static boolean canContinue() {
        return (error == ErrorCode.CONTINUE);
    }

    private static void onExit() {
        System.out.println(format("Program finished with error code %s(%d)", error.toString(), error.getValue()));
    }

    private static void initShowRobot(Parameters parameters) {
        String value = parameters.getValue("showRobot");
        if(value != null) {
            try {
                showRobot(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                output.errorMessage(e.getMessage());
            }
            exit(ErrorCode.NO_ERROR);
        }
    }

    private static void showRobot(String value) throws IllegalAccessException {
        String robotString = getRobotString(value);
        System.out.println(robotString);
    }

    private static String getRobotString(String path) throws IllegalAccessException {
        File file = new File(path);
        Robot robot = PopulationDAOFileSystem.getRobotFromFile(file);
        return robot.showRobot();
    }

    private static void initShowPopulation(Parameters parameters) {
        String value = parameters.getValue("showPopulation");
        if(value != null) {
            try {
                showPopulation(value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                output.errorMessage(e.getMessage());
            }
            exit(ErrorCode.NO_ERROR);
        }
    }

    private static void showPopulation(String path) throws IllegalAccessException {
        PopulationDAOFileSystem dao = new PopulationDAOFileSystem(path);
        Population population = PopulationFactory.getDefaultPopulation(dao);
        showHeader();
        showRobots(population);
    }

    private static void showRobots(Population population) throws IllegalAccessException {
        for(RobotInfo robotInfo: population.getRobotInfoList()) {
            String info = getRobotInfoString(robotInfo);
            System.out.println(info);
        }
    }

    private static String getRobotInfoString(RobotInfo robotInfo) throws IllegalAccessException {
        StringBuilder sb = new StringBuilder();
        Field [] fields = robotInfo.getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            if(!Modifier.isStatic(field.getModifiers())) {
                Object object = field.get(robotInfo);
                if(sb.length() > 0) {
                    sb.append(",");
                }
                sb.append(object.toString());
            }
        }
        return sb.toString();
    }

    private static void showHeader() {
        Class<RobotInfo> infoClass = RobotInfo.class;
        List<Field> fields = buildFields(infoClass);
        String line = buildLine(fields);
        System.out.println(line);
    }

    private static List<Field> buildFields(Class<?> infoClass) {
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

    private static void initVersionRequest(Parameters parameters) {
        if(parameters.getValue("showVersion") != null) {
            System.out.println(Main.VERSION_STRING);
            exit(ErrorCode.NO_ERROR);
        }
    }
    
    private static void initHelp(Parameters parameters) {
        if(parameters.getValue("help") != null
                || parameters.getValue("--help") != null
                || parameters.getValue("-h") != null) {
            System.out.print("Displaying version: ");
            System.out.println("	java -jar genotick.jar showVersion");
            System.out.print("Reversing data: ");
            System.out.println("	java -jar genotick.jar reverse=mydata");
            System.out.print("Inputs from a file: ");
            System.out.println("	java -jar genotick.jar input=file:path\\to\\file");
            System.out.print("Output to a file: ");
            System.out.println("	java -jar genotick.jar output=csv");
            System.out.print("Custom output directory for generated files (log, charts, population): ");
            System.out.println("    java -jar genotick.jar outdir=path\\of\\folders");
            System.out.print("show population: ");
            System.out.println("	java -jar genotick.jar showPopulation=directory_with_population");
            System.out.print("show robot info: ");
            System.out.println("	java -jar genotick.jar showRobot=directory_with_population\\system name.prg");
            System.out.println("contact: 		lukasz.wojtow@gmail.com");
            System.out.println("more info: 		genotick.com");

            exit(ErrorCode.NO_ERROR);
        }
    }

    private static void initYahoo(Parameters parameters) {
        String yahooValue = parameters.getValue("fixYahoo");
        if(yahooValue != null) {
            YahooFixer yahooFixer = new YahooFixer(yahooValue);
            yahooFixer.fixFiles();
            exit(ErrorCode.NO_ERROR);
        }
    }

    private static void initUserIO(Parameters parameters) throws IOException {
        input = UserInputOutputFactory.getUserInput(parameters);
        if(input == null) {
            exit(ErrorCode.NO_INPUT);
            return;
        }
        output = UserInputOutputFactory.getUserOutput(parameters);
        if(output == null) {
            exit(ErrorCode.NO_OUTPUT);
            return;
        }
    }

    private static void initReverse(Parameters parameters) {
        String dataDirectory = parameters.getValue("reverse");
        if(dataDirectory != null) {
            DataLoader loader = new FileSystemDataLoader();
            DataSaver saver = new FileSystemDataSaver();
            MainAppData data = loader.loadAll(dataDirectory);
            for (DataSet loadedSet : data.getDataSets()) {
                Reversal reversal = new Reversal(loadedSet);
                if (!reversal.isReversed()) {
                    if (!data.containsDataSet(reversal.getReversedName())) {
                        DataSet reversedSet = reversal.getReversedDataSet();
                        saver.save(reversedSet);
                    }
                }
            }
            exit(ErrorCode.NO_ERROR);
        }
    }

    private static void initSimulation(Parameters parameters) throws IllegalAccessException {
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all arguments processed: " + parameters.getUnconsumed());
            exit(ErrorCode.UNKNOWN_ARGUMENT);
            return;
        }
        Simulation simulation = new Simulation();
        MainSettings settings = input.getSettings();
        MainAppData data = input.getData(settings.dataDirectory);
        generateMissingData(settings, data);
        settings.validateTimePoints(data);
        simulation.start(settings, data);
        exit(ErrorCode.NO_ERROR);
    }
    
    private static void generateMissingData(MainSettings settings, MainAppData data) {
        if (settings.requireSymmetricalRobots) {
            Collection<DataSet> loadedSets = data.getDataSets();
            DataSet[] loadedSetsCopy = loadedSets.toArray(new DataSet[data.getDataSets().size()]);
            for (DataSet loadedSet : loadedSetsCopy) {
                Reversal reversal = new Reversal(loadedSet);
                if (reversal.addReversedDataSetTo(data)) {
                    DataSaver saver = DataFactory.getDefaultSaver();
                    saver.save(reversal.getReversedDataSet());
                }
            }
        }
    }
}
