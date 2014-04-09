package nl.ibridge.wso2client.ant;

import org.apache.tools.ant.types.DataType;

public class Publisher extends DataType {

    private String url;
    private String username;
    private String password;

    protected Publisher getRef() {
        return (Publisher) getCheckedRef();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}