import CarModel.Airbag;
import CarModel.Car;
import CarModel.CarUpdate;
import com.google.gson.Gson;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import spark.Request;
import spark.Response;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

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
        get("/invoice", app::invoice);
        get("/faktury/:id", app::downloadInvoice);
        put("/update", app::update);
        get("/generate", app::generate);
        get("/deleteAll", App::testFunction);
        get("/delete/:id", App::testFunction);
        get("/update/:id", App::testFunction);

    }

    private Object downloadInvoice(Request request, Response response) throws IOException {
        response.type("application/octet-stream"); //
        response.header("Content-Disposition", "attachment; filename=plik.pdf"); // nagłówek
        String id = request.params("id");
        OutputStream outputStream = response.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of("faktury/"+id)));
        return outputStream;
    }

    private Object invoice(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));

        Optional<Car> carOptional = cars.stream().filter(e -> e.getUuid().equals(uuid)).findFirst();
        if (carOptional.isPresent()) {
            Car car = carOptional.get();
            try {
                Document document = new Document();
                String path = String.format("faktury/%s.pdf", car.getUuid());
                PdfWriter.getInstance(document, new FileOutputStream(path));
                document.open();
                String hexColor = car.getColor();
                int r = Integer.valueOf(hexColor.substring(1, 3), 16);
                int g = Integer.valueOf(hexColor.substring(3, 5), 16);
                int b = Integer.valueOf(hexColor.substring(5, 7), 16);
                Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                Font fontBig = FontFactory.getFont(FontFactory.COURIER, 24, BaseColor.BLACK);
                Font fontSmall = FontFactory.getFont(FontFactory.COURIER, 8, BaseColor.BLACK);
                Font fontSmallColored = FontFactory.getFont(FontFactory.COURIER, 8, 2,new BaseColor(r,g,b));
                Image img = Image.getInstance("dodge2.jpg");
                Chunk chunk = new Chunk("tekst", font);
                document.add( new Paragraph("FAKTURA dla "+car.getUuid(), font));
                document.add( new Paragraph("Model: "+car.getModel(), fontBig));
                document.add( new Paragraph("kolor: "+car.getColor(), fontSmallColored));
                document.add( new Paragraph("rok: "+car.getYear(), fontSmall));
                document.add(chunk);
                document.add(img);
                document.close();
                car.setInvoice(path);
                return path;
            } catch (DocumentException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private Object generate(Request request, Response response) {
        String[] models = {"Toyota", "Mercedes", "BMW", "Volkswagen", "Skoda"};
        for (int i = 0; i < 40; i++) {
            cars.add(new Car(getRandom(models),
                    new Random().nextInt(),
                    new ArrayList<Airbag>(
                            List.of(new Airbag("kierowca", new Random().nextBoolean()),
                                    new Airbag("pasażer", new Random().nextBoolean()),
                                    new Airbag("kanapa", new Random().nextBoolean()),
                                    new Airbag("boczne", new Random().nextBoolean()))),
                    String.format("#%06x", new Random().nextInt(0xffffff + 1))
            ));
        }
        return "";
    }

    private Object findOne(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));
        Optional<Car> car = cars.stream().filter(e -> e.getUuid().equals(uuid)).findFirst();
        return car.<Object>map(value -> gson.toJson(value, Car.class)).orElse(null);
    }

    private Object update(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));

        Optional<Car> carOptional = cars.stream().filter(e -> e.getUuid().equals(uuid)).findFirst();
        if (carOptional.isPresent()) {
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


    private static String getRandom(String[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }


}