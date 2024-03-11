package model;

public class User {

    private String id;
    private String firstName;
    private String lastName;
    private UserType userType;

    private UserContactMethod[] userContactMethods;

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public UserType getUserType() {
        return userType;
    }

    public UserContactMethod[] getUserContactMethods() {
        return userContactMethods;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserContactMethod getUserContactMethodByType(String typeName) {
        for (UserContactMethod method:this.userContactMethods) {
            if(method.getMethodTypeName().equals(typeName))
                return method;

        }
        return null;
    }
}
