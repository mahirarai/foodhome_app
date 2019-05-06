package itp341.raihan.mahira.project.model;

import java.io.Serializable;
import java.util.List;

public class Business implements Serializable {

    public String id;
    public String name;
    public String image_url;
    //public List<Location> location;


    @Override
    public String toString() {
        return name;
    }
}
