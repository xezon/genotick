package com.alphatica.genotick.population;

public class PopulationDAOFactory {    
    
    public static PopulationDAO getDefaultDAO(PopulationSettings settings) {
        PopulationDAO dao;
        if (settings.daoOption().contains(PopulationDaoOption.RAM)) {
            dao = new PopulationDAORAM();
        }
        else if (settings.daoOption().contains(PopulationDaoOption.DISK)) {
            dao = new PopulationDAOFileSystem(settings.daoPath());
        }
        else {
            throw new DAOException("Unhandled PopulationDaoOption");
        }
        return dao;
    }
}
