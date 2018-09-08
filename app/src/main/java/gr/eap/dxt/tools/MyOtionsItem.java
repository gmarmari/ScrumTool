package gr.eap.dxt.tools;

/**
 * Created by GEO on 21/1/2017.
 */

public class MyOtionsItem {

    private String name;
    public String getName(){
        return name;
    }

    private Integer imageResource;
    public Integer getImageResource() {
        return imageResource;
    }

    private String id;
    public String getId() {
        return id;
    }

    public MyOtionsItem(String name, Integer imageResource, String id){
        this.name = name;
        this.imageResource = imageResource;
        this.id = id;
    }
}
