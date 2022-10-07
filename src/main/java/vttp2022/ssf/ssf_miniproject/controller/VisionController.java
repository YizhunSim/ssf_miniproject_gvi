package vttp2022.ssf.ssf_miniproject.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.LocationInfo;

import vttp2022.ssf.ssf_miniproject.FileUploadUtil;
import vttp2022.ssf.ssf_miniproject.models.Position;
import vttp2022.ssf.ssf_miniproject.models.Role;
import vttp2022.ssf.ssf_miniproject.models.User;
import vttp2022.ssf.ssf_miniproject.services.UserService;

@Controller
public class VisionController {
  public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";
  @Autowired
  private ResourceLoader resourceLoader;

  @Autowired
  private CloudVisionTemplate cloudVisionTemplate;

  @Autowired
  private UserService userService;


@Value("${app.googlemap.apikey}")
  private String googleAPIKey;

  @GetMapping ("/uploadimage")
  public String displayCloudVisionAPIPage(){
    return "customers/cloudvisionapi";
  }

   @PostMapping("/getImageDetection")
    public String uploadGetImageDetectionReadings(@RequestParam String api_type, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile file, Model model) throws IOException {
        System.out.println(file.getOriginalFilename());
        System.out.println("api_type: " + api_type);

        List<EntityAnnotation> landmarkAnnotationResults = null;
        List<EntityAnnotation> labelAnnotationResults = null;
        // List<FaceAnnotation> faceAnnotationResults = null;
        if (!file.isEmpty()){
            String imagePath = StringUtils.cleanPath(file.getOriginalFilename());

            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMDD");
            System.out.println(formatter.format(date));

            User currentUser = userService.getByEmail(userService.getLoggedInUser()) ;
            String currentUserId = currentUser.getId();
            System.out.println("Current User: " + currentUser.getFirstName() + " " + currentUser.getLastName());
            String uploadDir = "analyzed-images/" + currentUserId;

            String uniqueImagePath = formatter.format(date) + "_" + imagePath ;
            String path = FileUploadUtil.saveFile(uploadDir, uniqueImagePath, file);
            System.out.println("Path: " + path);

            Resource imageResource = this.resourceLoader.getResource("file:" + path);
            Feature.Type type;
            AnnotateImageResponse response;
            String textDetectionResults = "";
            switch(api_type){
            //   case "FACE_DETECTION":
            //         type = Feature.Type.FACE_DETECTION;
            //         response = this.cloudVisionTemplate.analyzeImage(imageResource,
			// 	        type);
            //         faceAnnotationResults = response.getFaceAnnotationsList();
            //         System.out.println("Face Detection Result: " + faceAnnotationResults);
            //         break;
              case "LANDMARK_DETECTION":
                    type = Feature.Type.LANDMARK_DETECTION;
                    response = this.cloudVisionTemplate.analyzeImage(imageResource,
                        type);
                    landmarkAnnotationResults = response.getLandmarkAnnotationsList();
                    System.out.println("Landmark Detection Result: " + landmarkAnnotationResults);
                    break;
            //   case "LOGO_DETECTION":
            //         type = Feature.Type.LOGO_DETECTION;
            //         response = this.cloudVisionTemplate.analyzeImage(imageResource,
            //             type);
            //         entityAnnotationResults = response.getLogoAnnotationsList();
            //         System.out.println("Logo Detection Result: " + entityAnnotationResults);
            //         break;
              case "LABEL_DETECTION":
                    type = Feature.Type.LABEL_DETECTION;
                    response = this.cloudVisionTemplate.analyzeImage(imageResource,
				        type);
                    labelAnnotationResults = response.getLabelAnnotationsList();
                    System.out.println("Label Detection Result: " + labelAnnotationResults);
                    break;
              case "TEXT_DETECTION":
                    type = Feature.Type.TEXT_DETECTION;
                    textDetectionResults = this.cloudVisionTemplate.extractTextFromImage(imageResource);
                    System.out.println("Text Detection Result: " + textDetectionResults);
                    break;
              default:
                  type = Feature.Type.UNRECOGNIZED;
            }

            if (landmarkAnnotationResults != null){
                List<Position> landmarkPositions = new ArrayList<>();
                System.out.println("-------------");
                for (EntityAnnotation ea : landmarkAnnotationResults){
                    List<LocationInfo> locationInfos = ea.getLocationsList();
                    for (LocationInfo locationInfo : locationInfos){
                       System.out.println(locationInfo);
                       System.out.println(locationInfo.getLatLng());
                       String googleMapInitialiseUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + locationInfo.getLatLng().getLatitude() + "," + locationInfo.getLatLng().getLongitude() + "&zoom=14&size=400x400&markers=size:mid%7Ccolor:red%7C" +locationInfo.getLatLng().getLatitude() + "," + locationInfo.getLatLng().getLongitude() + "&key="+ googleAPIKey;
                       Position p = new Position(ea.getDescription(), ea.getScore(), locationInfo.getLatLng().getLatitude(), locationInfo.getLatLng().getLongitude(), googleMapInitialiseUrl);
                       landmarkPositions.add(p);
                    }
                }
                // System.out.println(googleMapInitialiseUrl);
                // redirectAttributes.addFlashAttribute("googleMapUrl", googleMapInitialiseUrl);
                redirectAttributes.addFlashAttribute("imagePath", "/" + uploadDir + "/" + uniqueImagePath);
                redirectAttributes.addFlashAttribute("landmarkAnnotationResults", landmarkAnnotationResults);
                 redirectAttributes.addFlashAttribute("landmarkPositions", landmarkPositions);
                redirectAttributes.addFlashAttribute("message", "Image has been uploaded successfully and processed with Google Vision API.");
            }
            else if (labelAnnotationResults != null){
                System.out.println("-------------");
                for (EntityAnnotation ea : labelAnnotationResults){
                    System.out.println(ea);
                }
                redirectAttributes.addFlashAttribute("imagePath", "/" + uploadDir + "/" + uniqueImagePath);
                redirectAttributes.addFlashAttribute("labelAnnotationResults", labelAnnotationResults);
                redirectAttributes.addFlashAttribute("message", "Image has been uploaded successfully and processed with Google Vision API.");
            }
            //  else if (faceAnnotationResults != null){
            //     for (FaceAnnotation fa : faceAnnotationResults){
            //         System.out.println(fa);
            //     }
            //      model.addAttribute("analyzedResult", faceAnnotationResults);
            // }
             else if (!textDetectionResults.isEmpty()){
                System.out.println("-------------");
                System.out.println(textDetectionResults);
                String[] texts = textDetectionResults.split("[\\n\\s]");
                System.out.println(texts.length);
                for (String t : texts){
                      System.out.println(t);
                }

                redirectAttributes.addFlashAttribute("imagePath", "/"+uploadDir+"/"+uniqueImagePath);
                redirectAttributes.addFlashAttribute("textDetectionResults", texts);
                redirectAttributes.addFlashAttribute("message", "Image has been uploaded successfully and processed with Google Vision API.");
            } else{
                System.out.println("No Results Found");
                redirectAttributes.addFlashAttribute("message", "Image has been uploaded successfully and processed with Google Vision API -- [No Results Found!]");
            }
        }

        return "redirect:/uploadimage";
    }
}
