package nl.ibridge.wso2client;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class Publisher {

    private final Client client;
    private final WebTarget publisherEndpoint;
    private final WebTarget loginResource;
    private final WebTarget itemListResource;
    private final WebTarget addResource;
    private final WebTarget removeResource;

    private Map<String, Cookie> loginCookies = new HashMap<>();

    public Publisher(final String endpoint) {
        client = ClientBuilder.newClient().register(JsonContentTypeResponseFilter.class);
        publisherEndpoint = client.target(endpoint);
        loginResource = publisherEndpoint.path("/site/blocks/user/login/ajax/login.jag");
        itemListResource = publisherEndpoint.path("/site/blocks/listing/ajax/item-list.jag");
        addResource = publisherEndpoint.path("/site/blocks/item-add/ajax/add.jag");
        removeResource = publisherEndpoint.path("/site/blocks/item-add/ajax/remove.jag");
    }

    public void login(final String username, final String password) throws ClientException {
        Response loginResponse = handleError(loginResource
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.form(new Form()
                        .param("action", "login")
                        .param("username", username)
                        .param("password", password))));
        loginCookies = new HashMap<String, Cookie>(loginResponse.getCookies());
    }

    protected Response handleError(Response response) throws ClientException {
        if (Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            handleError(response.readEntity(JsonObject.class));
            return response;
        } else {
            throw new ClientException(response.toString());
        }
    }

    protected JsonObject handleError(JsonObject response) throws ClientException {
        if (response.getBoolean("error")) {
            throw new ClientException(response.getString("message"));
        }
        return response;
    }

    public JsonObject getAllApis() throws ClientException {
        return handleError(itemListResource
                .queryParam("action", "getAllAPIs")
                .request(MediaType.APPLICATION_JSON)
                .cookie(loginCookies.get("JSESSIONID"))
                .get(JsonObject.class));
    }

    public Response addApi(final String name, final String provider, final String context, final String version, final String description, final String owner) throws ClientException {
        return handleError(addResource
                .request(MediaType.APPLICATION_JSON)
                .cookie(loginCookies.get("JSESSIONID"))
                .post(Entity.form(new Form()
                        .param("action", "addAPI")
                        .param("name", name)
                        .param("provider", provider)
                        .param("context", context)
                        .param("version", version)
                        .param("tier", "Unlimited")
                        .param("transports", "http")
                        .param("http_checked", "http")
                        .param("transports", "https")
                        .param("https_checked", "https")
                        .param("description", description)
                        .param("visibility", "public API")
                        .param("tags", owner)
                        .param("resourceCount", "0")
                        .param("resourceMethod-0", "GET,POST")
                        .param("resourceMethodAuthType-0", "None,None")
                        .param("uriTemplate-0", "/*")
                        .param("resourceMethodThrottlingTier-0", "Unlimited,Unlimited")
                        .param("tiersCollection", "Unlimited")
                )));
    }

    public Response removeApi(final String name, final String provider, final String version) throws ClientException {
        return handleError(removeResource
                .request(MediaType.APPLICATION_JSON)
                .cookie(loginCookies.get("JSESSIONID"))
                .post(Entity.form(new Form()
                        .param("action", "removeAPI")
                        .param("name", name)
                        .param("provider", provider)
                        .param("version", version)
                )));
    }

    public void logout() throws ClientException {
        handleError(loginResource
                .request(MediaType.APPLICATION_JSON)
                .cookie(loginCookies.get("JSESSIONID"))
                .post(Entity.form(new Form()
                        .param("action", "logout"))));
        loginCookies = new HashMap<>();
    }

    public static void main(String... args) throws Exception {
        XTrustProvider.install();
        Publisher publisher = new Publisher("https://hpx-nxt-fin-app.nl.rsg/publisher");
        try {
            publisher.login("admin", "admin");
            System.out.println(publisher.getAllApis());
            publisher.addApi("filetransferservice", "admin", "/kim/services/filetransferservice", "1", "", "KIM");
            System.out.println(publisher.getAllApis());
            publisher.removeApi("filetransferservice", "admin", "1");
        } finally {
            publisher.logout();
        }
    }
}
