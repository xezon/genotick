package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.DataSaver;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.FileSystemDataLoader;
import com.alphatica.genotick.data.FileSystemDataSaver;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.data.YahooFixer;
import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;

import java.util.Collection;
import java.io.IOException;

import static java.lang.String.format;

public class Main {
    public static final String DEFAULT_DATA_DIR = "data";
    private static final String VERSION_STRING = "Genotick version 0.10.7 (copyleft 2017)";
    private static ErrorCode error;
    private static boolean canContinue;
    private static UserInput input;
    private static UserOutput output;

    public static void main(String[] args) throws IOException, IllegalAccessException {
        init(args);
    }

    public static ErrorCode init(String[] args) throws IOException, IllegalAccessException {
        setError(ErrorCode.NO_ERROR);
        Parameters parameters = new Parameters(args);
        if (canContinue) {
            initHelp(parameters);
        }
        if (canContinue) {
            initVersionRequest(parameters);
        }
        if (canContinue) {
            initUserIO(parameters);
        }
        if (canContinue) {
            initShowPopulation(parameters);
        }
        if (canContinue) {
            initShowRobot(parameters);
        }
        if (canContinue) {
            initReverse(parameters);
        }
        if (canContinue) {
            initYahoo(parameters);
        }
        if (canContinue) {
            initSimulation(parameters);
        }
        printError(error);
        return error;
    }

    private static void setError(ErrorCode error) {
        Main.error = error;
        canContinue = (error == ErrorCode.NO_ERROR) ? true : false;
    }

    private static void printError(final ErrorCode error) {
        System.out.println(format("Program finished with error code %s(%d)", error.toString(), error.getValue()));
    }

    private static void initHelp(Parameters parameters) {
        if(parameters.getValue("help") != null
                || parameters.getValue("--help") != null
                || parameters.getValue("-h") != null) {
            System.out.print("Displaying version: ");
            System.out.println("    java -jar genotick.jar showVersion");
            System.out.print("Reversing data: ");
            System.out.println("    java -jar genotick.jar reverse=mydata");
            System.out.print("Inputs from a file: ");
            System.out.println("    java -jar genotick.jar input=file:path\\to\\file");
            System.out.print("Output to a file: ");
            System.out.println("    java -jar genotick.jar output=csv");
            System.out.print("Custom output directory for generated files (log, charts, population): ");
            System.out.println("    java -jar genotick.jar outdir=path\\of\\folders");
            System.out.print("show population: ");
            System.out.println("    java -jar genotick.jar showPopulation=directory_with_population");
            System.out.print("show robot info: ");
            System.out.println("    java -jar genotick.jar showRobot=directory_with_population\\system name.prg");
            System.out.println("contact:        lukasz.wojtow@gmail.com");
            System.out.println("more info:      genotick.com");

            setError(ErrorCode.NO_ERROR);
        }
    }
    
    private static void initVersionRequest(Parameters parameters) {
        if(parameters.getValue("showVersion") != null) {
            System.out.println(Main.VERSION_STRING);
            setError(ErrorCode.NO_ERROR);
        }
    }

    private static void initUserIO(Parameters parameters) throws IOException {
        input = UserInputOutputFactory.createUserInput(parameters);
        if(input == null) {
            setError(ErrorCode.NO_INPUT);
            return;
        }
        output = UserInputOutputFactory.createUserOutput(parameters);
        if(output == null) {
            setError(ErrorCode.NO_OUTPUT);
            return;
        }
    }

    private static void initShowRobot(Parameters parameters) {
        String path = parameters.getValue("showRobot");
        if(path != null) {
            try {
                RobotPrinter.printRobot(path);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            setError(ErrorCode.NO_ERROR);
        }
    }

    private static void initShowPopulation(Parameters parameters) {
        String path = parameters.getValue("showPopulation");
        if(path != null) {
            try {
                PopulationPrinter.printPopulation(path);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            setError(ErrorCode.NO_ERROR);
        }
    }

    private static void initYahoo(Parameters parameters) {
        String yahooValue = parameters.getValue("fixYahoo");
        if(yahooValue != null) {
            YahooFixer yahooFixer = new YahooFixer(yahooValue);
            yahooFixer.fixFiles();
            setError(ErrorCode.NO_ERROR);
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
            setError(ErrorCode.NO_ERROR);
        }
    }

    private static void initSimulation(Parameters parameters) throws IllegalAccessException {
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all arguments processed: " + parameters.getUnconsumed());
            setError(ErrorCode.UNKNOWN_ARGUMENT);
            return;
        }
        Simulation simulation = new Simulation();
        MainSettings settings = input.getSettings();
        MainAppData data = input.getData(settings.dataDirectory);
        generateMissingData(settings, data);
        simulation.start(settings, data);
        setError(ErrorCode.NO_ERROR);
    }
    
    private static void generateMissingData(MainSettings settings, MainAppData data) {
        if (settings.requireSymmetricalRobots) {
            Collection<DataSet> loadedSets = data.getDataSets();
            DataSet[] loadedSetsCopy = loadedSets.toArray(new DataSet[data.getDataSets().size()]);
            for (DataSet loadedSet : loadedSetsCopy) {
                Reversal reversal = new Reversal(loadedSet);
                if (reversal.addReversedDataSetTo(data)) {
                    if (!settings.dataDirectory.isEmpty()) {
                        DataSaver saver = DataFactory.getDefaultSaver();
                        saver.save(reversal.getReversedDataSet());
                    }
                }
            }
        }
    }
}
