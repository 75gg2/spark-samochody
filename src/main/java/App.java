import Car.Car;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.ArrayList;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static spark.Spark.*;

class App {
    ArrayList<Car> cars = new ArrayList<>();

    static String testFunction(Request req, Response res) {
        return "test";
    }

    public static void main(String[] args) {
        externalStaticFileLocation("C:\\Users\\4pb\\IdeaProjects\\spark\\src\\main\\resources\\public");
        var app = new App();
        post("/add", app::addPost);
        get("/text", app::text);
        get("/json", app::json);
        get("/deleteall", App::testFunction);
        get("/delete/:id", App::testFunction);
        get("/update/:id", App::testFunction);

    }

    private String addPost(Request request, Response response) {
        Gson gson = new Gson();
        Car car = gson.fromJson(request.body(), Car.class);
        cars.add(car);
        return "test3234";
    }

//    private Object html(Request request, Response response) {
//        StringBuilder s = new StringBuilder();
//        s.append("<table>");
//        s.append(this.cars.stream().map(c ->
//                String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
//                        c.getUuid(),
//                        c.getModel(),
//                        c.getDoors(),
//                        c.getCountry())
//        ));
//        s.append("</table>");
//        return s.toString();
//    }

    private String text(Request request, Response response) {
        Gson gson = new Gson();
        return gson.toJson(this.cars, ArrayList.class);
    }

    private String json(Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(this.cars, ArrayList.class);
    }




}