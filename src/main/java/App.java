import CarModel.Airbag;
import CarModel.Car;
import CarModel.CarUpdate;
import Utils.Helpers;
import Utils.Invoice;
import Utils.InvoiceInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.imgscalr.Scalr;
import spark.Request;
import spark.Response;

import javax.imageio.ImageIO;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static spark.Spark.*;

class App {
    ArrayList<Car> cars = new ArrayList<>();
    ArrayList<InvoiceInfo> invoiceOfAll = new ArrayList<>();
    ArrayList<InvoiceInfo> invoicesByYear = new ArrayList<>();
    ArrayList<InvoiceInfo> invoicesByPrice = new ArrayList<>();
    Gson gson = new Gson();

    static String testFunction(Request req, Response res) {
        return "test";
    }

    public static void main(String[] args) {


        externalStaticFileLocation("C:\\Users\\4pb\\IdeaProjects\\spark\\src\\main\\resources\\public");
        externalStaticFileLocation("/home/kamil/IdeaProjects/spark-samochody/src/main/resources/public");
        var app = new App();
        post("/add", app::addPost);
        get("/json", app::json);
        get("/findOne", app::findOne);

        put("/update", app::update);
        get("/generate", app::generate);
        get("/deleteAll", App::testFunction);
        get("/delete/:id", app::delete);
        get("/update/:id", App::testFunction);

        get("/invoice", app::invoice);
        post("/invoiceOfAll", app::invoiceOfAll);
        post("/invoiceByYear", app::invoiceByYear);
        post("/invoiceByPrice", app::invoiceByPrice);
        get("/invoicesFilter", app::getInvoiceFilter);
        get("/faktury/:id", app::downloadInvoice);

        get("/thumb", app::thumb);
        get("/imageInEdit", app::imageInEdit);
        post("/uploadPhoto", app::uploadPhoto);
        post("/savePhotosForCar", app::savePhotosForCar);
        get("/getPhotosByUuid", app::getPhotosByUuid);

        get("/saveImageInEdit", app::saveImageInEdit);
        post("/editPhoto", app::editPhoto);

    }

    private Object delete(Request request, Response response) {
        UUID uuid = UUID.fromString(request.params("id"));
        return cars.remove(cars.stream().filter(car -> car.getUuid().equals(uuid)).findFirst().get());
    }

    private Object saveImageInEdit(Request request, Response response) throws IOException {
        String originPhoto = "images/" + request.queryParams("name");
        String newFile = secureEditPath(originPhoto);
        File oldPhoto = new File(newFile);
        FileInputStream oFile = new FileInputStream(oldPhoto);
        byte[] bytes = oFile.readAllBytes();
        FileOutputStream fos = new FileOutputStream(originPhoto);
        fos.write(bytes);
        fos.close();
        return true;
    }

    private Object editPhoto(Request request, Response response) throws IOException {
        String operation = request.queryParams("operation");
        String originPhoto = "images/" + request.queryParams("name");
        String editablePhoto = secureEditPath(originPhoto);

        switch (operation) {
            case "rotate":
                rotatePhoto(editablePhoto);
                break;
            case "flipH":
                flipPhoto(editablePhoto, true);
                break;
            case "flipV":
                flipPhoto(editablePhoto, false);
                break;

            case "crop":
                crop(editablePhoto,
                        request.queryParams("x1"),
                        request.queryParams("x2"),
                        request.queryParams("y1"),
                        request.queryParams("y2"));
                break;

        }

        File sourceFile = new File(editablePhoto);
        BufferedImage editableImage = ImageIO.read(sourceFile);
        var list = new int[]{editableImage.getWidth(), editableImage.getHeight()};
        return gson.toJson(list);
    }

    private void crop(String editablePhoto, String x1, String x2, String y1, String y2) throws IOException {
        File sourceFile = new File(editablePhoto);
        BufferedImage originalImage = ImageIO.read(sourceFile);

        BufferedImage targetImage = Scalr.crop(originalImage,
                Integer.parseInt(x1.trim()),
                Integer.parseInt(y1.trim()),
                Integer.parseInt(x2.trim()),
                Integer.parseInt(y2.trim())); // x,y,w,h
        File targetFile = new File(editablePhoto);

        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
    }

    private void rotatePhoto(String path) throws IOException {
        File sourceFile = new File(path);
        BufferedImage originalImage = ImageIO.read(sourceFile);

        System.out.println(originalImage.getWidth());
        System.out.println(originalImage.getHeight());

        BufferedImage targetImage = Scalr.rotate(originalImage, Scalr.Rotation.CW_90);

        System.out.println(targetImage.getWidth());
        System.out.println(targetImage.getHeight());

        File targetFile = new File(path);
        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
    }

    private void flipPhoto(String path, boolean H) throws IOException {
        File sourceFile = new File(path);
        BufferedImage originalImage = ImageIO.read(sourceFile);

        BufferedImage targetImage = Scalr.rotate(originalImage, H ? Scalr.Rotation.FLIP_HORZ : Scalr.Rotation.FLIP_VERT);

        File targetFile = new File(path);
        ImageIO.write(targetImage, "jpg", targetFile);

        originalImage.flush();
        targetImage.flush();
    }

    private String secureEditPath(String path) throws IOException {
        String newPath = path.replace(".jpg", "edit.jpg");
        File newPhoto = new File(newPath);
        if (newPhoto.createNewFile()) {
            File oldPhoto = new File(path);
            FileInputStream oFile = new FileInputStream(oldPhoto);
            byte[] bytes = oFile.readAllBytes();
            FileOutputStream fos = new FileOutputStream(newPath);
            fos.write(bytes);
            fos.close();
        }
        return newPath;
    }

    private Object imageInEdit(Request request, Response response) throws IOException {
        String originPhoto = "images/" + request.queryParams("name");
        String path = secureEditPath(originPhoto);

        response.type("image/jpeg");
        OutputStream outputStream = response.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of(path)));
        outputStream.flush();
        return outputStream;
    }

    private Object getPhotosByUuid(Request request, Response response) {
        UUID uuid = UUID.fromString(request.queryParams("uuid"));
        var opCar = cars.stream().filter(car -> car.getUuid().equals(uuid)).findFirst();
        if (opCar.isPresent())
            return gson.toJson(opCar.get().getPhotosPaths());
        return false;
    }

    private Object savePhotosForCar(Request request, Response response) {
        String carId = request.queryParams("id");
        TypeToken<ArrayList<String>> typeToken = new TypeToken<>() {
        };
        ArrayList<String> list = gson.fromJson(request.body(), typeToken.getType());

        Optional<Car> optionalCar = cars.stream()
                .filter(car -> car.getUuid().equals(UUID.fromString(carId)))
                .findFirst();
        optionalCar.ifPresent(car -> car.addPhotosPath(list));
        return true;
    }

    private Object thumb(Request request, Response res) throws IOException {
        String id = "images/" + request.queryParams("name");
        res.type("image/jpeg");
        OutputStream outputStream = res.raw().getOutputStream();

        outputStream.write(Files.readAllBytes(Path.of(id)));
        outputStream.flush();
        return outputStream;
    }

    private Object uploadPhoto(Request request, Response response) throws ServletException, IOException {
        request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/images"));
        System.out.println("plików jest: " + request.raw().getParts().size());
        System.out.println("parts " + request.raw().getParts());
        ArrayList<String> nazwy = new ArrayList<>();
        for (Part p : request.raw().getParts()) {
            System.out.println(p);
            System.out.println(p.getInputStream());
            InputStream inputStream = p.getInputStream();
            // inputstream to byte
            byte[] bytes = inputStream.readAllBytes();
            String fileName = Helpers.randomUUID() + ".jpg";
            FileOutputStream fos = new FileOutputStream("images/" + fileName);
            fos.write(bytes);
            fos.close();
            nazwy.add(fileName);
            // dodaj do Arraylist z nazwami aut do odesłania do przeglądarki
        }
        return gson.toJson(nazwy);
    }

    private Object getInvoiceFilter(Request request, Response response) {
        return gson.toJson(new ArrayList[]{invoicesByYear, invoicesByPrice, invoiceOfAll});
    }

    private Object invoiceByYear(Request request, Response response) {
        int year = Integer.parseInt(request.queryParams("year"));
        String title = "Faktura dla roku " + year + new Date();
        ArrayList<Car> auta = cars.stream().filter(car -> car.getYear() == year).collect(Collectors.toCollection(ArrayList::new));
        invoicesByYear.add(
                new InvoiceInfo(
                        new Invoice(title, "nabywca", "kupiec", auta).generate(),
                        title)
        );
        return true;
    }

    private Object invoiceOfAll(Request request, Response response) {
        String title = "Faktura wszystko " + new Date();
        invoiceOfAll.add(
                new InvoiceInfo(
                        new Invoice(title, "nabywca", "kupiec", cars).generate(),
                        title)
        );
        return true;
    }

    private Object invoiceByPrice(Request request, Response response) {
        int price1 = Integer.parseInt(request.queryParams("price1"));
        int price2 = Integer.parseInt(request.queryParams("price2"));
        String title = String.format("Faktura dla cen w %s-%s", price1, price2) + new Date();
        ArrayList<Car> auta = cars.stream().filter(car -> {
                    double p = car.getPrice();
                    return price1 < p && price2 > p;
                }
        ).collect(Collectors.toCollection(ArrayList::new));
        invoicesByPrice.add(
                new InvoiceInfo(
                        new Invoice(title, "nabywca", "kupiec", auta).generate(),
                        title)
        );
        return true;
    }

    private Object downloadInvoice(Request request, Response response) throws IOException {
        response.type("application/octet-stream"); //
        response.header("Content-Disposition", "attachment; filename=plik.pdf"); // nagłówek
        String id = request.params("id");
        OutputStream outputStream = response.raw().getOutputStream();
        outputStream.write(Files.readAllBytes(Path.of("faktury/" + id)));
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
                Font fontSmallColored = FontFactory.getFont(FontFactory.COURIER, 8, 2, new BaseColor(r, g, b));
                Image img = Image.getInstance("dodge2.jpg");
//                Chunk chunk = new Chunk("tekst", font);
                document.add(new Paragraph("FAKTURA dla " + car.getUuid(), font));
                document.add(new Paragraph("Model: " + car.getModel(), fontBig));
                document.add(new Paragraph("kolor: " + car.getColor(), fontSmallColored));
                document.add(new Paragraph("rok: " + car.getYear(), fontSmall));
                for (Airbag a : car.getAirbags()) {
                    document.add(new Paragraph("poduszka: " + a.airbagType() + "-" + a.active(), fontSmall));
                }
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
                    Helpers.randomYear(),
                    new ArrayList<>(
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
            return true;
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