package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.Debug;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class PopulationDAOFileSystem implements PopulationDAO {
    private static final String FILE_EXTENSION = ".prg";
    private String programsPath = "population";
    private final Random random = new Random();

    @Override
    public void setSettings(String pathToDir) {
        File dirFile = new File(pathToDir);
        if(dirFile.exists())
            return;
        boolean success = dirFile.mkdirs();
        if(!success) {
            throw new DAOException("Unable to create dir: " + pathToDir);
        }
        this.programsPath = pathToDir;
    }

    @Override
    public ProgramName[] listProgramNames() {
        String [] files = listFiles(programsPath);
        ProgramName[] names = new ProgramName[files.length];
        for(int i = 0; i < files.length; i++) {
            String longString = files[i].substring(0,files[i].indexOf('.'));
            names[i] = new ProgramName(Long.valueOf(longString));
        }
        return names;
    }

    @Override
    public Program getProgramByName(ProgramName name) {
        File file = createFileForName(name);
        return getProgramFromFile(file);
    }

    @Override
    public Iterable<Program> getProgramList() {
        return new Iterable<Program>() {
            class ListAvailablePrograms implements Iterator<Program> {
                final private List<ProgramName> names;
                int index = 0;
                ListAvailablePrograms() {
                    names = getAllProgramNames();
                }
                @Override
                public boolean hasNext() {
                    return names.size() > index;
                }

                @Override
                public Program next() {
                    return getProgramByName(names.get(index++));
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("remove() not supported");
                }
            }
            @Override
            public Iterator<Program> iterator() {
                return new ListAvailablePrograms();
            }
        };
    }

    @Override
    public int getAvailableProgramsCount() {
        return getAllProgramNames().size();
    }

    @Override
    public void saveProgram(Program program) {
        if(program.getName() == null) {
            program.setName(getAvailableName());
        }
        //Debug.d("DAO saveProgram:",program.getName());
        File file = createFileForName(program.getName());
        saveProgramToFile(program,file);
    }

    @Override
    public void removeProgram(ProgramName programName) {
        File file = createFileForName(programName);
        boolean result = file.delete();
        //Debug.d("Deleting program:",programName.getName());
        if(!result)
            throw new DAOException("Unable to remove file " + file.getAbsolutePath());
    }

    private List<ProgramName> getAllProgramNames() {
        List<ProgramName> list = new ArrayList<>();
        String [] fileList = listFiles(programsPath);
        if(fileList == null)
            return list;
        for(String name: fileList) {
            String shortName = name.split("\\.")[0];
            Long l = Long.parseLong(shortName);
            list.add(new ProgramName(l));
        }
        return list;
    }

    private String [] listFiles(String dir) {
        File path = new File(dir);
        return path.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });
    }

    private ProgramName getAvailableName() {
        File file;
        long l;
        do {
            l = Math.abs(random.nextLong());
            file = new File(programsPath + String.valueOf(l) + FILE_EXTENSION);
        } while (file.exists());
        return new ProgramName(l);
    }

    private Program getProgramFromFile(File file) {
        try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream( new FileInputStream(file)))) {
            return (Program) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new DAOException(e);
        }
    }

    private File createFileForName(ProgramName name) {
        return new File(programsPath + File.separator + name.toString() + FILE_EXTENSION);
    }

    private void saveProgramToFile(Program program, File file)  {
        deleteFileIfExists(file);
        try(ObjectOutputStream ous = new ObjectOutputStream(new BufferedOutputStream( new FileOutputStream(file)))) {
            ous.writeObject(program);
        } catch (IOException ex) {
            throw new DAOException(ex);
        }
    }

    private void deleteFileIfExists(File file) {
        if(!file.exists())
            return;
        if(!file.delete()) {
            throw new DAOException("Unable to delete file: " + file);
        }
    }


}
