import Car.Car;
import Car.CarUpdate;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static spark.Spark.*;

class App {
    ArrayList<Car> cars = new ArrayList<>();
    Gson gson = new Gson();

    static String testFunction(Request req, Response res) {
        return "test";
    }

    public static void main(String[] args) {
        externalStaticFileLocation("C:\\Users\\4pb\\IdeaProjects\\spark\\src\\main\\resources\\public");
        var app = new App();
        post("/add", app::addPost);
        get("/json", app::json);
        get("/findOne", app::findOne);
        put("/update", app::update);
        get("/deleteAll", App::testFunction);
        get("/delete/:id", App::testFunction);
        get("/update/:id", App::testFunction);

    }

    private Object findOne(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));
        Optional<Car> car = cars.stream().filter(e -> e.getUuid().equals(uuid)).findFirst();
        return car.<Object>map(value -> gson.toJson(value, Car.class)).orElse(null);
    }
    private Object update(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));

        Optional<Car> carOptional = cars.stream().filter(e -> e.getUuid().equals(uuid)).findFirst();
        if(carOptional.isPresent()){
            CarUpdate carUpdate = gson.fromJson(request.body(), CarUpdate.class);
            Car car = carOptional.get();
            car.setModel(carUpdate.model());
            car.setYear(carUpdate.year());
        }

        return false;
    }


    private String addPost(Request request, Response response) {
        Gson gson = new Gson();
        Car car = gson.fromJson(request.body(), Car.class);
        car.generateUUID();
        cars.add(car);

        response.type("application/json");
        return gson.toJson(car);
    }

    private String json(Request request, Response response) {
        response.type("application/json");
        Gson gson = new Gson();
        return gson.toJson(this.cars, ArrayList.class);
    }


}