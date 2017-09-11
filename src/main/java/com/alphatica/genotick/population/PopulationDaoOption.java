package com.alphatica.genotick.population;

public enum PopulationDaoOption {
    IMPLICIT(1<<0),
    EXPLICIT(1<<1),
    RAM(1<<2),
    DISK(1<<3),
    IMPLICIT_RAM (IMPLICIT.value() | RAM.value()),
    IMPLICIT_DISK(IMPLICIT.value() | DISK.value()),
    EXPLICIT_RAM (EXPLICIT.value() | RAM.value()),
    EXPLICIT_DISK(EXPLICIT.value() | DISK.value());

    private final int value;

    PopulationDaoOption(int value) {
        this.value = value;
    }

    private int value() {
        return value;
    }

    public boolean contains(PopulationDaoOption option) {
        return (value & option.value()) != 0;
    }

    private static String getIdentifier(PopulationDaoOption option) {        
        String identifier;
        switch (option) {
            case EXPLICIT_RAM: identifier = "ram:"; break;
            case EXPLICIT_DISK: identifier = "disk:"; break;
            default: identifier = ""; break;
        }
        return identifier;
    }

    private static boolean hasIdentifier(String daoSetting, PopulationDaoOption option) {
        return daoSetting.startsWith(getIdentifier(option));
    }

    public static PopulationDaoOption getOption(String daoSetting) {
        PopulationDaoOption option;
        if (daoSetting.isEmpty())
            option = IMPLICIT_RAM;
        else if (hasIdentifier(daoSetting, EXPLICIT_RAM))
            option = EXPLICIT_RAM;
        else if (hasIdentifier(daoSetting, EXPLICIT_DISK))
            option = EXPLICIT_DISK;
        else
            option = IMPLICIT_DISK;
        return option;
    }

    public static String getPath(String daoSetting) {
        PopulationDaoOption option = getOption(daoSetting);
        String identifier = getIdentifier(option);
        return daoSetting.substring(identifier.length());
    }
}
