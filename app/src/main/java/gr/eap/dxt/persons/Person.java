package gr.eap.dxt.persons;

import java.util.ArrayList;

/**
 * Created by GEO on 22/1/2017.
 */

public class Person {

    public static String FIREBASE_LIST = "Persons";

    public Person(){ }

    private String personId;
    public String getPersonId() {
        return personId;
    }
    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public static String EMAIL = "email";
    private String email;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public static String NAME = "name";
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static String ROLE = "role";
    private String role;
    public String getRole(){
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public static Person getCopy(Person person){
        if (person == null) return null;

        Person copy = new Person();
        copy.personId = person.personId;
        copy.email = person.email;
        copy.name = person.name;
        copy.role = person.role;

        return copy;
    }


    public static Person getPersonWithId(String personId, ArrayList<Person> persons){
        if (personId == null || personId.isEmpty()) return null;
        if (persons == null || persons.isEmpty()) return null;

        for (Person person : persons) {
            if (person != null){
                if (person.getPersonId() != null){
                    if (person.getPersonId().equals(personId)) return person;
                }
            }
        }

        return null;
    }
}
