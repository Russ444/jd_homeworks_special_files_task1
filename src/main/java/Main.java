import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String file1Name = "data.xml";

        List<Employee> list = parseCSV(columnMapping, fileName);
        List<Employee> list1 = parseXML(file1Name);

        String json = listToJson(list);
        String json1 = listToJson(list1);

        String outputFileName = "data.json";
        String output1FileName = "data1.json";
        writeString(json, outputFileName);
        writeString(json1, output1FileName);
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        List<Employee> employees = new ArrayList<>();

        NodeList nodeList = doc.getElementsByTagName("employee");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            parseEmployeeNodes(node, employees);
        }
        return employees;
    }

    public static void parseEmployeeNodes(Node node, List<Employee> employees) {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            Element employee = (Element) node;
            int id = Integer.parseInt(employee.getElementsByTagName("id").item(0).getTextContent());
            String firstName = employee.getElementsByTagName("firstName").item(0).getTextContent();
            String lastName = employee.getElementsByTagName("lastName").item(0).getTextContent();
            String country = employee.getElementsByTagName("country").item(0).getTextContent();
            int age = Integer.parseInt(employee.getElementsByTagName("age").item(0).getTextContent());
            employees.add(new Employee(id, firstName, lastName, country, age));
        }
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName) throws IOException {

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + fileName);
            throw e;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            throw e;
        }
    }

    public static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString (String jsonString, String fileName) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonString);
        }
    }
}
