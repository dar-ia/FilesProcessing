import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileProcessing {
    private ClassLoader cl = FileProcessing.class.getClassLoader();

    public InputStream parseZipFile(String fileName) throws IOException {
        ZipInputStream is = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("file_sample.zip"))
        );
        ZipEntry zipEntry;
        while ((zipEntry = is.getNextEntry()) != null) {
            if (zipEntry.getName().equals(fileName)) {

                return is;
            }
        }
        return null;
    }

    @ParameterizedTest(name = "PDF file validation {0}")
    @ValueSource(strings =
            {"pdf_file_sample.pdf"}
    )
    void validatePDFInZipContent(String fileName) throws IOException {
        try (InputStream is = parseZipFile(fileName)) {
            if (is != null) {
                PDF pdf = new PDF(is);
                Assertions.assertTrue(pdf.numberOfPages > 0);
                Assertions.assertEquals("Sunny Farm Invoice Sample", pdf.title);
                Assertions.assertFalse(pdf.text.isEmpty());

            } else throw new IOException("No such PDF file in archive (" + fileName + ")");

        }

    }

    @ParameterizedTest(name = "CSV file validation {0}")
    @ValueSource(strings = {
            "csv_file_sample.csv"
    })
    void validateCSVInZipContent(String fileName) throws IOException, CsvException {
        try (InputStream is = parseZipFile(fileName)) {
            if (is != null) {
                CSVReader csv = new CSVReader(new InputStreamReader(is));
                List<String[]> content = csv.readAll();
                Assertions.assertTrue(content.size() > 1);
            } else throw new IOException("No such CSV file in archive (" + fileName + ")");
        }
    }

    @ParameterizedTest(name = "XLSX file validation {0}")
    @ValueSource(strings = {
            "xlsx_file_sample.xlsx"
    })
    void validateXLSXInZipContent(String fileName) throws IOException {
        try (InputStream is = parseZipFile(fileName)) {
            if (is != null) {
                XLS xlsx = new XLS(is);
                Assertions.assertTrue(xlsx.excel.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() == 8);
                Assertions.assertFalse(xlsx.excel.getSheetAt(0).getRow(1).toString().isEmpty());

            } else throw new IOException("No such XLSX file in archive (" + fileName + ")");
        }
    }

    @Test
    @DisplayName("Json Validation")
    void validateJson() throws IOException {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(cl.getResourceAsStream("object.json")))
        ) {
            ObjectMapper object = new ObjectMapper();
            object.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            User user = object.readValue(reader, User.class);
            Assertions.assertEquals("User", user.getFirstName());
            Assertions.assertTrue(user.getUserContactMethods().length > 0);
            Assertions.assertEquals("8-(111)-333-4444",
                    user.getUserContactMethodByType("Phone").getName());

        }
    }


}
