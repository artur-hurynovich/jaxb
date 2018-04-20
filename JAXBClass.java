package jaxb;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.function.Consumer;
@XmlRootElement(name = "cars")
class Cars {
    @XmlElement(name = "car")
    private ArrayList<Car> cars;
    Cars() {
        cars = new ArrayList<>();
    }
    void add(Car car) {
        cars.add(car);
    }
    void forEach(Consumer<? super Car> consumer) {
        cars.forEach(consumer);
    }
    @Override
    public String toString() {
        return cars.toString();
    }
}
@XmlRootElement(name = "car")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"mark", "model", "techChar"})
class Car {
    private String mark;
    private String model;
    private TechChar techChar;
    @XmlRootElement(name = "characteristics")
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"date", "engineCapacity", "engineType"})
    public static class TechChar {
        private XMLGregorianCalendar date;
        private double engineCapacity;
        private String engineType;
        void setDate(LocalDate date) {
            try {
                this.date = DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
            }
            catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        void setEngineCapacity(double engineCapacity) {
            this.engineCapacity = engineCapacity;
        }
        void setEngineType(String engineType) {
            this.engineType = engineType;
        }
        @Override
        public String toString() {
            return date.toGregorianCalendar().toZonedDateTime().toLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) + ", " + engineCapacity + " (" + engineType + ")";
        }
    }
    void setMark(String mark) {
        this.mark = mark;
    }
    void setModel(String model) {
        this.model = model;
    }
    void setTechChar(TechChar techChar) {
        this.techChar = techChar;
    }
    TechChar getTechChar() {
        return new TechChar();
    }
    @Override
    public String toString() {
        return mark + " " + model + ": " + techChar.toString();
    }
}
class CarBuilder {
    static Car buildCar(String mark, String model, LocalDate date, double engineCapacity, String engineType) {
        Car car = new Car();
        car.setMark(mark);
        car.setModel(model);
        Car.TechChar techChar = car.getTechChar();
        techChar.setDate(date);
        techChar.setEngineCapacity(engineCapacity);
        techChar.setEngineType(engineType);
        car.setTechChar(techChar);
        return car;
    }
}
public class JAXBClass {
    public static void main(String[] args) {
        JAXBContext context;
        File xmlFile = new File("cars.xml");
        Cars cars = new Cars();
        cars.add(CarBuilder.buildCar("Audi", "Q7", LocalDate.of(2016, 5, 10),
                3.0, "Diesel"));
        cars.add(CarBuilder.buildCar("BMW", "X5", LocalDate.of(2010, 7, 4),
                4.4, "Gasoline"));
        cars.add(CarBuilder.buildCar("Porsche", "911", LocalDate.of(2005, 1, 12),
                5.0, "Gasoline"));
        cars.add(CarBuilder.buildCar("Volkswagen", "Passat", LocalDate.of(1989, 9, 2),
                1.6, "Diesel"));
        Cars newCars = new Cars();
        try {
            context = JAXBContext.newInstance(Cars.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(cars, xmlFile);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            newCars = (Cars) unmarshaller.unmarshal(xmlFile);
        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
        if (newCars != null) {
            newCars.forEach(System.out::println);
        }
    }
}
