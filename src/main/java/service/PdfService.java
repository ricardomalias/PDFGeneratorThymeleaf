package service;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import com.lowagie.text.DocumentException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresource.UrlTemplateResource;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PdfService {
    private static final String HTML = "src/main/resources/template_pf.html";
//    private static final Logger LOGGER = LoggerFactory.getLogger(PdfService.class);

    public static void main(String[] args) throws IOException, DocumentException {
        PdfService thymeleaf2Pdf = new PdfService();

        for(int i = 0; i < 100; i++) {
            long time = System.nanoTime();

            String html = thymeleaf2Pdf.parseThymeleafTemplate();
            thymeleaf2Pdf.generatePdfFromHtml(html);
            long algo = System.nanoTime() - time;
//            LOGGER.info("Tempo: {} ", String.valueOf(algo / 1000000000.0));
            System.out.println("execução: " + i + " Tempo: " + String.valueOf(algo / 1000000000.0));
        }

    }

    public void generatePdfFromHtml(String html) throws IOException, DocumentException {
        Path resourceDirectory = Paths.get("src", "main", "resources");
        String outputFolder = resourceDirectory.toFile().getAbsolutePath() + "/output.pdf";
        OutputStream outputStream = new FileOutputStream(outputFolder);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();

        renderer.createPDF(outputStream);

        outputStream.close();
//        try {
//            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//            InputStream is = new ByteArrayInputStream(FileUtils.readFileToByteArray(new File(resourceDirectory.toFile().getAbsolutePath() + "/template_pf.html")));
//            Document doc = builder.parse(is);
//            is.close();
//            renderer.setDocument(doc, null);
//
//            renderer.layout();
//
//            renderer.createPDF(outputStream);
//            outputStream.close();
//
//        } catch (ParserConfigurationException | SAXException e) {
//            e.printStackTrace();
//        }
    }

    private String parseThymeleafTemplate() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Path path = Paths.get("src", "main", "resources", "img", "cabecalho_largo.png");
        String s = String.valueOf(path.toFile().getAbsoluteFile());
        String base64Image = convertToBase64(s);

        String cabecalho = "data:image/png;base64, " + base64Image;

        Context context = new Context();
        context.setVariable("nome", "Teste de novo");
        context.setVariable("year", "2020");
        context.setVariable("cabecalho", cabecalho);

        return templateEngine.process("template_pf", context);
    }

    private String convertToBase64(String path) {
        try {
            BufferedImage read = ImageIO.read(new File(path));
            WritableRaster raster = read.getRaster();
            DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();

            return Base64.getEncoder().encodeToString(dataBuffer.getData());

//            Resource resource = new UrlTemplateResource(path.toFile().getAbsoluteFile());
//            InputStream inputStream = resource.getInputStream();
//            imageAsBytes = IOUtils.toByteArray(inputStream);

        } catch (IOException e) {
            System.out.println("\n File read Exception");
        }

        return "";
    }
}
