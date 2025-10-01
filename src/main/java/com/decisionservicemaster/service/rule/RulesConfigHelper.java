package com.decisionservicemaster.service.rule;

import java.util.Map;

/**
 * Helper class to extract state/county specific config from YAML
 */
public class RulesConfigHelper {
    
    public static Map<String, Object> getConfig(Map<String, Object> allConfigs, String state, String county) {
        if (allConfigs == null || state == null) {
            return Map.of();
        }
        
        Map<String, Object> stateConfig = (Map<String, Object>) allConfigs.get(state);
        if (stateConfig == null) {
            return Map.of();
        }
        
        // Check for county-specific config
        if (county != null && stateConfig.containsKey("counties")) {
            Map<String, Object> counties = (Map<String, Object>) stateConfig.get("counties");
            if (counties != null && counties.containsKey(county)) {
                Map<String, Object> countyConfig = (Map<String, Object>) counties.get(county);
                if (countyConfig != null && !countyConfig.isEmpty()) {
                    return countyConfig;
                }
            }
        }
        
        // Return state-level config (excluding counties key)
        return stateConfig;
    }
}