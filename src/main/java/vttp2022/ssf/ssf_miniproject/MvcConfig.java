package vttp2022.ssf.ssf_miniproject;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("analyzed-images", registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(dirName);
        String uploadPath = uploadDir.toFile().getAbsolutePath();
        System.out.println("MvcConfig: exposeDirectory - " + uploadPath);
        if (dirName.startsWith("../")) dirName = dirName.replace("../", "");
        System.out.println("MvcConfig: exposeDirectory - dirName - " + dirName);
        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:"+ uploadPath + "/");
    }
}