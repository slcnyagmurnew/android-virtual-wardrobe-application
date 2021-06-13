package com.studio.classes;

public class Combine {

    private String overheadPath;
    private String facePath;
    private String upperPath;
    private String lowerPath;
    private String footPath;
    private String name;
    private String id;

    public Combine(String overheadPath, String facePath, String upperPath, String lowerPath, String footPath, String name, String id) {
        this.overheadPath = overheadPath;
        this.facePath = facePath;
        this.upperPath = upperPath;
        this.lowerPath = lowerPath;
        this.footPath = footPath;
        this.name = name;
        this.id = id;
    }

    public String getOverheadPath() {
        return overheadPath;
    }

    public void setOverheadPath(String overheadPath) {
        this.overheadPath = overheadPath;
    }

    public String getFacePath() {
        return facePath;
    }

    public void setFacePath(String facePath) {
        this.facePath = facePath;
    }

    public String getUpperPath() {
        return upperPath;
    }

    public void setUpperPath(String upperPath) {
        this.upperPath = upperPath;
    }

    public String getLowerPath() {
        return lowerPath;
    }

    public void setLowerPath(String lowerPath) {
        this.lowerPath = lowerPath;
    }

    public String getFootPath() {
        return footPath;
    }

    public void setFootPath(String footPath) {
        this.footPath = footPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
