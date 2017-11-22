package com.alphatica.genotick.genotick;

import com.alphatica.genotick.chart.GenoChart;
import com.alphatica.genotick.chart.GenoChartFactory;
import com.alphatica.genotick.chart.GenoChartMode;
import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLines;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.DataSaver;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.FileSystemDataLoader;
import com.alphatica.genotick.data.FileSystemDataSaver;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.data.YahooFixer;
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
import java.nio.file.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

import static java.lang.String.format;

public class Main {
    public static final String DEFAULT_DATA_DIR = "data";
    private static final String VERSION_STRING = "Genotick version 0.10.7 (copyleft 2017)";
    private ErrorCode error = ErrorCode.NO_ERROR;
    private boolean canContinue = true;
    private UserInput input;
    private UserOutput output;
    private MainInterface.Session session;

    public static void main(String[] args) throws IOException, IllegalAccessException {
        Main main = new Main();
        main.init(args, null);
    }

    public ErrorCode init(String[] args, MainInterface.Session session) throws IOException, IllegalAccessException {
        this.session = session;
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
            initDrawData(parameters);
        }
        if (canContinue) {
            initShowPopulation(parameters);
        }
        if (canContinue) {
            initShowRobot(parameters);
        }
        if (canContinue) {
            initMerge(parameters);
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

    private void setError(ErrorCode error) {
        this.error = error;
        this.canContinue = false;
    }

    private void printError(final ErrorCode error) {
        System.out.println(format("Program finished with error code %s(%d)", error.toString(), error.getValue()));
    }

    private void initHelp(Parameters parameters) {
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
            System.out.print("Show population: ");
            System.out.println("    java -jar genotick.jar showPopulation=directory_with_population");
            System.out.print("Show robot info: ");
            System.out.println("    java -jar genotick.jar showRobot=directory_with_population\\system name.prg");
            System.out.print("Merge robots: ");
            System.out.println("    java -jar genotick.jar mergeRobots=directory_for_merged_robots candidateRobots=base_directory_of_Population_folders");
            System.out.print("Draw price curves for asset data ");
            System.out.println("    java -jar genotick.jar drawData=mydata");
            System.out.println("contact:        lukasz.wojtow@gmail.com");
            System.out.println("more info:      genotick.com");

            setError(ErrorCode.NO_ERROR);
        }
    }
    
    private void initVersionRequest(Parameters parameters) {
        if(parameters.getValue("showVersion") != null) {
            System.out.println(Main.VERSION_STRING);
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initUserIO(Parameters parameters) throws IOException {
        output = UserInputOutputFactory.createUserOutput(parameters);
        if (output == null) {
            setError(ErrorCode.NO_OUTPUT);
            return;
        }
        input = UserInputOutputFactory.createUserInput(parameters, output, session);
        if (input == null) {
            setError(ErrorCode.NO_INPUT);
            return;
        }
    }

    private void initDrawData(Parameters parameters) {
        String dataDirectory = parameters.getValue("drawData");
        if (dataDirectory != null) {
            DataLoader loader = new FileSystemDataLoader(output);
            MainAppData data = loader.loadAll(dataDirectory);
            GenoChart chart = GenoChartFactory.create(GenoChartMode.JFREECHART_DRAW, output);
            for (DataSet set : data.getDataSets()) {
                DataLines dataLines = set.getDataLinesCopy();
                int lineCount = dataLines.lineCount();
                String chartName = set.getName().getName();
                for (int line = 0; line < lineCount; ++line) {
                    for (int column : Column.Array.OHLC) {
                        double value = dataLines.getOhlcValue(line, column);
                        chart.addXYLineChart(chartName, "bar", "price", Column.Names.OHLC[column], line, value);
                    }
                }
            }
            chart.plotAll();
            setError(ErrorCode.NO_ERROR);
        }
    }
    
    private void initShowRobot(Parameters parameters) {
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

    private void initShowPopulation(Parameters parameters) {
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

    private void initMerge(Parameters parameters) {
        String destination = parameters.getValue("mergeRobots");
        if(destination != null) {
            String source = parameters.getValue("candidateRobots");
            if(source != null) {
                ErrorCode errorCode = ErrorCode.NO_OUTPUT;
                try {
                    errorCode = mergePopulation(destination, source);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    output.errorMessage(e.getMessage());
                }
                setError(errorCode);
            }
        }
    }

    private static ErrorCode mergePopulation(String destination, String source) throws IllegalAccessException {
	    
        File destinationPath = new File(destination);
        destinationPath.mkdirs();
        
        PopulationDAOFileSystem dao = new PopulationDAOFileSystem(destinationPath.getAbsolutePath());
        Population destinationPopulation = PopulationFactory.getDefaultPopulation(dao);
        double initialScore = populationScore(destinationPopulation);
        System.out.println(format("Current population size: %d desiredSize: %d population score: %.4f", destinationPopulation.getSize(), destinationPopulation.getDesiredSize(), initialScore));
        // Enumerate directories here....
        try {
            System.out.println(destinationPath.getAbsolutePath());
            Files.walk(Paths.get(source), 1).filter(path -> Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)).collect(Collectors.toList()).parallelStream().forEach(directory -> mergeSource(destinationPopulation, destinationPath.getAbsolutePath(), directory));
        } catch (IOException e) {
            //
        }
        
        destinationPopulation.saveOnDisk();
        double newScore = populationScore(destinationPopulation);
        if(newScore > initialScore) {
            System.out.println(format("Success merging populations. New size: %d old score: %.4f new score: %.4f", destinationPopulation.getSize(), initialScore, newScore));
            return ErrorCode.NO_ERROR;
        }
        
        if(newScore < initialScore) {
            System.out.println(format("Warning population score decreased after merge:%.4f", newScore));
        }
        return ErrorCode.NO_OUTPUT;
    }
    
    private static void mergeSource(Population destinationPopulation, String destination, Path sourcePath) {
    	String source = sourcePath.toString().replace("./", "");
    	if(!source.startsWith("population_")) return;
    	File sourceFile = new File(source);
    	source = sourceFile.getAbsolutePath();
    	
    	if(destination.compareTo(source) == 0) return;
  		System.out.println(source);
	  		
        PopulationDAOFileSystem daoSource = new PopulationDAOFileSystem(source);
        Population sourcePopulation = PopulationFactory.getDefaultPopulation(daoSource);
        
        if(sourcePopulation.getSize() < 1) return;
        
        sourcePopulation.getRobotInfoList().stream().filter(robot -> robot.getWeight() == 0.0).collect(Collectors.toList()).forEach(robot -> sourcePopulation.removeRobot(robot.getName()));
        
        while(sourcePopulation.getSize() > 0) {
	        RobotInfo best = findBestPerformingRobot(sourcePopulation);
	        if(best == null) break;
	        
	        synchronized(destinationPopulation) {
		        RobotInfo worst = findWorstPerformingRobot(destinationPopulation);
		        
		        if(destinationPopulation.getSize() < destinationPopulation.getDesiredSize() && Math.abs(best.getWeight()) > 0.0 && best.isPredicting()) {
			        // Take robot...
			        System.out.println(format("Adding robot %s to destination due to desination not full. Weight: %.4f new size: %d", best.getName(), best.getWeight(), destinationPopulation.getSize()+1));
			        if(moveRobot(sourcePopulation, destinationPopulation, best)) {
    			        continue;
    			    }
		        } else if(worst != null && Math.abs(best.getWeight()) > 0 && Math.abs(worst.getWeight()) < Math.abs(best.getWeight()) && (best.isPredicting() || !worst.isPredicting())) {
			        System.out.println(format("Adding robot %s to destination due to higher weight. Weight: %.4f population score: %.4f", best.getName(), best.getWeight(), populationScore(destinationPopulation)));
			        destinationPopulation.removeRobot(worst.getName());
			        if(moveRobot(sourcePopulation, destinationPopulation, best)) {
    			        continue;
    			    }
		        }
	        }
	        break;
        }
        
        try {
	        Files.walk(sourceFile.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
	        System.out.println(format("Exception clearing directory %s. Exception %s", sourceFile, e.toString()));
        }
    }
    
    private static double populationScore(Population population) {
    	return population.getRobotInfoList().stream().mapToDouble((robot) -> {
        	return Math.abs(robot.getWeight());
        }).average().orElse(0);
    }
    
    private static boolean moveRobot(Population sourcePopulation, Population destinationPopulation, RobotInfo robot) {
        Robot movingRobot = sourcePopulation.getRobot(robot.getName());
        if(movingRobot == null) {
            return false;
        }
		sourcePopulation.removeRobot(robot.getName());
		movingRobot.setName(null);
		destinationPopulation.saveRobot(movingRobot);
		return true;
    }
    
    private static RobotInfo findWorstPerformingRobot(Population population) {
    	if(population.getRobotInfoList().size() < 1) {
        	return null;
        }
    	RobotInfo result = population.getRobotInfoList().stream().min((a,b) -> {
        	return (int)(Math.abs(a.getWeight()) - Math.abs(b.getWeight()));
    	}).get();
    	return result;
    }
    
    private static RobotInfo findBestPerformingRobot(Population population) {
    	if(population.getRobotInfoList().size() < 1) {
        	return null;
        }
    	RobotInfo result = population.getRobotInfoList().stream().max((a,b) -> {
        	return (int)(Math.abs(a.getWeight()) - Math.abs(b.getWeight()));
    	}).get();
    	return result;
    }

    private void initYahoo(Parameters parameters) {
        String path = parameters.getValue("fixYahoo");
        if(path != null) {
            YahooFixer yahooFixer = new YahooFixer(path, output);
            yahooFixer.fixFiles();
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initReverse(Parameters parameters) {
        String dataDirectory = parameters.getValue("reverse");
        if(dataDirectory != null) {
            DataLoader loader = new FileSystemDataLoader(output);
            DataSaver saver = new FileSystemDataSaver(output);
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

    private void initSimulation(Parameters parameters) throws IllegalAccessException {
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all arguments processed: " + parameters.getUnconsumed());
            setError(ErrorCode.UNKNOWN_ARGUMENT);
            return;
        }
        Simulation simulation = new Simulation(output);
        MainSettings settings = input.getSettings();
        MainAppData data = input.getData(settings.dataDirectory);
        generateMissingData(settings, data);
        MainInterface.SessionResult sessionResult = (session != null) ? session.result : null;
        simulation.start(settings, data, sessionResult);
        setError(ErrorCode.NO_ERROR);
    }
    
    private void generateMissingData(MainSettings settings, MainAppData data) {
        if (settings.requireSymmetricalRobots) {
            Collection<DataSet> loadedSets = data.getDataSets();
            DataSet[] loadedSetsCopy = loadedSets.toArray(new DataSet[data.getDataSets().size()]);
            for (DataSet loadedSet : loadedSetsCopy) {
                Reversal reversal = new Reversal(loadedSet);
                if (reversal.addReversedDataSetTo(data)) {
                    if (!settings.dataDirectory.isEmpty()) {
                        DataSaver saver = DataFactory.getDefaultSaver(output);
                        saver.save(reversal.getReversedDataSet());
                    }
                }
            }
        }
    }
}
