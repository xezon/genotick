package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.MainSettings;

public class FilterSettings {
    
    final FilterOption filterOption;
    // TODO add bit mask for filter columns
    // TODO add settings for filter specific variables
    
    public FilterSettings() {
        filterOption = FilterOption.NONE;
    }
    
    public FilterSettings(final MainSettings settings) {
        filterOption = settings.filterOption;
    }
}
