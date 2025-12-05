package com.xypha.onlineBus.token.module;

import java.util.List;
import java.util.Map;

public class ModulePermissions {
    private Map<String, List<String>> modules;


    public Map<String, List<String>> getModules() {
        return modules;
    }

    public void setModules(Map<String, List<String>> modules) {
        this.modules = modules;
    }
}
