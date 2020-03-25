package sample;

import java.nio.file.Path;

public class FileUtils {
    public static String getExtensionIcon(Path fileName) {

        String iconPath = "";
        String extension = "";
        int i = fileName.toString().lastIndexOf('.');

        if (i > 0) {
            extension = fileName.toString().substring(i+1);
        }

        switch (extension) {
            case "txt":
                iconPath = "assets/icons/txt.png";
                break;
            case "doc": case "docx":
                iconPath = "assets/icons/doc.png";
                break;
            case "xls": case "xlxs":
                iconPath = "assets/icons/xls.png";
                break;
            case "ppt":
                iconPath = "assets/icons/ppt.png";
                break;
            case "pdf":
                iconPath = "assets/icons/pdf.png";
                break;
            case "mp4":
                iconPath = "assets/icons/mp4.png";
                break;
            case "avi":
                iconPath = "assets/icons/avi.png";
                break;
            case "mp3":
                iconPath = "assets/icons/mp3.png";
                break;
            case "jpg": case "jpeg":
                iconPath = "assets/icons/jpg.png";
                break;
            case "png":
                iconPath = "assets/icons/png.png";
                break;
            case "html":
                iconPath = "assets/icons/html.png";
                break;
            case "php":
                iconPath = "assets/icons/php.png";
                break;
            case "js":
                iconPath = "assets/icons/js.png";
                break;
            case "xml":
                iconPath = "assets/icons/xml.png";
                break;
            case "dll":
                iconPath = "assets/icons/dll.png";
                break;
            case "ps":
                iconPath = "assets/icons/ps.png";
                break;
            case "zip": case "rar": case "gz":
                iconPath = "assets/icons/zip.png";
                break;
            case "":
                iconPath = "assets/icons/folder.png";
                break;
            default:
                iconPath = "assets/icons/file.png";
                break;

        }

        return iconPath;
    }
}
